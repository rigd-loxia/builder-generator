package nl.loxia.builder.generator.ap.test.builderinheritence;

import javax.tools.Diagnostic.Kind;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.CompilationResult;
import org.mapstruct.ap.testutil.compilation.annotation.Diagnostic;
import org.mapstruct.ap.testutil.compilation.annotation.ExpectedCompilationOutcome;
import org.mapstruct.ap.testutil.runner.Compiler;
import org.mapstruct.ap.testutil.runner.GeneratedSource;

class BuilderInheritenceTest {

    @RegisterExtension
    private final GeneratedSource generatedSource = new GeneratedSource();

    @ProcessorTest
    @WithClasses({ BuilderInheritence.class, BuilderInheritenceBase.class })
    void builderInheritence() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(BuilderInheritence.class);
    }

    @ProcessorTest
    @WithClasses({ BuilderInnerClass.class })
    void builderInnerClass() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(BuilderInnerClass.class);
    }

    @ProcessorTest
    @WithClasses({ BuilderInheritenceOfinnerClass.class, BuilderInnerClass.class })
    void builderInheritenceOfinnerClass() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(BuilderInheritenceOfinnerClass.class);
    }

    @ProcessorTest(Compiler.JDK)
    @WithClasses({ BuilderInheritenceWithParentWithoutBuilder.class, ParentWithoutBuilder.class })
    @ExpectedCompilationOutcome(value = CompilationResult.SUCCEEDED,
            diagnostics = @Diagnostic(kind = Kind.WARNING,
                    message = "Parent class 'nl.loxia.builder.generator.ap.test.builderinheritence.ParentWithoutBuilder' is not annotated with '@Builder'."))
    void builderWithParentWithoutBuilderHasCompilerWarning() {
    }

}
