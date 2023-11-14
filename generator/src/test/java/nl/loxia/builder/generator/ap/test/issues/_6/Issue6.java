package nl.loxia.builder.generator.ap.test.issues._6;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
class Issue6 {
    private final String myField;

    Issue6(String somethingElse) {
        myField = somethingElse;
    }

    public String getMyField() {
        return myField;
    }

}
