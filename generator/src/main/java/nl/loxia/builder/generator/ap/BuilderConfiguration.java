package nl.loxia.builder.generator.ap;

import nl.loxia.builder.generator.annotations.Builder;

class BuilderConfiguration {
    private final EnvironmentConfiguration environmentConfiguration;

    private final Type typeElement;

    BuilderConfiguration(EnvironmentConfiguration environmentConfiguration, Type typeElement) {
        this.environmentConfiguration = environmentConfiguration;
        this.typeElement = typeElement;
    }

    boolean isCopyOfEnabled() {
        Boolean value = (Boolean) typeElement.getAnnotationValue(Builder.class, "copyOf");
        if (value != null) {
            return value;
        }
        return environmentConfiguration.getCopyOfMethodGeneration();
    }

    public String getMethodPrefix() {
        String value = (String) typeElement.getAnnotationValue(Builder.class, "methodPrefix");
        if (value != null) {
            return value;
        }
        return environmentConfiguration.getMethodPrefix().orElse("with");
    }

    public boolean isBuilderValidationEnabled() {
        Boolean value = (Boolean) typeElement.getAnnotationValue(Builder.class, "validation");
        if (value != null) {
            return value;
        }
        return environmentConfiguration.isBuilderValidationGenerationEnabled();
    }

}
