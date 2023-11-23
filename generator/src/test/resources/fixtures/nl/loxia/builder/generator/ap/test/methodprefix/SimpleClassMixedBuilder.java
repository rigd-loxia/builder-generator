package nl.loxia.builder.generator.ap.test.methodprefix;

/**
 * Generated by RIGD-Loxia Builder generator
 */
public class SimpleClassMixedBuilder<PARENT> {
    private boolean booleanField;
    private String stringField;

    private PARENT parent;

    public SimpleClassMixedBuilder() {
    }

    public SimpleClassMixedBuilder(PARENT parent) {
        this.parent = parent;
    }

    public SimpleClassMixedBuilder<PARENT> witherBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
        return this;
    }

    public SimpleClassMixedBuilder<PARENT> witherStringField(String stringField) {
        this.stringField = stringField;
        return this;
    }

    public SimpleClassMixed build() {
        SimpleClassMixed result = new SimpleClassMixed(stringField);
        result.setBooleanField(booleanField);
        return result;
    }

    public PARENT end() {
        return parent;
    }

    public static SimpleClassMixedBuilder<Void> copyOf(SimpleClassMixed bron) {
        if (bron == null) {
            return null;
        }
        SimpleClassMixedBuilder<Void> builder = new SimpleClassMixedBuilder<>();
        builder.booleanField = bron.getBooleanField();
        builder.stringField = bron.getStringField();
        return builder;
    }

    public static <T> SimpleClassMixedBuilder<T> copyOf(SimpleClassMixed bron, T parentBuilder) {
        if (bron == null) {
            return null;
        }
        SimpleClassMixedBuilder<T> builder = new SimpleClassMixedBuilder<>(parentBuilder);
        builder.booleanField = bron.getBooleanField();
        builder.stringField = bron.getStringField();
        return builder;
    }
}