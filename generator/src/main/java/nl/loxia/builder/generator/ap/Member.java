package nl.loxia.builder.generator.ap;

import java.util.Collections;
import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * This data represents a field in a class.
 *
 * @author Ben Zegveld
 */
public class Member {

    private final TypeMirror type;
    private final String name;
    private final TypeMirror subType;
    private final boolean hasBuilder;
    private final boolean inherited;
    private final TypeMirror outerType;
    private final List<Alias> aliases;
    private final boolean isAbstract;

    private Member(Member.Builder builder) {
        type = builder.type;
        isAbstract = builder.isAbstract;
        subType = builder.subType;
        hasBuilder = builder.hasBuilder;
        name = builder.name;
        inherited = builder.inherited;
        outerType = builder.outerType;
        aliases = Collections.unmodifiableList(builder.aliases);
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
     * The type of the field.
     *
     * @return the type of the field.
     */
    public TypeMirror getType() {
        return type;
    }

    /**
     * Sometimes a field also has a generic subType.
     *
     * @return the subType of this field if present, otherwise null.
     */
    public TypeMirror getSubType() {
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
    public TypeMirror getOuterType() {
        return outerType;
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
        private TypeMirror type;
        private String name;
        private TypeMirror subType;
        private boolean hasBuilder;
        private boolean inherited;
        private TypeMirror outerType;
        private boolean isAbstract;
        private List<Alias> aliases = Collections.emptyList();

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
         * @param outerType defines the class that contains the member's type.
         * @return itself for chaining.
         */
        public Builder outerType(TypeMirror outerType) {
            this.outerType = outerType;
            return this;
        }

        /**
         * Configure the member's subType.
         *
         * @param subType of the member.
         * @return itself for chaining.
         */
        public Builder subType(TypeMirror subType) {
            this.subType = subType;
            return this;
        }

        /**
         * Configure the member's type.
         *
         * @param type of the member.
         * @return itself for chaining.
         */
        public Builder type(TypeMirror type) {
            this.type = type;
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
         * @return the build Member.
         */
        public Member build() {
            return new Member(this);
        }
    }
}
