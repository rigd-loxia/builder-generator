package nl.loxia.builder.generator.ap.test.typeshortening;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class JavaLangTypes {

    private String stringValue;
    private Boolean boolValue;
    private Integer intValue;
    private Character charValue;
    private Byte byteValue;

    public void setByteValue(Byte byteValue) {
        this.byteValue = byteValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    public void setCharValue(Character charValue) {
        this.charValue = charValue;
    }

    public Character getCharValue() {
        return charValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

}
