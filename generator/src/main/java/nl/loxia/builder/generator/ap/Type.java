package nl.loxia.builder.generator.ap;

import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import nl.loxia.builder.generator.annotations.Builder;

/**
 * Used to simplify model structure.
 */
public class Type {

    private final TypeElement typeElement;

    /**
     * @param typeElement the model item to simplify
     */
    public Type(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    /**
     * @return the typeElement represented by this Type.
     */
    public TypeElement getTypeElement() {
        return typeElement;
    }

    /**
     * see {@link TypeElement#getQualifiedName()}
     *
     * @return the qualified name of the type.
     */
    public String getQualifiedName() {
        return typeElement.getQualifiedName().toString();
    }

    /**
     * @return the package of the type.
     */
    public String getPackageName() {
        String qualifiedName = getQualifiedName();
        return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
    }

    /**
     * see {@link TypeElement#getSimpleName()}
     *
     * @return the simple name of the type.
     */
    public String getSimpleName() {
        return typeElement.getSimpleName().toString();
    }

    /**
     * see {@link TypeElement#getEnclosedElements()}, but then wrapped in {@link TypeMember}.
     *
     * @return the simple name of the type.
     */
    public List<TypeMember> getEnclosedElements() {
        return getEnclosedElementsStream().collect(toList());
    }

    private Stream<TypeMember> getEnclosedElementsStream() {
        return typeElement.getEnclosedElements().stream().map(TypeMember::new);
    }

    /**
     * @return true if this type is marked as abstract.
     */
    public boolean isAbstract() {
        return typeElement.getModifiers().contains(Modifier.ABSTRACT);
    }

    /**
     * see {@link TypeElement#getAnnotation(Class)}
     *
     * @param <ANNOTATION> the annotation type
     * @param annotationType the Class object corresponding to the annotation type
     * @return this construct's annotation of the specified type if such an annotation is present, else null.
     */
    public <ANNOTATION extends Annotation> ANNOTATION getAnnotation(Class<ANNOTATION> annotationType) {
        return typeElement.getAnnotation(annotationType);
    }

    /**
     * @return true if the Builder annotation is present.
     */
    public boolean hasBuilderAnnotation() {
        return getAnnotation(Builder.class) != null;
    }

    /**
     * see {@link TypeElement#getSuperclass()}
     *
     * @return the super class.
     */
    public TypeMirror getSuperclass() {
        return typeElement.getSuperclass();
    }

    /**
     * If there are multiple constructors available it will search for the one with the least amount of arguments and return this
     * one. If there are multiple constructors which are valid for this match, then one at random is returned.
     *
     * @return a list of parameters for the smallest constructor.
     */
    public List<TypeMember> getSmallestConstructorArguments() {
        Optional<TypeMember> constructor = getEnclosedElementsStream()
            .filter(TypeMember::isConstructor)
            .min((o1, o2) -> o1.getParameters().size() - o2.getParameters().size());
        if (constructor.isPresent()) {
            return constructor.get().getParameters();
        }
        return List.of();
    }

    /**
     * If there are multiple constructors available it will search for the one with the least amount of arguments and return this
     * one. If there are multiple constructors which are valid for this match, then one at random is returned.
     *
     * @return a list of parameter names for the smallest constructor.
     */
    public List<String> getSmallestConstructorArgumentNames() {
        return getSmallestConstructorArguments().stream().map(TypeMember::getPropertyName).collect(toList());
    }

    /**
     * @param typeMember representing the field
     * @return true if a getter method is available to retrieve the field.
     */
    public boolean hasGetterFor(TypeMember typeMember) {
        return getEnclosedElementsStream()
            .anyMatch(element -> element.getSimpleName()
                .equals(typeMember.determineGetterMethodName()));
    }

    /**
     * @param typeMember representing the field
     * @return true if a getter method is available to retrieve the field.
     */
    public boolean hasSetterFor(TypeMember typeMember) {
        return getEnclosedElementsStream()
            .anyMatch(element -> element.getSimpleName()
                .equals(typeMember.determineSetterMethodName()));
    }

    /**
     * @param annotationClass - the annotation class for which the needs to be looked up.
     * @param annotationMethod - the name of the method for which the value is wanted.
     * @return the value associated with the specified annotation and method of that annotation. returns null if not found or not
     *         present.
     */
    public Object getAnnotationValue(Class<? extends Annotation> annotationClass, String annotationMethod) {
        for (AnnotationMirror mirror : typeElement.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(annotationClass.getName())) {
                Map<String, Object> map = mirror.getElementValues().entrySet().stream()
                    .collect(Collectors.toMap(k -> k.getKey().getSimpleName().toString(), k -> k.getValue().getValue()));
                return map.get(annotationMethod);
            }
        }
        return null;
    }

}
