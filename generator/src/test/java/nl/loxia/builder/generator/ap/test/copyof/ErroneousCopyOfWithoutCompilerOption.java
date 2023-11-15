package nl.loxia.builder.generator.ap.test.copyof;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class ErroneousCopyOfWithoutCompilerOption {

    private final String myField;

    ErroneousCopyOfWithoutCompilerOption(String somethingElse) {
        myField = somethingElse;
    }

    public String getMyField() {
        return myField;
    }

}
