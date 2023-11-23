package nl.loxia.builder.generator.ap.test.methodprefix;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.compilation.annotation.ProcessorOption;
import org.mapstruct.ap.testutil.runner.GeneratedSource;

class MethodPrefixTest {

    @RegisterExtension
    private final GeneratedSource generatedSource = new GeneratedSource();

    @ProcessorTest
    @WithClasses(NoPrefix.class)
    void noPrefix() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(NoPrefix.class);
    }

    @ProcessorTest
    @ProcessorOption(name = "nl.loxia.BuilderGenerator.methodPrefix", value = "wither")
    @WithClasses(SetPrefix.class)
    void setPrefix() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(SetPrefix.class);
    }

    @ProcessorTest
    @ProcessorOption(name = "nl.loxia.BuilderGenerator.methodPrefix", value = "wither")
    @WithClasses(SimpleClassMixed.class)
    void processorArgument() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(SimpleClassMixed.class);
    }
}
