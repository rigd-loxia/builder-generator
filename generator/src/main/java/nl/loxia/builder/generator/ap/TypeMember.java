package nl.loxia.builder.generator.ap;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Used to simplify model structure.
 */
public class TypeMember {

    private final Element element;

    /**
     * @param element the model item to simplify
     */
    public TypeMember(Element element) {
        this.element = element;
    }

    /**
     * @return this element as a {@link Type}
     */
    public Type asType() {
        return new Type((TypeElement) element);
    }

    /**
     * @return true if this element represents a field.
     */
    public boolean isField() {
        return element.getKind() == ElementKind.FIELD;
    }

    /**
     * @return true if this element represents a method.
     */
    public boolean isMethod() {
        return element.getKind() == ElementKind.METHOD;
    }

    /**
     * @return true if this element represents a class. If true then you can use {@link #asType()} to get more information from it.
     */
    public boolean isClass() {
        return element.getKind() == ElementKind.CLASS;
    }

    /**
     * @return true if this element represents a constructor.
     */
    public boolean isConstructor() {
        return element.getKind() == ElementKind.CONSTRUCTOR;
    }

    /**
     * @return true if this element represents a setter method.
     */
    public boolean isSetterMethod() {
        Name methodName = element.getSimpleName();
        return isMethod() && isSetter(methodName) && ((ExecutableElement) element).getParameters().size() == 1;
    }

    private boolean isSetter(Name methodName) {
        return methodName.charAt(0) == 's'
            && methodName.charAt(1) == 'e'
            && methodName.charAt(2) == 't'
            && methodName.charAt(3) >= 'A' && methodName.charAt(3) <= 'Z';
    }

    /**
     * @return true if this element represents a getter method.
     */
    public boolean isGetterMethod() {
        Name methodName = element.getSimpleName();
        return isMethod() && isGetter(methodName) && ((ExecutableElement) element).getParameters().isEmpty();
    }

    private boolean isGetter(Name methodName) {
        return methodName.charAt(0) == 'g'
            && methodName.charAt(1) == 'e'
            && methodName.charAt(2) == 't'
            && methodName.charAt(3) >= 'A' && methodName.charAt(3) <= 'Z';
    }

    /**
     * See also {@link ExecutableElement#getReturnType()}
     *
     * @return true the return type if this element represents a method. returns null if this is not a method.
     */
    public TypeMirror getReturnType() {
        return isMethod() ? ((ExecutableElement) element).getReturnType() : null;
    }

    /**
     * @return the parameters if this is a method or constructor otherwise an empty list.
     */
    public List<TypeMember> getParameters() {
        if (isMethod() || isConstructor()) {
            return ((ExecutableElement) element).getParameters().stream().map(TypeMember::new).collect(toList());
        }
        return List.of();
    }

    /**
     * @return the return type for a getter, the first parameter for another method, or the element itself.
     */
    public TypeMirror getPropertyType() {
        if (isGetterMethod()) {
            return getReturnType();
        }
        if (element instanceof ExecutableElement) {
            return ((ExecutableElement) element).getParameters().get(0).asType();
        }
        else {
            return ((VariableElement) element).asType();
        }
    }

    /**
     * if this is a getter or setter method then the first character will be turned into lowercase after removing the get/set method
     * name.<BR>
     * Examples:<BR>
     * <ul>
     * <li>{@code getAll} would become {@code all}</li>
     * <li>{@code getALL} would become {@code aLL}</li>
     * <li>{@code getall} would become {@code getall}</li>
     * <li>{@code setAll} would become {@code all}</li>
     * <li>{@code setall} would become {@code setall}</li>
     * </ul>
     *
     * @return the property name.
     */
    public String getPropertyName() {
        int index = isSetterMethod() || isGetterMethod() ? 3 : 0;
        return String.valueOf(getSimpleName().charAt(index)).toLowerCase() + getSimpleName().substring(index + 1);
    }

    /**
     * see {@link Element#getSimpleName()}
     *
     * @return the simple name of the type.
     */
    public String getSimpleName() {
        return element.getSimpleName().toString();
    }

    /**
     * return the expected get method based on the {@link #getPropertyName()} result.
     *
     * @return the getter method name that would match this element.
     */
    public String determineGetterMethodName() {
        String propertyName = getPropertyName();
        return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    /**
     * return the expected set method based on the {@link #getPropertyName()} result.
     *
     * @return the setter method name that would match this element.
     */
    public String determineSetterMethodName() {
        String propertyName = getPropertyName();
        return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    public Element getElement() {
        return element;
    }

}
