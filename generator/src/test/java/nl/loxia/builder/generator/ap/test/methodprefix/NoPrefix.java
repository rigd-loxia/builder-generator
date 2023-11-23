package nl.loxia.builder.generator.ap.test.methodprefix;

import nl.loxia.builder.generator.annotations.Builder;

@Builder(methodPrefix = "")
public class NoPrefix {

    private boolean booleanField;
    private final String stringField;

    public NoPrefix(String stringField) {
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
