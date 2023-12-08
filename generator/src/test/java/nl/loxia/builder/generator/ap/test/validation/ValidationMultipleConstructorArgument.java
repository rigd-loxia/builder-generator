package nl.loxia.builder.generator.ap.test.validation;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class ValidationMultipleConstructorArgument {

    private final String required;
    private final String otherRequired;

    public ValidationMultipleConstructorArgument(String required, String otherRequired) {
        this.required = required;
        this.otherRequired = otherRequired;
    }

    public String getRequired() {
        return required;
    }

    public String getOtherRequired() {
        return otherRequired;
    }
}
