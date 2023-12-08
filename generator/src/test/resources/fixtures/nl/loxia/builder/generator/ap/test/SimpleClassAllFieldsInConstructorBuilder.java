package nl.loxia.builder.generator.ap.test;

/**
 * Generated by RIGD-Loxia Builder generator
 */
public class SimpleClassAllFieldsInConstructorBuilder<PARENT> {
    private Boolean booleanField;
    private String stringField;

    private PARENT parent;

    public SimpleClassAllFieldsInConstructorBuilder() {
    }

    public SimpleClassAllFieldsInConstructorBuilder(PARENT parent) {
        this.parent = parent;
    }

    public SimpleClassAllFieldsInConstructorBuilder<PARENT> withBooleanField(Boolean booleanField) {
        this.booleanField = booleanField;
        return this;
    }

    public SimpleClassAllFieldsInConstructorBuilder<PARENT> withStringField(String stringField) {
        this.stringField = stringField;
        return this;
    }

    /**
     * returns the build object. For builder chaining use the {@link #end()} method to return the parent builder.
     */
    public SimpleClassAllFieldsInConstructor build() {
        java.util.List<String> missingRequiredFields = new java.util.ArrayList<>();
        if (stringField == null) {
            missingRequiredFields.add("stringField");
        }
        if (booleanField == null) {
            missingRequiredFields.add("booleanField");
        }
        if (!missingRequiredFields.isEmpty()) {
            throw new nl.loxia.builder.generator.annotations.BuilderValidationException("The following required fields are not set: "
                      + missingRequiredFields.stream().collect(java.util.stream.Collectors.joining(",")));
        }
        SimpleClassAllFieldsInConstructor result = new SimpleClassAllFieldsInConstructor(stringField, booleanField);
        return result;
    }

    /**
     * returns the parent builder if present, otherwise null is returned.
     */
    public PARENT end() {
        return parent;
    }

    public static SimpleClassAllFieldsInConstructorBuilder<Void> copyOf(SimpleClassAllFieldsInConstructor bron) {
        if (bron == null) {
            return null;
        }
        SimpleClassAllFieldsInConstructorBuilder<Void> builder = new SimpleClassAllFieldsInConstructorBuilder<>();
        builder.booleanField = bron.getBooleanField();
        builder.stringField = bron.getStringField();
        return builder;
    }

    public static <T> SimpleClassAllFieldsInConstructorBuilder<T> copyOf(SimpleClassAllFieldsInConstructor bron, T parentBuilder) {
        if (bron == null) {
            return null;
        }
        SimpleClassAllFieldsInConstructorBuilder<T> builder = new SimpleClassAllFieldsInConstructorBuilder<>(parentBuilder);
        builder.booleanField = bron.getBooleanField();
        builder.stringField = bron.getStringField();
        return builder;
    }
}