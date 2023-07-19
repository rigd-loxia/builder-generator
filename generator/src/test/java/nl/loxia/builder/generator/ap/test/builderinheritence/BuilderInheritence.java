package nl.loxia.builder.generator.ap.test.builderinheritence;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class BuilderInheritence extends BuilderInheritenceBase {
    private int otherValue;

    public int getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(int otherValue) {
        this.otherValue = otherValue;
    }
}
