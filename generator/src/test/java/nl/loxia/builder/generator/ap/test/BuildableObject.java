package nl.loxia.builder.generator.ap.test;

import nl.loxia.builder.generator.annotations.Builder;

/**
 * This class is used for multiple tests as an object that has a builder of its own.
 */
@Builder
public class BuildableObject {
    private String val;

    public void setVal(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
