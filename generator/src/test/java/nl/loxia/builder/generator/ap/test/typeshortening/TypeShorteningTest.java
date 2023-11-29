package nl.loxia.builder.generator.ap.test.typeshortening;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

public class TypeShorteningTest {

    @ProcessorTest
    @WithClasses(JavaLangTypes.class)
    void shouldShortenSuccesfully() {
    }

    @ProcessorTest
    @WithClasses(JavaLangSubpackageTypes.class)
    void shouldNotShorten() {

    }
}
