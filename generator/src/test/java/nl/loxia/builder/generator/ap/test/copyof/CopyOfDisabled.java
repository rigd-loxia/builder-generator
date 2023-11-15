package nl.loxia.builder.generator.ap.test.copyof;

import static nl.loxia.builder.generator.annotations.DefaultBoolean.FALSE;

import nl.loxia.builder.generator.annotations.Builder;

@Builder(copyOf = FALSE)
public class CopyOfDisabled {

    private final String myField;

    CopyOfDisabled(String somethingElse) {
        myField = somethingElse;
    }

    public String getMyField() {
        return myField;
    }

}
