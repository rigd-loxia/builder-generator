package nl.loxia.builder.generator.ap.test.issues._16;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

/**
 * This test is created to test builders for inner classes in a List.
 */
public class Issue16Test {

    @ProcessorTest
    @WithClasses(OuterClass.class)
    void situationShouldNotResultInUnexpectedCompilationError() {

    }

}
