package nl.loxia.builder.generator.ap;

import static java.util.stream.Collectors.toList;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import freemarker.template.TemplateException;
import nl.loxia.builder.generator.annotations.Builder;

/**
 * The core of the builder generator framework.
 *
 * @author Ben Zegveld
 */
public class BuilderGenerator {

    private static final String BUILDER_SUFFIX = "Builder";

    private final Messager messager;
    private final Type typeElement;
    private final TypeUtils typeUtils;
    private final EnvironmentConfiguration environmentConfiguration;

    private final Collection<String> actions = new ArrayList<>();

    /**
     * the constructor.
     *
     * @param environmentConfiguration - contains the configuration supplied to the compiler
     * @param types - Utility class for working with Types.
     * @param elements - Utility class for working with Elements.
     * @param messager - Feedback endpoint for communication errors/warnings to the java compiler
     * @param typeElement - The current element for which a builder needs to be generated.
     */
    public BuilderGenerator(EnvironmentConfiguration environmentConfiguration, Types types, Elements elements,
            Messager messager, TypeElement typeElement) {
        this.environmentConfiguration = environmentConfiguration;
        typeUtils = new TypeUtils(types, elements);
        this.messager = messager;
        this.typeElement = new Type(typeElement);
    }

    /**
     * The method to actually generate the builder.
     *
     * @param filer - compiler utility to register newly created files.
     * @param freeMarkerWriter - the writer used to write the builder java file.
     */
    public void generate(Filer filer, FreeMarkerWriter freeMarkerWriter) {
        actions.add("generating builder for " + typeElement.getQualifiedName());
        BuilderData builderData = createBuilderData(typeElement);

        if (!builderData.isValid()) {
            messager.printMessage(ERROR, String.format("Cannot generate builder. %s", builderData.getValidationError()),
                typeElement.getTypeElement());
            return;
        }

        try {
            actions.add("writing builder class");
            String fileName = typeElement.getQualifiedName() + BUILDER_SUFFIX;
            JavaFileObject sourceFile = filer.createSourceFile(fileName, typeElement.getTypeElement());
            freeMarkerWriter.write(sourceFile, builderData);
        }
        catch (IOException | TemplateException e) {
            throw new GenerationException(e);
        }
    }

    /**
     * @param typeElement - The type annotated with the {@link Builder} annotation
     * @return All data required to generate a builder class.
     */
    private BuilderData createBuilderData(Type typeElement) {
        BuilderConfiguration builderConfiguration = new BuilderConfiguration(environmentConfiguration, typeElement);
        BuilderData builderData = BuilderData.builder()
            .setAbstract(typeElement.isAbstract())
            .setBuilderClassName(typeElement.getSimpleName() + BUILDER_SUFFIX)
            .setBuilderConfiguration(builderConfiguration)
            .setExtendedBuilderName(getExtendedBuilderName(typeElement))
            .setPackageName(typeUtils.getPackageName(typeElement.getTypeElement()))
            .setSourceClassName(asGenerationType(typeElement.getTypeElement().asType()))
            .build();

        updateBuilderDataWithHierarchicalInformation(typeElement, builderData, builderConfiguration);
        updateBuilderDataWithConstructorInformation(typeElement, builderData, builderConfiguration);
        return builderData;
    }

    private void updateBuilderDataWithConstructorInformation(Type typeElement, BuilderData builderData,
            BuilderConfiguration builderConfiguration) {
        String constructorInformationAction = "updating builder information with constructor details";
        actions.add(constructorInformationAction);
        List<TypeMember> constructorArguments = typeElement.getSmallestConstructorArguments();
        if (constructorArguments.size() == 1) {
            TypeMember parameter = constructorArguments.get(0);
            if (isProbablyBuilderClass(typeElement, parameter)) {
                builderData.setBuilderPassingConstructor(true);
            }
            else {
                builderData.addConstructorArgumentName(parameter.getPropertyName());
                builderData.addMember(createMember(builderConfiguration, typeElement, typeElement, parameter));
            }
        }
        else {
            for (TypeMember parameter : constructorArguments) {
                builderData.addConstructorArgumentName(parameter.getPropertyName());
                builderData.addMember(createMember(builderConfiguration, typeElement, typeElement, parameter));
            }
        }
        actions.remove(constructorInformationAction);
    }

    private boolean isProbablyBuilderClass(Type typeElement, TypeMember parameter) {
        return determineClassName(parameter).equals(typeElement.getSimpleName() + BUILDER_SUFFIX)
            || parameter.isOfAnUnavailableType();
    }

    private String determineClassName(TypeMember parameter) {
        return parameter.getPropertyType().toString();
    }

    private void updateBuilderDataWithHierarchicalInformation(Type typeElement, BuilderData builderData,
            BuilderConfiguration builderConfiguration) {
        String memberInformationActions = "updating builder information with members";
        actions.add(memberInformationActions);
        for (Type currentElement : getTypeElementHierarchy(typeElement)) {
            for (TypeMember typeMember : currentElement.getEnclosedElements()) {
                String memberInformationAction = "updating builder information with " + typeMember.getSimpleName();
                actions.add(memberInformationAction);
                if (typeMember.isField()) {
                    builderData.addMember(createMember(builderConfiguration, typeElement, currentElement, typeMember));
                }
                else if (typeMember.isSetterMethod()) {
                    builderData.addMember(createMember(builderConfiguration, typeElement, currentElement, typeMember));
                }
                else if (isListGetterMethod(typeMember)) {
                    builderData.addMember(createMember(builderConfiguration, typeElement, currentElement, typeMember));
                }
                else if (typeMember.isClass() && currentElement == typeElement) {
                    builderData.addInnerClass(createBuilderData(typeMember.asType()));
                }
                actions.remove(memberInformationAction);
            }
        }
        actions.remove(memberInformationActions);
    }

    private Member createMember(BuilderConfiguration builderConfiguration, Type typeElement, Type currentElement,
            TypeMember enclosedEle) {
        TypeMirror propertyType = enclosedEle.getPropertyType();
        Member.Builder memberBuilder = Member.builder()
            .type(asGenerationType(propertyType))
            .outerTypes(getSurroundingClassesForGeneration(propertyType))
            .name(enclosedEle.getPropertyName())
            .setBuilderMethod(
                determineBuilderMethod(enclosedEle, builderConfiguration.getMethodPrefix()))
            .hasGetter(hasGetter(typeElement, enclosedEle))
            .hasSetter(hasSetter(typeElement, enclosedEle))
            .inherited(currentElement != typeElement && currentElement.hasBuilderAnnotation())
            .javadoc(determineJavadoc(enclosedEle));
        if (typeUtils.isList(propertyType)) {
            TypeMirror subType = typeUtils.getSubType(propertyType);
            memberBuilder
                .isAbstract(typeUtils.isAbstract(subType))
                .subType(asGenerationType(subType))
                .outerTypes(getSurroundingClassesForGeneration(subType))
                .hasBuilder(typeHasBuilderAnnotation(subType))
                .subBuilderClassName(determineSubBuilderClassName(subType));
            addAliases(memberBuilder, subType, typeElement);
        }
        else {
            memberBuilder.isAbstract(typeUtils.isAbstract(propertyType))
                .hasBuilder(typeHasBuilderAnnotation(propertyType));
        }
        return memberBuilder.build();
    }

    private String determineJavadoc(TypeMember enclosedEle) {
        return typeUtils.getJavadoc(enclosedEle);
    }

    private List<GenerationType> getSurroundingClassesForGeneration(TypeMirror propertyType) {
        return typeUtils.getSurroundingClasses(propertyType).stream().map(this::asGenerationType).collect(toList());
    }

    private GenerationType asGenerationType(TypeMirror propertyType) {
        GenerationType subType = null;
        if (typeUtils.isList(propertyType)) {
            subType = asGenerationType(typeUtils.getSubType(propertyType));
        }
        return new GenerationType(propertyType, typeUtils.getPackageName(propertyType), subType);
    }

    private boolean hasSetter(Type typeElement, TypeMember enclosedEle) {
        return hasMethodInClassHierarchy(Type::hasSetterFor, typeElement, enclosedEle);
    }

    private boolean hasGetter(Type typeElement, TypeMember enclosedEle) {
        return hasMethodInClassHierarchy(Type::hasGetterFor, typeElement, enclosedEle);
    }

    private boolean hasMethodInClassHierarchy(BiPredicate<Type, TypeMember> validationMethod, Type type, TypeMember member) {
        Type currentType = type;
        do {
            if (validationMethod.test(currentType, member)) {
                return true;
            }
            currentType = getParentType(currentType);
        }
        while (currentType.getSuperclass().getKind() != TypeKind.NONE);
        return false;
    }

    private String determineBuilderMethod(TypeMember enclosedEle, String prefix) {
        if (prefix.isBlank()) {
            return enclosedEle.getPropertyName();
        }
        return prefix + enclosedEle.getPropertyName().substring(0, 1).toUpperCase()
            + enclosedEle.getPropertyName().substring(1);
    }

    private GenerationType determineSubBuilderClassName(TypeMirror subType) {
        String initialBuilder = subType.toString() + BUILDER_SUFFIX;
        Element currentElement = typeUtils.asElement(subType);
        while (currentElement.getEnclosingElement().getKind() == ElementKind.CLASS) {
            currentElement = currentElement.getEnclosingElement();
            String outerclass = currentElement.asType().toString();
            initialBuilder = initialBuilder.replace(outerclass, outerclass + BUILDER_SUFFIX);
        }
        return new GenerationType(initialBuilder, typeUtils.getPackageName(currentElement));
    }

    private void addAliases(Member.Builder memberBuilder, TypeMirror subType, Type typeElement) {
        List<TypeMirror> aliases = gatherAliases(subType);
        memberBuilder.setAliases(aliases.stream()
            .filter(type -> notPresentAsFieldIn(type, typeElement)) // in case an alias conflicts with a field name skip it.
            .map(this::toAlias)
            .collect(Collectors.toList()));
    }

    private Alias toAlias(TypeMirror mirror) {
        return new Alias(asGenerationType(mirror), typeUtils.getName(mirror));
    }

    private List<TypeMirror> gatherAliases(TypeMirror subType) {
        List<TypeMirror> aliases = new ArrayList<>();
        for (AnnotationMirror mirror : typeUtils.getAnnotationMirrors(subType)) {
            if (AnnotationUtils.isSeeAlsoAnnotation(mirror)) {
                for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues()
                    .entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals("value")) {
                        aliases.addAll(gatherReferencedAliases(entry));
                    }
                }
            }
        }
        return aliases;
    }

    private List<TypeMirror> gatherReferencedAliases(Entry<? extends ExecutableElement, ? extends AnnotationValue> entry) {
        List<TypeMirror> aliases = new ArrayList<>();
        AnnotationValue value = entry.getValue();
        AnnotationClassReferenceVisitor referenceVisitor = new AnnotationClassReferenceVisitor();
        value.accept(referenceVisitor, null);
        for (TypeMirror typeMirror : referenceVisitor.mirrors) {
            aliases.addAll(gatherAliases(typeMirror));
            boolean isList = typeUtils.isList(typeMirror);
            if (!isList && !typeUtils.isAbstract(typeMirror)
                || isList && !typeUtils.isAbstract(typeUtils.getSubType(typeMirror))) {
                aliases.add(typeMirror);
            }
        }
        return aliases;
    }

    private boolean notPresentAsFieldIn(TypeMirror type, Type typeElement) {
        for (Type currentElement : getTypeElementHierarchy(typeElement)) {
            for (TypeMember enclosedEle : currentElement.getEnclosedElements()) {
                if (enclosedEle.isMethod() && (enclosedEle.isSetterMethod() || isListGetterMethod(enclosedEle))) {
                    TypeMirror propertyType = enclosedEle.getPropertyType();
                    if (typeUtils.isList(propertyType) && type.equals(typeUtils.getSubType(propertyType))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean typeHasBuilderAnnotation(TypeMirror propertyType) {
        return typeUtils.hasAnnotation(propertyType, Builder.class);
    }

    private List<Type> getTypeElementHierarchy(Type type) {
        List<Type> allTypes = new ArrayList<>();
        Type currentType = type;
        do {
            allTypes.add(currentType);
            currentType = getParentType(currentType);
        }
        while (currentType != null && !currentType.toString().equals("java.lang.Object"));
        return allTypes;
    }

    private String getExtendedBuilderName(Type type) {
        Type parentType = getParentType(type);
        if (parentType != null && !parentType.getQualifiedName().equals("java.lang.Object")) {
            if (parentType.getAnnotation(Builder.class) != null) {
                return getClassName(parentType.getQualifiedName()) + BUILDER_SUFFIX;
            }
            messager.printMessage(Kind.WARNING,
                String.format("Parent class '%s' is not annotated with '@Builder'.", parentType.getQualifiedName()),
                type.getTypeElement());
        }
        return null;
    }

    private Type getParentType(Type type) {
        return typeUtils.getParentType(type);
    }

    private boolean isListGetterMethod(TypeMember enclosedEle) {
        return enclosedEle.isGetterMethod() && typeUtils.isList(enclosedEle.getReturnType());
    }

    private String getClassName(String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1, qualifiedName.length());
    }

    /**
     * Use for reporting how far the builder generator got before a crash occurred.
     *
     * @return the stack of locations it visited to reach the current point. Does not contain actions that have been finished.
     */
    public String getActions() {
        return actions.stream().collect(Collectors.joining(", "));

    }

}
