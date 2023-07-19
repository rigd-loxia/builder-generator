package nl.loxia.builder.generator.ap.test.builderinheritence;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class BuilderInheritenceBase {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
