package nl.loxia.builder.generator.ap.test.builderchaining;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class BuilderChainingChild {
    private final List<String> values = new ArrayList<>();

    public List<String> getValues() {
        return values;
    }
}
