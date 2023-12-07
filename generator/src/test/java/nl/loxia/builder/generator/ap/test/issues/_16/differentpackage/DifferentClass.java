package nl.loxia.builder.generator.ap.test.issues._16.differentpackage;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class DifferentClass {
    private String val;

    public void setVal(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
