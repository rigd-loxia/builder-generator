package nl.loxia.builder.generator.ap;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;

public class EnvironmentConfiguration {
    private final boolean copyOfMethodGeneration;
    private final Optional<String> methodPrefix;

    public EnvironmentConfiguration(ProcessingEnvironment processingEnv) {
        copyOfMethodGeneration = !"false".equalsIgnoreCase(processingEnv.getOptions().get("nl.loxia.BuilderGenerator.copyOfMethodGeneration"));
        methodPrefix = Optional.ofNullable(processingEnv.getOptions().get("nl.loxia.BuilderGenerator.methodPrefix"));
    }

    public boolean getCopyOfMethodGeneration() {
        return copyOfMethodGeneration;
    }

    public Optional<String> getMethodPrefix() {
        return methodPrefix;
    }

}
