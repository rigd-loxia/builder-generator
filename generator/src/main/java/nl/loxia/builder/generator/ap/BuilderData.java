package nl.loxia.builder.generator.ap;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The data used to generate a single builder.
 *
 * @author Ben Zegveld
 */
public class BuilderData {

    private final String packageName;
    private final String builderClassName;
    private final SortedSet<Member> members = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
    private final GenerationType sourceClassName;
    private final List<String> constructorMembers = new ArrayList<>();
    private final String extendedBuilderName;
    private final SortedSet<BuilderData> innerClasses =
        new TreeSet<>((o1, o2) -> o1.getBuilderClassName().compareTo(o2.getBuilderClassName()));
    private final boolean isAbstract;
    private final BuilderConfiguration builderConfiguration;
    private boolean builderPassingConstructor;

    BuilderData(BuilderDataBuilder builder) {
        packageName = builder.packageName;
        sourceClassName = builder.sourceClassName;
        builderClassName = builder.builderClassName;
        extendedBuilderName = builder.extendedBuilderName;
        isAbstract = builder.isAbstract;
        builderConfiguration = builder.builderConfiguration;
    }

    /**
     * the builder class name
     *
     * @return the class name of this builder.
     */
    public String getBuilderClassName() {
        return builderClassName;
    }

    /**
     * Sometimes you have a hierarchy of classes, and a similar hierarchy with builders. In this case for subclass builders this
     * will return the name of the superclass builder.
     *
     * @return the class name of the builder to extend.
     */
    public String getExtendedBuilderName() {
        return extendedBuilderName;
    }

    /**
     * Sometimes you have a hierarchy of classes, and a similar hierarchy with builders. In this case for subclass builders this
     * will return the name of the superclass builder. This method will return true if this is the case.
     *
     * @return true if this builder extends another builder.
     */
    public boolean extendsBuilder() {
        return extendedBuilderName != null;
    }

    /**
     * The package name for this builder cannot always be logically determined. The package name returned here is what according to
     * the java compiler is the package. <BR>
     * <BR>
     * For example it can handle package names like: I.Do.Not.Follow.Java.Conventions
     *
     * @return the package name for this builder
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * used to add field information to the builder data.
     *
     * @param member - the field information
     */
    public void addMember(Member member) {
        members.add(member);
    }

    /**
     * Retrieves the list of fields that will be made available through the builder.
     *
     * @return the list of members
     */
    public Collection<Member> getMembers() {
        return members;
    }

    /**
     * Retrieves the class that this builder will build.
     *
     * @return the typeMirror representing the class to be build.
     */
    public GenerationType getSourceClassName() {
        return sourceClassName;
    }

    /**
     * used to check if the class for this builder is abstract, in that case the generated builder will also be abstract.
     *
     * @return true if this builder is based on an abstract class.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Sometimes you want to disable the copy of method, because your class does not expose all of it's fields.
     *
     * @return true if the copy of method should be generated
     */
    public boolean isCopyOfGenerationEnabled() {
        return builderConfiguration.isCopyOfEnabled();
    }

    /**
     * Sometimes you want to allow passing null values into a constructor, in that case you want to disable builder validation.
     *
     * @return true if the validation code should be generated.
     */
    public boolean isBuilderValidationEnabled() {
        return builderConfiguration.isBuilderValidationEnabled();
    }

    /**
     * You can use a constructor that accepts the builder as an argument. If this is set to true then that is the case.
     *
     * @param builderPassingConstructor whether or not the constructor accepts the builder
     */
    public void setBuilderPassingConstructor(boolean builderPassingConstructor) {
        this.builderPassingConstructor = builderPassingConstructor;
    }

    /**
     * You can use a constructor that accepts the builder as an argument.<BR>
     * <BR>
     * For example: class MyClass { ... MyClass(MyClassBuilder builder) { ... } ... }
     *
     * @return true if the only argument for the constructor is the builder itself.
     */
    public boolean isBuilderPassingConstructor() {
        return builderPassingConstructor;
    }

    /**
     * Used to determine which fields should be passed into the constructor
     *
     * @param propertyName the constructor argument name
     */
    public void addConstructorArgumentName(String propertyName) {
        constructorMembers.add(propertyName);
    }

    /**
     * Retrieves the list of fields that will be instantiated through the constructor of the class.
     *
     * @return the list of constructor members
     */
    public List<Member> getConstructorMembers() {
        return constructorMembers.stream()
            .map(name -> getMembers().stream().filter(member -> member.getName().equals(name)).findFirst().orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the list of fields that will be instantiated through setter methods in the class.
     *
     * @return the list of members that will be set after the class is created through set methods.
     */
    public List<Member> getSettableMembers() {
        return members.stream()
            .filter(member -> !constructorMembers.contains(member.getName()))
            .filter(member -> !getCollectionMembers().contains(member))
            .filter(member -> member.isUsableForSetAction())
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the list of Collection fields that will be instantiated through adder methods in the class.
     *
     * @return the list of members that will be expanded after the class is created through add methods.
     */
    public List<Member> getCollectionMembers() {
        return members.stream()
            .filter(member -> !constructorMembers.contains(member.getName()))
            .filter(member -> member.getSubType() != null)
            .collect(Collectors.toList());
    }

    /**
     * Used to add information about inner classes.
     *
     * @param builderData - the information about inner classes to add.
     */
    public void addInnerClass(BuilderData builderData) {
        innerClasses.add(builderData);
    }

    /**
     * retrieves the {@link BuilderData} for the inner classes.
     *
     * @return the collection of {@link BuilderData}.
     */
    public SortedSet<BuilderData> getInnerClasses() {
        return innerClasses;
    }

    /**
     * Checks if the builder can generate a valid class.
     *
     * @return false if copy of methods cannot be generated while the generation of them is enabled.
     */
    public boolean isValid() {
        if (!isCopyOfGenerationEnabled()) {
            return true;
        }
        return isValidForCopyOfMethod();
    }

    private boolean isValidForCopyOfMethod() {
        return determineInvalidMembers().isEmpty();
    }

    private List<String> determineInvalidMembers() {
        List<String> invalidMembers;
        if (isBuilderPassingConstructor()) {
            invalidMembers = members.stream()
                .filter(member -> !constructorMembers.contains(member.getName()))
                .filter(t -> !t.hasGetter())
                .map(Member::getName)
                .collect(toList());
        }
        else {
            invalidMembers = constructorMembers.stream()
                .filter(name -> getMembers().stream().noneMatch(member -> member.getName().equals(name) && member.hasGetter()))
                .collect(toList());
        }
        return invalidMembers;
    }

    /**
     * If this builder data object is not valid, then this method will return information for the user as to why it is not valid.
     *
     * @return the validation error tekst for the compilation error when {@link #isValid()} returns false.
     */
    public String getValidationError() {
        if (!isValidForCopyOfMethod()) {
            return String.format(
                "The fields %s do not have a getter, copyOf method cannot be generated. Use `@Builder(copyOf=false)` to disable the copyOf method generation.",
                determineInvalidMembers());
        }
        return "";
    }

    /**
     * creates a new builder for creating a BuilderData object.
     *
     * @return a new builder for creating a BuilderData object.
     */
    public static BuilderDataBuilder builder() {
        return new BuilderDataBuilder();
    }

    /**
     * The Builder class for creating the BuilderData object.
     */
    public static class BuilderDataBuilder {
        private BuilderConfiguration builderConfiguration;
        private boolean isAbstract;
        private String extendedBuilderName;
        private String builderClassName;
        private GenerationType sourceClassName;
        private String packageName;

        private BuilderDataBuilder() {
        }

        /**
         * Used to set the package name.
         *
         * @param packageName - the package name of this builder annotated class
         * @return itself for builder chaining
         */
        public BuilderDataBuilder setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        /**
         * Used to set the generated type for the builder annotated class
         *
         * @param sourceClassName - the builder annotated class
         * @return itself for builder chaining
         */
        public BuilderDataBuilder setSourceClassName(GenerationType sourceClassName) {
            this.sourceClassName = sourceClassName;
            return this;
        }

        /**
         * Used to set how the resulting builder class should be called
         *
         * @param builderClassName - the resulting builder class name
         * @return itself for builder chaining
         */
        public BuilderDataBuilder setBuilderClassName(String builderClassName) {
            this.builderClassName = builderClassName;
            return this;
        }

        /**
         * Used to set the builder configuration
         *
         * @param builderConfiguration - the builderConfiguration containing generation settings
         * @return itself for builder chaining
         */
        public BuilderDataBuilder setBuilderConfiguration(BuilderConfiguration builderConfiguration) {
            this.builderConfiguration = builderConfiguration;
            return this;
        }

        /**
         * Used to set whether or not the source class is abstract.
         *
         * @param isAbstract - whether the source class is abstract or not.
         * @return itself for builder chaining
         */
        public BuilderDataBuilder setAbstract(boolean isAbstract) {
            this.isAbstract = isAbstract;
            return this;
        }

        /**
         * Used to set the builder to extend, if there is no builder then this method should not be used.
         *
         * @param extendedBuilderName - the name of the builder that this builder should extend if any.
         * @return itself for builder chaining
         */
        public BuilderDataBuilder setExtendedBuilderName(String extendedBuilderName) {
            this.extendedBuilderName = extendedBuilderName;
            return this;
        }

        /**
         * Creates a new BuilderData object with information in this builder.
         *
         * @return the BuilderData based on the builder
         */
        public BuilderData build() {
            return new BuilderData(this);
        }
    }
}
