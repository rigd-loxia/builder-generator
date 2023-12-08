package nl.loxia.builder.generator.ap.test;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class ErroneousBuilderWithConstructorFieldMismatch extends ParentWithoutBuilder {

    private final String somethingElse;

    public ErroneousBuilderWithConstructorFieldMismatch(String field2, String somethingElse) {
        this.somethingElse = somethingElse;
        setField(field2);// mismatched property
    }

    public String getSomethingElse() {
        return somethingElse;
    }
}
