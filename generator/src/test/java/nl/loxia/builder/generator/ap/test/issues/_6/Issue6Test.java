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
    @WithClasses(ErroneousIssue6.class)
    @ExpectedCompilationOutcome(value = CompilationResult.FAILED,
            diagnostics = {
                @Diagnostic(
                        type = ErroneousIssue6.class,
                        line = 6,
                        message = "Cannot generate builder. The fields [somethingElse] do not have a getter, copyOf method cannot be generated. "
                            + "Use `@Builder(copyOf=false)` to disable the copyOf method generation.",
                        kind = Kind.ERROR)
            })
    void situationShouldNotResultInUnexpectedCompilationError() {

    }

}
