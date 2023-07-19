package nl.loxia.builder.generator.ap.test;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class SimpleClassAllFieldsInConstructor {

    private final Boolean booleanField;
    private final String stringField;

    public SimpleClassAllFieldsInConstructor(String stringField, Boolean booleanField) {
        this.stringField = stringField;
        this.booleanField = booleanField;
    }

    public String getStringField() {
        return stringField;
    }

    public Boolean getBooleanField() {
        return booleanField;
    }
}
