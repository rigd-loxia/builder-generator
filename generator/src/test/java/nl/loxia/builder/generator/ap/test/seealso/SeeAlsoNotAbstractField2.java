package nl.loxia.builder.generator.ap.test.seealso;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class SeeAlsoNotAbstractField2 extends SeeAlsoNotAbstractAnnotated {

    String field2;

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }
}
