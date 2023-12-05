package nl.loxia.builder.generator.ap.test;

import javax.tools.Diagnostic.Kind;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.CompilationResult;
import org.mapstruct.ap.testutil.compilation.annotation.Diagnostic;
import org.mapstruct.ap.testutil.compilation.annotation.ExpectedCompilationOutcome;
import org.mapstruct.ap.testutil.compilation.annotation.ExpectedNote;
import org.mapstruct.ap.testutil.compilation.annotation.ProcessorOption;
import org.mapstruct.ap.testutil.runner.Compiler;
import org.mapstruct.ap.testutil.runner.GeneratedSource;

class GeneratedBuilderTest {

    @RegisterExtension
    private final GeneratedSource generatedSource = new GeneratedSource();

    @ProcessorTest(Compiler.JDK) // Eclipse compiler does not support notes.
    @ProcessorOption(name = "nl.loxia.BuilderGenerator.verbose", value = ".")
    @WithClasses({ SimpleClassAllFieldsInConstructor.class, SimpleClassAllFieldsInSetter.class })
    @ExpectedNote("Generating builder for nl.loxia.builder.generator.ap.test.SimpleClassAllFieldsInConstructor")
    @ExpectedNote("Generating builder for nl.loxia.builder.generator.ap.test.SimpleClassAllFieldsInSetter")
    void verbose() throws ClassNotFoundException {
    }

    @ProcessorTest
    @WithClasses(SimpleClassAllFieldsInConstructor.class)
    void simpleClassAllFieldsInConstructor() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(SimpleClassAllFieldsInConstructor.class);
    }

    @ProcessorTest
    @WithClasses(SimpleClassAllFieldsInSetter.class)
    void simpleClassAllFieldsInSetter() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(SimpleClassAllFieldsInSetter.class);
    }

    @ProcessorTest
    @WithClasses(SimpleClassMixed.class)
    void simpleClassMixed() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(SimpleClassMixed.class);
    }

    @ProcessorTest
    @WithClasses(ListHandling.class)
    void listHandling() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(ListHandling.class);
    }

    @ProcessorTest(Compiler.JDK)
    @WithClasses({ ErroneousBuilderWithConstructorFieldMismatch.class, ParentWithoutBuilder.class })
    @ExpectedCompilationOutcome(value = CompilationResult.FAILED,
            diagnostics = {
                @Diagnostic(kind = Kind.WARNING,
                        line = 6l,
                        type = ErroneousBuilderWithConstructorFieldMismatch.class,
                        message = "Parent class 'nl.loxia.builder.generator.ap.test.ParentWithoutBuilder' is not annotated with '@Builder'."),
                @Diagnostic(kind = Kind.ERROR,
                        line = 6l,
                        type = ErroneousBuilderWithConstructorFieldMismatch.class,
                        message = "Cannot generate builder. The fields [field2] do not have a getter, copyOf method cannot be generated. "
                            + "Use `@Builder(copyOf=false)` to disable the copyOf method generation.")
            })
    void errorJdk() {

    }

    @ProcessorTest(Compiler.ECLIPSE)
    @WithClasses({ ErroneousBuilderWithConstructorFieldMismatch.class, ParentWithoutBuilder.class })
    @ExpectedCompilationOutcome(value = CompilationResult.FAILED,
            diagnostics = {
                @Diagnostic(kind = Kind.ERROR,
                        line = 6l,
                        type = ErroneousBuilderWithConstructorFieldMismatch.class,
                        message = "Cannot generate builder. The fields [field2] do not have a getter, copyOf method cannot be generated. "
                            + "Use `@Builder(copyOf=false)` to disable the copyOf method generation.")
            })
    void errorEclipse() {

    }
}
