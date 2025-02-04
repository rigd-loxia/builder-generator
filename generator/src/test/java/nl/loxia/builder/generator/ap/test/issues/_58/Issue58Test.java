package nl.loxia.builder.generator.ap.test.issues._58;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

import nl.loxia.builder.generator.ap.test.issues._58.a.Inherited;
import nl.loxia.builder.generator.ap.test.issues._58.b.Other;

public class Issue58Test {

    @ProcessorTest
    @WithClasses({ Inherited.class, Other.class })
    void situationShouldNotResultInUnexpectedCompilationError() {

    }
}
