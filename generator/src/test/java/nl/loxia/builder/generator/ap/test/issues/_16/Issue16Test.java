package nl.loxia.builder.generator.ap.test.issues._16;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

import nl.loxia.builder.generator.ap.test.issues._16.differentpackage.ReferencingInnerClasses;

/**
 * This test is created to test builders for inner classes in a List.
 */
public class Issue16Test {

    @ProcessorTest
    @WithClasses(OuterClass.class)
    void situationShouldNotResultInUnexpectedCompilationError() {

    }

    @ProcessorTest
    @WithClasses(OuterClassWithTwoLayersOfInnerClasses.class)
    void situationShouldNotResultInUnexpectedCompilationErrorAsWell() {

    }

    @ProcessorTest
    @WithClasses({ OuterClassWithTwoLayersOfInnerClasses.class, ReferencingInnerClasses.class })
    void situationReferencingFromDifferentPackage() {

    }

}
