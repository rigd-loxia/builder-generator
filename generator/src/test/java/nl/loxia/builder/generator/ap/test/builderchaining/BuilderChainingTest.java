package nl.loxia.builder.generator.ap.test.builderchaining;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.GeneratedSource;

class BuilderChainingTest {

    @RegisterExtension
    private final GeneratedSource generatedSource = new GeneratedSource();

    @ProcessorTest
    @WithClasses({ BuilderChaining.class, BuilderChainingChild.class })
    void builderChaining() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(BuilderChaining.class);
    }

    @ProcessorTest
    @WithClasses({ BuilderListChaining.class, BuilderChainingChild.class })
    void builderListChaining() throws ClassNotFoundException {
        generatedSource.addComparisonToFixtureFor(BuilderListChaining.class);
    }

    @ProcessorTest
    @WithClasses({ BuilderChaining.class, BuilderChainingChild.class })
    void builderChainingNullValueTest() {
        BuilderChaining original = new BuilderChaining();
        original.setValue(null);

        BuilderChaining copy = BuilderChainingBuilder.copyOf(original).build();

        assertThat(copy).isNotSameAs(original);
        assertThat(copy.getValue()).isNull();
    }
}
