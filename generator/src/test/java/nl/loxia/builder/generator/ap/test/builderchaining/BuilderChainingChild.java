package nl.loxia.builder.generator.ap.test.builderchaining;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class BuilderChainingChild {
    private List<String> values = new ArrayList<>();

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
