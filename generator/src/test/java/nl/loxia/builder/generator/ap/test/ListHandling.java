package nl.loxia.builder.generator.ap.test;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class ListHandling {
    private final List<String> values = new ArrayList<>();

    public List<String> getValues() {
        return values;
    }
}
