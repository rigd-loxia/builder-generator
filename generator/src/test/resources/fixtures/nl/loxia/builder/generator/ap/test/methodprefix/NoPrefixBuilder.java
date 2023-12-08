package nl.loxia.builder.generator.ap.test.methodprefix;

/**
 * Generated by RIGD-Loxia Builder generator
 */
public class NoPrefixBuilder<PARENT> {
    private boolean booleanField;
    private String stringField;

    private PARENT parent;

    public NoPrefixBuilder() {
    }

    public NoPrefixBuilder(PARENT parent) {
        this.parent = parent;
    }

    public NoPrefixBuilder<PARENT> booleanField(boolean booleanField) {
        this.booleanField = booleanField;
        return this;
    }

    public NoPrefixBuilder<PARENT> stringField(String stringField) {
        this.stringField = stringField;
        return this;
    }

    /**
     * returns the build object. For builder chaining use the {@link #end()} method to return the parent builder.
     */
    public NoPrefix build() {
        java.util.List<String> missingRequiredFields = new java.util.ArrayList<>();
        if (stringField == null) {
            missingRequiredFields.add("stringField");
        }
        if (!missingRequiredFields.isEmpty()) {
            throw new nl.loxia.builder.generator.annotations.BuilderValidationException("The following required fields are not set: "
                      + missingRequiredFields.stream().collect(java.util.stream.Collectors.joining(",")));
        }
        NoPrefix result = new NoPrefix(stringField);
        result.setBooleanField(booleanField);
        return result;
    }

    /**
     * returns the parent builder if present, otherwise null is returned.
     */
    public PARENT end() {
        return parent;
    }

    public static NoPrefixBuilder<Void> copyOf(NoPrefix bron) {
        if (bron == null) {
            return null;
        }
        NoPrefixBuilder<Void> builder = new NoPrefixBuilder<>();
        builder.booleanField = bron.getBooleanField();
        builder.stringField = bron.getStringField();
        return builder;
    }

    public static <T> NoPrefixBuilder<T> copyOf(NoPrefix bron, T parentBuilder) {
        if (bron == null) {
            return null;
        }
        NoPrefixBuilder<T> builder = new NoPrefixBuilder<>(parentBuilder);
        builder.booleanField = bron.getBooleanField();
        builder.stringField = bron.getStringField();
        return builder;
    }
}