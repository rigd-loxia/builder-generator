package nl.loxia.builder.generator.ap;

import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * This class contains all the options that can be set on the compiler.
 */
public class EnvironmentConfiguration {
    private final boolean copyOfMethodGeneration;
    private final Optional<String> methodPrefix;

    /**
     * @param processingEnv - The processing environment used by the compiler.
     */
    public EnvironmentConfiguration(ProcessingEnvironment processingEnv) {
        copyOfMethodGeneration =
            !"false".equalsIgnoreCase(processingEnv.getOptions().get("nl.loxia.BuilderGenerator.copyOfMethodGeneration"));
        methodPrefix = Optional.ofNullable(processingEnv.getOptions().get("nl.loxia.BuilderGenerator.methodPrefix"));
    }

    /**
     * @return false if the copyOfMethodGeneration option is set to false, returns true otherwise.
     */
    public boolean getCopyOfMethodGeneration() {
        return copyOfMethodGeneration;
    }

    /**
     * @return An optional value, if the methodPrefix option is supplied then the optional is filled with this value, otherwise it
     *         is empty.
     */
    public Optional<String> getMethodPrefix() {
        return methodPrefix;
    }

}
