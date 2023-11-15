package nl.loxia.builder.generator.ap;

import javax.annotation.processing.ProcessingEnvironment;

public class EnvironmentConfiguration {
    private final VariableValue copyOfMethodGeneration;

    public enum VariableValue {
        UNSET,
        TRUE,
        FALSE;
    }

    public EnvironmentConfiguration(ProcessingEnvironment processingEnv) {
        copyOfMethodGeneration = determineChoice(processingEnv, "nl.loxia.BuilderGenerator.copyOfMethodGeneration");
    }

    public VariableValue getCopyOfMethodGeneration() {
        return copyOfMethodGeneration;
    }

    private VariableValue determineChoice(ProcessingEnvironment processingEnv, String envVariable) {
        if (!processingEnv.getOptions().containsKey(envVariable)) {
            return VariableValue.UNSET;
        }
        return processingEnv.getOptions().get(envVariable).equalsIgnoreCase("false") ? VariableValue.FALSE : VariableValue.TRUE;
    }

}
