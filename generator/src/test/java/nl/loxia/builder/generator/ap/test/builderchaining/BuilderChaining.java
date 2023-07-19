package nl.loxia.builder.generator.ap.test.builderchaining;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class BuilderChaining {
    private BuilderChainingChild value;

    public BuilderChainingChild getValue() {
        return value;
    }

    public void setValue(BuilderChainingChild value) {
        this.value = value;
    }
}
