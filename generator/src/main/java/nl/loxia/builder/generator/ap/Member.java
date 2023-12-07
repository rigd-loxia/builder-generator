package nl.loxia.builder.generator.ap;

import java.util.Collections;
import java.util.List;

/**
 * This data represents a field in a class.
 *
 * @author Ben Zegveld
 */
public class Member {

    private final GenerationType type;
    private final String name;
    private final GenerationType subType;
    private final boolean hasBuilder;
    private final boolean inherited;
    private final List<GenerationType> outerTypes;
    private final List<Alias> aliases;
    private final boolean isAbstract;
    private final boolean hasGetter;
    private final boolean hasSetter;
    private final GenerationType subBuilderClassName;
    private final String methodName;
    private final String javadoc;
    private final boolean isPrivate;

    private Member(Member.Builder builder) {
        type = builder.type;
        isAbstract = builder.isAbstract;
        subType = builder.subType;
        hasBuilder = builder.hasBuilder;
        hasGetter = builder.hasGetter;
        hasSetter = builder.hasSetter;
        name = builder.name;
        inherited = builder.inherited;
        outerTypes = builder.outerTypes;
        subBuilderClassName = builder.subBuilderClassName;
        aliases = Collections.unmodifiableList(builder.aliases);
        methodName = builder.methodName;
        javadoc = builder.javadoc;
        isPrivate = builder.isPrivate;
    }

    /**
     * The name of the field.
     *
     * @return the name of the field.
     */
    public String getName() {
        return name;
    }

    /**
     * the method name to be used for generation.
     *
     * @return the method name to be used for generation.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * The type of the field.
     *
     * @return the type of the field.
     */
    public GenerationType getType() {
        return type;
    }

    /**
     * Sometimes a field also has a generic subType.
     *
     * @return the subType of this field if present, otherwise null.
     */
    public GenerationType getSubType() {
        return subType;
    }

    /**
     * Sometimes a field also has a generic subType.
     *
     * @return true if a generic subType is present.
     */
    public boolean hasSubType() {
        return subType != null;
    }

    /**
     * a List always has a generic subType.
     *
     * @return true if this field represents a List.
     */
    public boolean isList() {
        return hasSubType();
    }

    /**
     * To support builder chaining we need to know if the targeted type also has a builder.
     *
     * @return true if the type of this field also has a builder.
     */
    public boolean hasBuilder() {
        return hasBuilder;
    }

    /**
     * This can be used to determine whether a copy constructor is possible or not.
     *
     * @return true if a getter method is available
     */
    public boolean hasGetter() {
        return hasGetter;
    }

    /**
     * This can be used to determine whether a set action is possible in the build method.
     *
     * @return true if a setter method is available
     */
    public boolean hasSetter() {
        return hasSetter;
    }

    /**
     * To make it easier for inheritence, all members of the parent class are also added as a member. If the member is inherited
     * then we need to generate an `@Override` annotation.
     *
     * @return true if the field is defined in a parent class.
     */
    public boolean isInherited() {
        return inherited;
    }

    /**
     * Retrieves the surrounding class name.
     *
     * @return the surrounding class name.
     */
    public List<GenerationType> getOuterTypes() {
        return outerTypes;
    }

    /**
     * Retrieves the builder class name of the subtype.
     *
     * @return the builder class name of the generic types item from the list.
     */
    public GenerationType getSubBuilderClassName() {
        return subBuilderClassName;
    }

    /**
     * For chaining you sometimes need to know that a certain field is implemented in different ways. If there is a `SeeAlso`
     * annotation present then this will return true.
     *
     * @return true if there are aliases.
     */
    public boolean hasAliases() {
        return !aliases.isEmpty();
    }

    /**
     * For chaining you sometimes need to know that a certain field is implemented in different ways. If there is a `SeeAlso`
     * annotation present then this will return true.
     *
     * @return the list of implementations possible for this field.
     */
    public List<Alias> getAliases() {
        return aliases;
    }

    /**
     * Sometimes you get an abstract property, in this case we will generate the template for the builder.
     *
     * @return if the property is abstract.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * If a field is annotated with javadoc, then this is transferred to the builder.
     *
     * @return the javadoc of this member.
     */
    public String getJavadoc() {
        return javadoc;
    }

    /**
     * checks if the field is private and without setter, then it cannot be used except by being filled in the constructor.
     *
     * @return true if this member is ussable for builder generation.
     */
    public boolean isUsableForSetAction() {
        return hasSetter || !isPrivate;
    }

    /**
     * A new builder for creating a member.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to create a member.
     *
     * @author zegveb
     */
    public static class Builder {
        private boolean isPrivate;
        private String javadoc;
        private GenerationType type;
        private String name;
        private GenerationType subType;
        private boolean hasBuilder;
        private boolean hasGetter;
        private boolean inherited;
        private List<GenerationType> outerTypes;
        private boolean isAbstract;
        private GenerationType subBuilderClassName;
        private List<Alias> aliases = Collections.emptyList();
        private String methodName;
        private boolean hasSetter;

        private Builder() {
            // alleen via Member.builder() aan te maken.
        }

        /**
         * Configure the member to be abstract.
         *
         * @param isAbstract defines if the member is abstract.
         * @return itself for chaining.
         */
        public Builder isAbstract(boolean isAbstract) {
            this.isAbstract = isAbstract;
            return this;
        }

        /**
         * Configure the member type can be constructed using a builder.
         *
         * @param hasBuilder defines if builderchaining is possible.
         * @return itself for chaining.
         */
        public Builder hasBuilder(boolean hasBuilder) {
            this.hasBuilder = hasBuilder;
            return this;
        }

        /**
         * Configure if the member type has a getter method
         *
         * @param hasGetter defines if the member has a getter method.
         * @return itself for chaining
         */
        public Builder hasGetter(boolean hasGetter) {
            this.hasGetter = hasGetter;
            return this;
        }

        /**
         * Configure if the member type has a setter method
         *
         * @param hasSetter defines if the member has a setter method.
         * @return itself for chaining
         */
        public Builder hasSetter(boolean hasSetter) {
            this.hasSetter = hasSetter;
            return this;
        }

        /**
         * Configure the member is inherited from a parent class.
         *
         * @param inherited defines if the member is inherited.
         * @return itself for chaining.
         */
        public Builder inherited(boolean inherited) {
            this.inherited = inherited;
            return this;
        }

        /**
         * Configure the member name.
         *
         * @param name defines the members name.
         * @return itself for chaining.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Configure the member's type is located inside an inner class.
         *
         * @param outerTypes defines the class that contains the member's type.
         * @return itself for chaining.
         */
        public Builder outerTypes(List<GenerationType> outerTypes) {
            this.outerTypes = outerTypes;
            return this;
        }

        /**
         * Configure the member's subType.
         *
         * @param subType of the member.
         * @return itself for chaining.
         */
        public Builder subType(GenerationType subType) {
            this.subType = subType;
            return this;
        }

        /**
         * Configure the member's type.
         *
         * @param type of the member.
         * @return itself for chaining.
         */
        public Builder type(GenerationType type) {
            this.type = type;
            return this;
        }

        /**
         * Configure the member's classname of the subtype.
         *
         * @param subBuilderClassName - the builder classname of the subtype.
         * @return itself for chaining.
         */
        public Builder subBuilderClassName(GenerationType subBuilderClassName) {
            this.subBuilderClassName = subBuilderClassName;
            return this;
        }

        /**
         * Configure the member's aliases.
         *
         * @param aliases of the member.
         * @return itself for chaining.
         */
        public Builder setAliases(List<Alias> aliases) {
            this.aliases = aliases;
            return this;
        }

        /**
         * Configure the member's builder method name.
         *
         * @param methodName to be used in the generated builder.
         * @return itself for chaining.
         */
        public Builder setBuilderMethod(String methodName) {
            this.methodName = methodName;
            return this;
        }

        /**
         * Sets the javadoc that should be inherited to this builder method
         *
         * @param javadoc - the javadoc that is on the field.
         * @return itself for chaining
         */
        public Builder javadoc(String javadoc) {
            this.javadoc = javadoc;
            return this;
        }

        /**
         * Configures if the member is a private member.
         *
         * @param isPrivate - if the member is a private member.
         * @return itself for chaining
         */
        public Builder isPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        /**
         * creates a new {@link Member} based on the information in this builder.
         *
         * @return the build Member.
         */
        public Member build() {
            return new Member(this);
        }
    }
}
