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

public class TypeMember {

    private final Element element;

    public TypeMember(Element element) {
        this.element = element;
    }

    public Type asType() {
        return new Type((TypeElement) element);
    }

    public boolean isMethod() {
        return element.getKind() == ElementKind.METHOD;
    }

    public boolean isClass() {
        return element.getKind() == ElementKind.CLASS;
    }

    public boolean isConstructor() {
        return element.getKind() == ElementKind.CONSTRUCTOR;
    }

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

    public TypeMirror getReturnType() {
        return isMethod() ? ((ExecutableElement) element).getReturnType() : null;
    }

    public List<TypeMember> getParameters() {
        if (isMethod() || isConstructor()) {
            return ((ExecutableElement) element).getParameters().stream().map(TypeMember::new).collect(toList());
        }
        return List.of();
    }

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

    public String getPropertyName() {
        int index = isSetterMethod() || isGetterMethod() ? 3 : 0;
        return String.valueOf(getSimpleName().charAt(index)).toLowerCase() + getSimpleName().substring(index + 1);
    }

    public String getSimpleName() {
        return element.getSimpleName().toString();
    }

    public String determineGetterMethodName() {
        String propertyName = getPropertyName();
        return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

}
