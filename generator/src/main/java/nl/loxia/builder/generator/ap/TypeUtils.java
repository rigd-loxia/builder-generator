package nl.loxia.builder.generator.ap;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Utility method for working with {@link Types}.
 *
 * @author Ben Zegveld
 */
class TypeUtils {

    private final Types types;

    TypeUtils(Types types) {
        this.types = types;
    }

    /**
     * Retrieves the first type argument for the type. For example: using the TypeMirror for <code> List&lt;String&gt; </code> would
     * return the TypeMirror for the String class.
     *
     * @param propertyType - a TypeMirror that has a TypeArgument.
     * @return the first TypeArgument
     * @throws IndexOutOfBoundsException - if there are no TypeArguments present.
     */
    TypeMirror getSubType(TypeMirror propertyType) {
        return ((DeclaredType) propertyType).getTypeArguments().get(0);
    }

    /**
     * Used to check if a class is abstract.
     *
     * @param type - the typeMirror to check
     * @return true if the type refers to an abstract class
     */
    boolean isAbstract(TypeMirror type) {
        return isAbstract(asElement(type));
    }

    /**
     * Redirects to {@link Types#asElement(TypeMirror)}
     */
    Element asElement(TypeMirror type) {
        return types.asElement(type);
    }

    private boolean isAbstract(Element element) {
        return element != null && element.getModifiers().contains(Modifier.ABSTRACT);
    }

    /**
     * convenience method for calling {@link Element#getAnnotationMirrors()} on the element that corresponds to this type.
     *
     * @param type - the type to retrieve annotations for.
     * @return the annotations directly present on this TypeMirror; an empty list if there are none
     * @throws NullPointerException - if the type is not one with a corresponding element
     */
    List<? extends AnnotationMirror> getAnnotationMirrors(TypeMirror type) {
        return asElement(type).getAnnotationMirrors();
    }

    /**
     * convenience method for calling {@link Element#getSimpleName()} on the element that corresponds to this type.
     *
     * @param type - the type for which the name is wanted
     * @return the simple name associated with this type.
     * @throws NullPointerException - if the type is not one with a corresponding element
     */
    String getName(TypeMirror type) {
        return asElement(type).getSimpleName().toString();
    }

    /**
     * checks if the element enclosing this type is a class.
     *
     * @param type - the type for which the check is wanted.
     * @return true if this class is an innerclass.
     */
    boolean isContainedInClass(TypeMirror type) {
        return asElement(type) != null
            && asElement(type).getEnclosingElement().getKind() == ElementKind.CLASS;
    }

    /**
     * retrieves the type enclosing this type if {@link #isContainedInClass(TypeMirror)} returns true, otherwise {@code null}.
     *
     * @param type - the type for which the surrounding type is wanted.
     * @return the surrounding type, if {@link #isContainedInClass(TypeMirror)} returns true, otherwise {@code null}.
     */
    TypeMirror getSurroundingClass(TypeMirror type) {
        return isContainedInClass(type) ? asElement(type).getEnclosingElement().asType() : null;
    }

    /**
     * retrieves the type hierarchy enclosing this type. An empty collection is returned if {@link #isContainedInClass(TypeMirror)}
     * is false.
     *
     * @param type - the type for which the type hierarchy is wanted.
     * @return the type hierarchy.
     */
    List<TypeMirror> getSurroundingClasses(TypeMirror propertyType) {
        List<TypeMirror> outerClasses = new ArrayList<>();
        TypeMirror currentType = propertyType;
        while (isContainedInClass(currentType)) {
            currentType = asElement(currentType).getEnclosingElement().asType();
            outerClasses.add(currentType);
        }
        Collections.reverse(outerClasses);
        return outerClasses;
    }

    /**
     * Checks if the specified annotation is present on the type.
     *
     * @param type - type to check
     * @param annotation - annotation to check for
     * @return true if the annotation is present on the type
     */
    boolean hasAnnotation(TypeMirror type, Class<? extends Annotation> annotation) {
        return asElement(type) != null && asElement(type).getAnnotation(annotation) != null;
    }

    /**
     * returns the hierarchical parent of this typeElement.
     *
     * @param typeEle - the type element of which the hierachical parent is wanted.
     * @return the hierarchical parent of this typeElement or null if absent.
     */
    Type getParentType(Type typeEle) {
        return typeEle.getSuperclass().getKind() != TypeKind.NONE
            ? new Type((TypeElement) types.asElement(typeEle.getSuperclass()))
            : null;
    }

    /**
     * convenience method for calling {@link TypeElement#getQualifiedName()} on the element that corresponds to this type.
     *
     * @param type - the type for which the name is wanted
     * @return the simple name associated with this type.
     * @throws NullPointerException - if the type is not one with a corresponding element
     */
    String getQualifiedName(TypeMirror typeMirror) {
        return ((TypeElement) asElement(typeMirror)).getQualifiedName().toString();
    }

    /**
     * @param typeMirror - the type to check
     * @return true if this represents an java.util.List
     */
    boolean isList(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.DECLARED
            && getQualifiedName(typeMirror).equals("java.util.List");
    }

    /**
     * If there are multiple constructors available it will search for the one with the least amount of arguments and return this
     * one. If there are multiple constructors which are valid for this match, then one at random is returned.
     *
     * @return a list of parameters for the smallest constructor.
     */
    public List<TypeMember> getSmallestConstructorArguments(Type type) {
        Optional<TypeMember> constructor = type.getEnclosedElements()
            .stream()
            .filter(TypeMember::isConstructor)
            .min((o1, o2) -> o1.getParameters().size() - o2.getParameters().size());
        if (constructor.isPresent()) {
            return constructor.get().getParameters();
        }
        return List.of();
    }

    /**
     * @param propertyType - the type to determine the package name of.
     * @return the package name for this type.
     */
    public String getPackageName(TypeMirror propertyType) {
        return getPackageName(types.asElement(propertyType));
    }

    /**
     * @param element - the element to determine the package name of.
     * @return the package name for this type.
     */
    public String getPackageName(Element element) {
        Element currentElement = element;
        while (currentElement != null && currentElement.getKind() != ElementKind.PACKAGE) {
            currentElement = currentElement.getEnclosingElement();
        }
        if (currentElement == null) {
            return "";
        }
        String packageName = currentElement.asType().toString();
        if (packageName.equals("package")) {
            packageName = currentElement.toString().replace("package ", "");
        }
        return packageName;
    }

}
