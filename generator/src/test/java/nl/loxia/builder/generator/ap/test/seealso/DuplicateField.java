package nl.loxia.builder.generator.ap.test.seealso;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class DuplicateField extends SeeAlsoAnnotated {
    String dup;

    public String getDup() {
        return dup;
    }

    public void setDup(String dup) {
        this.dup = dup;
    }
}
