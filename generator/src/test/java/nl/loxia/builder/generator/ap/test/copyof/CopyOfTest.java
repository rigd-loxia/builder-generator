package nl.loxia.builder.generator.ap.test.copyof;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.ProcessorOption;

public class CopyOfTest {
    @ProcessorTest
    @WithClasses(CopyOfDisabled.class)
    void builderShouldBeGenerated() {

    }

    @ProcessorTest
    @ProcessorOption(name = "nl.loxia.BuilderGenerator.copyOfMethodGeneration", value = "false")
    @WithClasses(ErroneousCopyOfWithoutCompilerOption.class)
    void builderShouldBeGeneratedWithCompilerOption() {

    }

    @ProcessorTest
    @ProcessorOption(name = "nl.loxia.BuilderGenerator.copyOfMethodGeneration", value = "true")
    @WithClasses(CopyOfDisabled.class)
    void specificTakesPrecedentOverGeneric() {

    }

}
