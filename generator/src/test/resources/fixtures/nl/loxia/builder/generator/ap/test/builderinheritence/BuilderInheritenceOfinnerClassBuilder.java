package nl.loxia.builder.generator.ap.test.builderinheritence;

/**
 * Generated by RIGD-Loxia Builder generator
 */
public class BuilderInheritenceOfinnerClassBuilder<PARENT> extends BuilderInnerClassBuilder<PARENT> {
    private int otherValue;
    private BuilderInnerClass.InnerClass value;
    private BuilderInnerClassBuilder.InnerClassBuilder<BuilderInheritenceOfinnerClassBuilder<PARENT>> valueBuilder;

    public BuilderInheritenceOfinnerClassBuilder() {
    }

    public BuilderInheritenceOfinnerClassBuilder(PARENT parent) {
        super(parent);
    }

    public BuilderInheritenceOfinnerClassBuilder<PARENT> withOtherValue(int otherValue) {
        this.otherValue = otherValue;
        return this;
    }

    @Override
    public BuilderInheritenceOfinnerClassBuilder<PARENT> withValue(BuilderInnerClass.InnerClass value) {
        this.value = value;
        valueBuilder = null;
        return this;
    }

    @Override
    /**
     * returns a builder for chaining. Use the end() method to return back to the current builder.
     */
    public BuilderInnerClassBuilder.InnerClassBuilder<? extends BuilderInheritenceOfinnerClassBuilder<PARENT>> withValue() {
        if (valueBuilder == null) {
            valueBuilder = new BuilderInnerClassBuilder.InnerClassBuilder<>(this);
            value = null;
        }
        return valueBuilder;
    }

    /**
     * returns the build object. For builder chaining use the {@link #end()} method to return the previous builder.
     */
    @Override
    public BuilderInheritenceOfinnerClass build() {
        BuilderInheritenceOfinnerClass result = new BuilderInheritenceOfinnerClass();
        result.setOtherValue(otherValue);
        result.setValue(valueBuilder != null ? valueBuilder.build() : value);
        return result;
    }

    public static BuilderInheritenceOfinnerClassBuilder<Void> copyOf(BuilderInheritenceOfinnerClass bron) {
        if (bron == null) {
            return null;
        }
        BuilderInheritenceOfinnerClassBuilder<Void> builder = new BuilderInheritenceOfinnerClassBuilder<>();
        builder.otherValue = bron.getOtherValue();
        builder.valueBuilder = BuilderInnerClassBuilder.InnerClassBuilder.copyOf(bron.getValue(), builder);
        return builder;
    }

    public static <T> BuilderInheritenceOfinnerClassBuilder<T> copyOf(BuilderInheritenceOfinnerClass bron, T parentBuilder) {
        if (bron == null) {
            return null;
        }
        BuilderInheritenceOfinnerClassBuilder<T> builder = new BuilderInheritenceOfinnerClassBuilder<>(parentBuilder);
        builder.otherValue = bron.getOtherValue();
        builder.valueBuilder = BuilderInnerClassBuilder.InnerClassBuilder.copyOf(bron.getValue(), builder);
        return builder;
    }
}