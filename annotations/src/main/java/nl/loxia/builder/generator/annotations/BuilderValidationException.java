package nl.loxia.builder.generator.annotations;

/**
 * This exception is thrown by the build methods in case there are required fields that are missing.
 */
public class BuilderValidationException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 7058465585875283155L;

    /**
     * Constructor for the validation exception.
     *
     * @param message - the message containing the required fields that are not yet set.
     */
    public BuilderValidationException(String message) {
        super(message);
    }
}
