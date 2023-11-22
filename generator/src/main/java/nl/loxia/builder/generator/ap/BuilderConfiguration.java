package nl.loxia.builder.generator.ap;

import nl.loxia.builder.generator.annotations.Builder;
import nl.loxia.builder.generator.annotations.DefaultBoolean;
import nl.loxia.builder.generator.ap.EnvironmentConfiguration.VariableValue;

class BuilderConfiguration {
    private static final boolean COPY_OF_DEFAULT_VALUE = true;

    private final EnvironmentConfiguration environmentConfiguration;

    private final Type typeElement;

    BuilderConfiguration(EnvironmentConfiguration environmentConfiguration, Type typeElement) {
        this.environmentConfiguration = environmentConfiguration;
        this.typeElement = typeElement;
    }

    boolean isCopyOfEnabled() {
        DefaultBoolean copyOf = typeElement.getAnnotation(Builder.class).copyOf();
        if (copyOf == DefaultBoolean.DEFAULT) {
            if (environmentConfiguration.getCopyOfMethodGeneration() == VariableValue.UNSET) {
                return COPY_OF_DEFAULT_VALUE;
            }
            return environmentConfiguration.getCopyOfMethodGeneration() == VariableValue.TRUE;
        }
        return copyOf == DefaultBoolean.TRUE;
    }

}
