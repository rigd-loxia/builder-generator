package nl.loxia.builder.generator.ap.test;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class SimpleClassMixed {

    private boolean booleanField;
    private final String stringField;

    public SimpleClassMixed(String stringField) {
        this.stringField = stringField;
    }

    public String getStringField() {
        return stringField;
    }

    public boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }
}
