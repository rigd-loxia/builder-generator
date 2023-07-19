package nl.loxia.builder.generator.ap.test;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class SimpleClassAllFieldsInSetter {

    private boolean booleanField;
    private String stringField;

    public String getStringField() {
        return stringField;
    }

    public boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }
}
