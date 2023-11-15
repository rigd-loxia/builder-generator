package nl.loxia.builder.generator.ap.test.copyof;

import nl.loxia.builder.generator.annotations.Builder;

@Builder(copyOf = false)
public class CopyOfDisabled {

    private final String myField;

    CopyOfDisabled(String somethingElse) {
        myField = somethingElse;
    }

    public String getMyField() {
        return myField;
    }

}
