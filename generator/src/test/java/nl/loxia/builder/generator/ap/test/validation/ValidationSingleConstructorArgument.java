package nl.loxia.builder.generator.ap.test.validation;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class ValidationSingleConstructorArgument {

    private final String required;

    public ValidationSingleConstructorArgument(String required) {
        this.required = required;
    }

    public String getRequired() {
        return required;
    }
}
