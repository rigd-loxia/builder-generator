package nl.loxia.builder.generator.ap.test.issues._6;

import javax.tools.Diagnostic.Kind;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.CompilationResult;
import org.mapstruct.ap.testutil.compilation.annotation.Diagnostic;
import org.mapstruct.ap.testutil.compilation.annotation.ExpectedCompilationOutcome;

/**
 * In this situation a copy builder method is not possible. Enabling/Disabling of this behavior needs to be added to allow a builder
 * to be generated without a copy constructor.
 */
public class Issue6Test {
    @ProcessorTest
    @WithClasses(Issue6.class)
    @ExpectedCompilationOutcome(value = CompilationResult.FAILED,
            diagnostics = {
                @Diagnostic(
                        type = Issue6.class,
                        line = 6,
                        message = "Cannot generate builder.",
                        kind = Kind.ERROR)
            })
    void situationShouldNotResultInUnexpectedCompilationError() {

    }

}
