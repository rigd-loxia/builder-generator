package nl.loxia.builder.generator.ap.test.validation;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

import nl.loxia.builder.generator.annotations.BuilderValidationException;

public class ValidationTest {

    @ProcessorTest
    @WithClasses(ValidationSingleConstructorArgument.class)
    void validationWithSingleArgument() {
        ValidationSingleConstructorArgumentBuilder<?> builder = new ValidationSingleConstructorArgumentBuilder<>();

        assertThatThrownBy(builder::build)
            .isInstanceOf(BuilderValidationException.class)
            .hasMessageContaining("required");
        builder.withRequired("reqField");
        assertThatNoException().isThrownBy(builder::build);
    }

    @ProcessorTest
    @WithClasses(ValidationSingleConstructorArgument.class)
    void validationWithMultipleArgument() {
        ValidationMultipleConstructorArgumentBuilder<?> builder = new ValidationMultipleConstructorArgumentBuilder<>();

        assertThatThrownBy(builder::build)
            .isInstanceOf(BuilderValidationException.class)
            .hasMessageContaining("otherRequired")
            .hasMessageContaining("required");
        builder.withOtherRequired("reqField");
        assertThatThrownBy(builder::build)
            .isInstanceOf(BuilderValidationException.class)
            .hasMessageContaining("required");
        builder.withRequired("reqField");
        assertThatNoException().isThrownBy(builder::build);
    }

    @ProcessorTest
    @WithClasses(ValidationSingleConstructorArgument.class)
    void validationWithMultipleArgumentOtherOrderOfSetting() {
        ValidationMultipleConstructorArgumentBuilder<?> builder = new ValidationMultipleConstructorArgumentBuilder<>();

        assertThatThrownBy(builder::build)
            .isInstanceOf(BuilderValidationException.class)
            .hasMessageContaining("otherRequired")
            .hasMessageContaining("required");
        builder.withRequired("reqField");
        assertThatThrownBy(builder::build)
            .isInstanceOf(BuilderValidationException.class)
            .hasMessageContaining("otherRequired");
        builder.withOtherRequired("reqField");
        assertThatNoException().isThrownBy(builder::build);
    }
}
