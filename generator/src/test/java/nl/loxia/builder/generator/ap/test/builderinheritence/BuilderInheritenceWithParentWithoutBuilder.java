package nl.loxia.builder.generator.ap.test.builderinheritence;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class BuilderInheritenceWithParentWithoutBuilder extends ParentWithoutBuilder {
    private String other;

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
