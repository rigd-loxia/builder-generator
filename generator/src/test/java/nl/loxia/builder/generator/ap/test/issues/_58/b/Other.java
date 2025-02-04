package nl.loxia.builder.generator.ap.test.issues._58.b;

import nl.loxia.builder.generator.annotations.Builder;
import nl.loxia.builder.generator.ap.test.issues._58.a.Inherited;

@Builder
public class Other extends Inherited {
    private String otherField;

    public String getOtherField() {
        return otherField;
    }

    public void setOtherField(String otherField) {
        this.otherField = otherField;
    }
}
