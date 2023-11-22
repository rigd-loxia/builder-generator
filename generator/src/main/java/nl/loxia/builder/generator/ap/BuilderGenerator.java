package nl.loxia.builder.generator.ap;

import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
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

    /**
     * the constructor.
     *
     * @param environmentConfiguration
     * @param types - Utility class for working with Types.
     * @param messager - Feedback endpoint for communication errors/warnings to the java compiler
     * @param typeElement - The current element for which a builder needs to be generated.
     */
    public BuilderGenerator(EnvironmentConfiguration environmentConfiguration, Types types, Messager messager,
            TypeElement typeElement) {
        this.environmentConfiguration = environmentConfiguration;
        typeUtils = new TypeUtils(types);
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
        BuilderData builderData = createBuilderData(typeElement);

        if (!builderData.isValide()) {
            messager.printMessage(ERROR, "Cannot generate builder.", typeElement.getTypeElement());
            return;
        }

        try {
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
        BuilderData builderData =
            new BuilderData(typeElement.getPackageName(), typeElement.getTypeElement().asType(),
                typeElement.getSimpleName() + BUILDER_SUFFIX,
                getExtendedBuilderName(typeElement), isExtendedAbstract(typeElement),
                typeElement.getSmallestConstructorArgumentNames(),
                typeElement.isAbstract(), new BuilderConfiguration(environmentConfiguration, typeElement));

        updateBuilderDataWithHierarchicalInformation(typeElement, builderData);
        updateBuilderDataWithConstructorInformation(typeElement, builderData);
        return builderData;
    }

    private void updateBuilderDataWithConstructorInformation(Type typeElement, BuilderData builderData) {
        for (TypeMember parameter : typeElement.getSmallestConstructorArguments()) {
            builderData.addMember(createMember(typeElement, typeElement, parameter));
        }
    }

    private void updateBuilderDataWithHierarchicalInformation(Type typeElement, BuilderData builderData) {
        for (Type currentElement : getTypeElementHierarchy(typeElement)) {
            for (TypeMember typeMember : currentElement.getEnclosedElements()) {
                if (typeMember.isMethod() && typeMember.isSetter()) {
                    builderData.addMember(createMember(typeElement, currentElement, typeMember));
                }
                else if (typeMember.isMethod() && isListGetter(typeMember)) {
                    builderData.addMember(createMember(typeElement, currentElement, typeMember));
                }
                else if (typeMember.isClass() && currentElement == typeElement) {
                    builderData.addInnerClass(createBuilderData(typeMember.asType()));
                }
            }
        }
    }

    private Member createMember(Type typeElement, Type currentElement, TypeMember enclosedEle) {
        TypeMirror propertyType = enclosedEle.getPropertyType();
        Member.Builder memberBuilder = Member.builder()
            .type(propertyType)
            .outerType(typeUtils.getSurroundingClass(propertyType))
            .name(enclosedEle.getPropertyName())
            .hasGetter(typeElement.hasGetterFor(enclosedEle))
            .inherited(currentElement != typeElement && currentElement.hasBuilderAnnotation());
        if (typeUtils.isList(propertyType)) {
            TypeMirror subType = typeUtils.getSubType(propertyType);
            memberBuilder
                .isAbstract(typeUtils.isAbstract(subType))
                .subType(subType)
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

    private String determineSubBuilderClassName(TypeMirror subType) {
        String initialBuilder = subType.toString() + BUILDER_SUFFIX;
        Element currentElement = typeUtils.asElement(subType);
        while (currentElement.getEnclosingElement().getKind() == ElementKind.CLASS) {
            currentElement = currentElement.getEnclosingElement();
            String outerclass = currentElement.asType().toString();
            initialBuilder = initialBuilder.replace(outerclass, outerclass + BUILDER_SUFFIX);
        }
        return initialBuilder;
    }

    private void addAliases(Member.Builder memberBuilder, TypeMirror subType, Type typeElement) {
        List<TypeMirror> aliases = gatherAliases(subType);
        memberBuilder.setAliases(aliases.stream()
            .filter(type -> notPresentAsFieldIn(type, typeElement)) // in case an alias conflicts with a field name skip it.
            .map(this::toAlias)
            .collect(Collectors.toList()));
    }

    private Alias toAlias(TypeMirror mirror) {
        return new Alias(mirror, typeUtils.getName(mirror));
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
                if (enclosedEle.isMethod() && enclosedEle.isSetter()) {
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

    private boolean isExtendedAbstract(Type type) {
        Type parentType = getParentType(type);
        return parentType != null && parentType.isAbstract();
    }

    private Type getParentType(Type type) {
        return typeUtils.getParentType(type);
    }

    private boolean isListGetter(TypeMember enclosedEle) {
        return enclosedEle.isGetter() && typeUtils.isList(enclosedEle.getReturnType());
    }

    private String getClassName(String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1, qualifiedName.length());
    }

}
