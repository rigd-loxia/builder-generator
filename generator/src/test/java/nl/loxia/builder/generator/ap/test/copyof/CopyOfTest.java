package nl.loxia.builder.generator.ap.test.copyof;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

public class CopyOfTest {
    @ProcessorTest
    @WithClasses(CopyOfDisabled.class)
    void builderShouldBeGenerated() {

    }

}
