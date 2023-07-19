package nl.loxia.builder.generator.ap.test.seealso;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
@SeeAlso({ SeeAlsoAnnotated2.class, DuplicateField.class })
public abstract class SeeAlsoAnnotated {
    String val;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
