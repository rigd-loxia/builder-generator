package nl.loxia.builder.generator.ap.test.constructor;

import nl.loxia.builder.generator.annotations.Builder;

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
