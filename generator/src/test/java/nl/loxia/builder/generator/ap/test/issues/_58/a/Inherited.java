package nl.loxia.builder.generator.ap.test.issues._58.a;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class Inherited {
    private String inheritedField;

    public String getInheritedField() {
        return inheritedField;
    }

    public void setInheritedField(String inheritedField) {
        this.inheritedField = inheritedField;
    }
}
