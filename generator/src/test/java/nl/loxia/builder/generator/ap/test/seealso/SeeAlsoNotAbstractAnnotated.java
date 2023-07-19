package nl.loxia.builder.generator.ap.test.seealso;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
@SeeAlso({ SeeAlsoNotAbstractField1.class, SeeAlsoNotAbstractField2.class })
public class SeeAlsoNotAbstractAnnotated {

    String base;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
