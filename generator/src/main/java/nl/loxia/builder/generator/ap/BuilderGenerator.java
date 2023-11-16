package nl.loxia.builder.generator.ap;

import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractAnnotationValueVisitor9;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import freemarker.template.TemplateException;
import nl.loxia.builder.generator.annotations.Builder;
import nl.loxia.builder.generator.annotations.DefaultBoolean;
import nl.loxia.builder.generator.ap.EnvironmentConfiguration.VariableValue;

/**
 * The core of the builder generator framework.
 *
 * @author Ben Zegveld
 */
public class BuilderGenerator {

    private static final boolean COPY_OF_DEFAULT_VALUE = true;
    private static final String BUILDER_SUFFIX = "Builder";

    private final class GenerationException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public GenerationException(Throwable cause) {
            super(cause);
        }
    }

    private final class AnnotationClassReferenceVisitor extends AbstractAnnotationValueVisitor9<Void, Void> {

        private final List<TypeMirror> mirrors = new ArrayList<>();

        @Override
        public Void visitBoolean(boolean b, Void p) {
            return null;
        }

        @Override
        public Void visitByte(byte b, Void p) {
            return null;
        }

        @Override
        public Void visitChar(char c, Void p) {
            return null;
        }

        @Override
        public Void visitDouble(double d, Void p) {
            return null;
        }

        @Override
        public Void visitFloat(float f, Void p) {
            return null;
        }

        @Override
        public Void visitInt(int i, Void p) {
            return null;
        }

        @Override
        public Void visitLong(long i, Void p) {
            return null;
        }

        @Override
        public Void visitShort(short s, Void p) {
            return null;
        }

        @Override
        public Void visitString(String s, Void p) {
            return null;
        }

        @Override
        public Void visitType(TypeMirror t, Void p) {
            mirrors.add(t);
            return null;
        }

        @Override
        public Void visitEnumConstant(VariableElement c, Void p) {
            return null;
        }

        @Override
        public Void visitAnnotation(AnnotationMirror a, Void p) {
            return null;
        }

        @Override
        public Void visitArray(List<? extends AnnotationValue> vals, Void p) {
            for (AnnotationValue annotationValue : vals) {
                annotationValue.accept(this, p);
            }
            return null;
        }
    }

    private final Messager messager;
    private final TypeElement typeElement;
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
        this.typeElement = typeElement;
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
            messager.printMessage(ERROR, "Cannot generate builder.", typeElement);
            return;
        }

        try {
            String fileName = typeElement.getQualifiedName() + BUILDER_SUFFIX;
            JavaFileObject sourceFile = filer.createSourceFile(fileName, typeElement);
            freeMarkerWriter.write(sourceFile, builderData);
        }
        catch (IOException | TemplateException e) {
            throw new GenerationException(e);
        }
    }

    private BuilderData createBuilderData(TypeElement typeElement) {
        BuilderData builderData =
            new BuilderData(getPackageName(typeElement), typeElement.asType(),
                typeElement.getSimpleName() + BUILDER_SUFFIX,
                getExtendedBuilderName(typeElement), isExtendedAbstract(typeElement), getConstructorArguments(typeElement),
                isAbstract(typeElement), isCopyOfEnabled(typeElement));
        for (TypeElement currentElement : getTypeElementHierarchy(typeElement)) {
            for (Element enclosedEle : currentElement.getEnclosedElements()) {
                if (enclosedEle.getKind() == ElementKind.METHOD && isSetter(enclosedEle)) {
                    builderData.addMember(createMember(typeElement, currentElement, enclosedEle));
                }
                else if (enclosedEle.getKind() == ElementKind.METHOD && isListGetter(enclosedEle)) {
                    builderData.addMember(createMember(typeElement, currentElement, enclosedEle));
                }
                else if (enclosedEle.getKind() == ElementKind.CLASS && currentElement == typeElement) {
                    builderData.addInnerClass(createBuilderData((TypeElement) enclosedEle));
                }
            }
        }
        Optional<? extends Element> constructor = typeElement.getEnclosedElements()
            .stream()
            .filter(ele -> ele.getKind() == ElementKind.CONSTRUCTOR)
            .min((o1, o2) -> ((ExecutableElement) o1).getParameters().size() - ((ExecutableElement) o2).getParameters().size());
        if (constructor.isPresent()) {
            for (VariableElement parameter : ((ExecutableElement) constructor.get()).getParameters()) {
                builderData
                    .addMember(createMember(typeElement, typeElement, parameter));
            }
        }
        return builderData;
    }

    private boolean isCopyOfEnabled(TypeElement typeElement) {
        DefaultBoolean copyOf = typeElement.getAnnotation(Builder.class).copyOf();
        if (copyOf == DefaultBoolean.DEFAULT) {
            if (environmentConfiguration.getCopyOfMethodGeneration() == VariableValue.UNSET) {
                return COPY_OF_DEFAULT_VALUE;
            }
            return environmentConfiguration.getCopyOfMethodGeneration() == VariableValue.TRUE;
        }
        return copyOf == DefaultBoolean.TRUE;
    }

    private Member createMember(TypeElement typeElement, TypeElement currentElement, Element enclosedEle) {
        TypeMirror propertyType = getPropertyType(enclosedEle);
        Member.Builder memberBuilder = Member.builder()
            .type(propertyType)
            .outerType(typeUtils.getSurroundingClass(propertyType))
            .name(getPropertyName(enclosedEle))
            .hasGetter(getterMethodExist(typeElement, enclosedEle))
            .inherited(currentElement != typeElement && hasBuilderAnnotation(currentElement));
        if (isList(propertyType)) {
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

    private boolean getterMethodExist(TypeElement typeElement, Element enclosedEle) {
        return typeElement.getEnclosedElements().stream()
            .anyMatch(element -> element.getSimpleName().toString()
                .equals(determineGetterMethodName(enclosedEle)));
    }

    private String determineGetterMethodName(Element enclosedEle) {
        String propertyName = getPropertyName(enclosedEle);
        return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    private boolean isAbstract(Element element) {
        return element != null && element.getModifiers().contains(Modifier.ABSTRACT);
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

    private void addAliases(Member.Builder memberBuilder, TypeMirror subType, TypeElement typeElement) {
        List<TypeMirror> aliases = new ArrayList<>();
        gatherAliases(aliases, subType);
        memberBuilder.setAliases(aliases.stream()
            .filter(type -> notPresentAsFieldIn(type, typeElement))
            .map(this::toAlias)
            .collect(Collectors.toList()));
    }

    private void gatherAliases(List<TypeMirror> aliases, TypeMirror subType) {
        for (AnnotationMirror mirror : typeUtils.getAnnotationMirrors(subType)) {
            if (mirror.getAnnotationType().asElement().getSimpleName().toString().equals("SeeAlso")) {
                for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues()
                    .entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals("value")) {
                        gatherReferencedAliases(aliases, entry);
                    }
                }
            }
        }
    }

    private void gatherReferencedAliases(List<TypeMirror> aliases,
            Entry<? extends ExecutableElement, ? extends AnnotationValue> entry) {
        AnnotationValue value = entry.getValue();
        AnnotationClassReferenceVisitor referenceVisitor = new AnnotationClassReferenceVisitor();
        value.accept(referenceVisitor, null);
        for (TypeMirror typeMirror : referenceVisitor.mirrors) {
            gatherAliases(aliases, typeMirror);
            boolean isList = isList(typeMirror);
            if (!isList && !typeUtils.isAbstract(typeMirror)
                || isList && !typeUtils.isAbstract(typeUtils.getSubType(typeMirror))) {
                aliases.add(typeMirror);
            }
        }
    }

    private boolean notPresentAsFieldIn(TypeMirror type, TypeElement typeElement) {
        for (TypeElement currentElement : getTypeElementHierarchy(typeElement)) {
            for (Element enclosedEle : currentElement.getEnclosedElements()) {
                if (enclosedEle.getKind() == ElementKind.METHOD && isSetter(enclosedEle)) {
                    TypeMirror propertyType = getPropertyType(enclosedEle);
                    if (isList(propertyType) && type.equals(typeUtils.getSubType(propertyType))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Alias toAlias(TypeMirror mirror) {
        return new Alias(mirror, typeUtils.getName(mirror));
    }

    private boolean typeHasBuilderAnnotation(TypeMirror propertyType) {
        return typeUtils.hasAnnotation(propertyType, Builder.class);
    }

    private boolean hasBuilderAnnotation(Element typeElement) {
        return typeElement.getAnnotation(Builder.class) != null;
    }

    private String getPackageName(Element asElement) {
        return getPackageName(String.valueOf(((TypeElement) asElement).getQualifiedName()));
    }

    private List<TypeElement> getTypeElementHierarchy(TypeElement typeElement) {
        List<TypeElement> allTypeElements = new ArrayList<>();
        TypeElement currentTypeElement = typeElement;
        do {
            allTypeElements.add(currentTypeElement);
            currentTypeElement = getParentTypeElement(currentTypeElement);
        }
        while (currentTypeElement != null && !currentTypeElement.toString().equals("java.lang.Object"));
        return allTypeElements;
    }

    private String getExtendedBuilderName(TypeElement typeElement) {
        TypeElement superclass = getParentTypeElement(typeElement);
        if (superclass != null && !String.valueOf(superclass.getQualifiedName()).equals("java.lang.Object")) {
            if (superclass.getAnnotation(Builder.class) != null) {
                return getClassName(String.valueOf(superclass.getQualifiedName())) + BUILDER_SUFFIX;
            }
            messager.printMessage(Kind.WARNING,
                String.format("Parent class '%s' is not annotated with '@Builder'.", superclass.asType()),
                typeElement);
        }
        return null;
    }

    private boolean isExtendedAbstract(TypeElement typeElement) {
        TypeElement superclass = getParentTypeElement(typeElement);
        return superclass != null && superclass.getModifiers().contains(Modifier.ABSTRACT);
    }

    private TypeElement getParentTypeElement(TypeElement typeEle) {
        return typeUtils.getParentElement(typeEle);
    }

    boolean isList(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.DECLARED
            && typeUtils.getQualifiedName(typeMirror).equals("java.util.List");
    }

    private List<String> getConstructorArguments(TypeElement typeElement) {
        ArrayList<String> arrayList = new ArrayList<>();
        Optional<? extends Element> constructor = typeElement.getEnclosedElements()
            .stream()
            .filter(ele -> ele.getKind() == ElementKind.CONSTRUCTOR)
            .min((o1, o2) -> ((ExecutableElement) o1).getParameters().size() - ((ExecutableElement) o2).getParameters().size());
        if (constructor.isPresent()) {
            for (VariableElement parameter : ((ExecutableElement) constructor.get()).getParameters()) {
                arrayList.add(parameter.getSimpleName().toString());
            }
        }
        return arrayList;
    }

    private String getPropertyName(Element fieldOrMethod) {
        int index = isSetter(fieldOrMethod.getSimpleName()) || isListGetter(fieldOrMethod) ? 3 : 0;
        return String.valueOf(fieldOrMethod.getSimpleName().charAt(index)).toLowerCase()
            + fieldOrMethod.getSimpleName().subSequence(index + 1, fieldOrMethod.getSimpleName().length()).toString();
    }

    private TypeMirror getPropertyType(Element fieldOrMethod) {
        if (isListGetter(fieldOrMethod)) {
            return ((ExecutableElement) fieldOrMethod).getReturnType();
        }
        if (fieldOrMethod instanceof ExecutableElement) {
            return ((ExecutableElement) fieldOrMethod).getParameters().get(0).asType();
        }
        else {
            return ((VariableElement) fieldOrMethod).asType();
        }
    }

    private boolean isSetter(Element fieldOrMethod) {
        Name methodName = fieldOrMethod.getSimpleName();
        return isSetter(methodName) && ((ExecutableElement) fieldOrMethod).getParameters().size() == 1;
    }

    private boolean isSetter(Name methodName) {
        return methodName.charAt(0) == 's'
            && methodName.charAt(1) == 'e'
            && methodName.charAt(2) == 't'
            && methodName.charAt(3) >= 'A' && methodName.charAt(3) <= 'Z';
    }

    private boolean isListGetter(Element enclosedEle) {
        return isGetter(enclosedEle)
            && isList(((ExecutableElement) enclosedEle).getReturnType());
    }

    private boolean isGetter(Element fieldOrMethod) {
        Name methodName = fieldOrMethod.getSimpleName();
        return isGetter(methodName) && ((ExecutableElement) fieldOrMethod).getParameters().isEmpty();
    }

    private boolean isGetter(Name methodName) {
        return methodName.charAt(0) == 'g'
            && methodName.charAt(1) == 'e'
            && methodName.charAt(2) == 't'
            && methodName.charAt(3) >= 'A' && methodName.charAt(3) <= 'Z';
    }

    private String getClassName(String qualifiedName) {
        return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1, qualifiedName.length());
    }

    private String getPackageName(String qualifiedName) {
        return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
    }

}
