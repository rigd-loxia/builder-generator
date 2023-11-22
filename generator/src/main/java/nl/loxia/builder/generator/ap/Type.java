package nl.loxia.builder.generator.ap;

import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import nl.loxia.builder.generator.annotations.Builder;

/**
 * Used to simplify model structure.
 */
public class Type {

    private final TypeElement typeElement;

    public Type(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public String getQualifiedName() {
        return typeElement.getQualifiedName().toString();
    }

    public String getPackageName() {
        String qualifiedName = getQualifiedName();
        return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
    }

    public String getSimpleName() {
        return typeElement.getSimpleName().toString();
    }

    public List<TypeMember> getEnclosedElements() {
        return getEnclosedElementsStream().collect(toList());
    }

    private Stream<TypeMember> getEnclosedElementsStream() {
        return typeElement.getEnclosedElements().stream().map(TypeMember::new);
    }

    public boolean isAbstract() {
        return typeElement.getModifiers().contains(Modifier.ABSTRACT);
    }

    public <ANNOTATION extends Annotation> ANNOTATION getAnnotation(Class<ANNOTATION> annotationType) {
        return typeElement.getAnnotation(annotationType);
    }

    public boolean hasBuilderAnnotation() {
        return getAnnotation(Builder.class) != null;
    }

    public TypeMirror getSuperclass() {
        return typeElement.getSuperclass();
    }

    public List<TypeMember> getSmallestConstructorArguments() {
        Optional<TypeMember> constructor = getEnclosedElementsStream()
            .filter(TypeMember::isConstructor)
            .min((o1, o2) -> o1.getParameters().size() - o2.getParameters().size());
        if (constructor.isPresent()) {
            return constructor.get().getParameters();
        }
        return List.of();
    }

    public List<String> getSmallestConstructorArgumentNames() {
        return getSmallestConstructorArguments().stream().map(TypeMember::getPropertyName).collect(toList());
    }

    public boolean hasGetterFor(TypeMember typeMember) {
        return getEnclosedElementsStream()
            .anyMatch(element -> element.getSimpleName()
                .equals(typeMember.determineGetterMethodName()));
    }

}
