package nl.loxia.builder.generator.ap.test.seealso;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class SeeAlsoNotAbstractField1 extends SeeAlsoNotAbstractAnnotated {

    String field1;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }
}
