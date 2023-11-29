package nl.loxia.builder.generator.ap;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.lang.model.type.TypeMirror;

/**
 * The data used to generate a single builder.
 *
 * @author Ben Zegveld
 */
public class BuilderData {

    private final String packageName;
    private final String builderClassName;
    private final SortedSet<Member> members = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));
    private final TypeMirror sourceClassName;
    private final List<String> constructorMembers = new ArrayList<>();
    private final String extendedBuilderName;
    private final SortedSet<BuilderData> innerClasses =
        new TreeSet<>((o1, o2) -> o1.getBuilderClassName().compareTo(o2.getBuilderClassName()));
    private final boolean isAbstract;
    private final boolean extendedBuilderIsAbstract;
    private final BuilderConfiguration builderConfiguration;
    private boolean builderPassingConstructor;

    BuilderData(BuilderDataBuilder builder) {
        packageName = builder.packageName;
        sourceClassName = builder.sourceClassName;
        builderClassName = builder.builderClassName;
        extendedBuilderName = builder.extendedBuilderName;
        extendedBuilderIsAbstract = builder.extendedBuilderIsAbstract;
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
     * @return true if this builder extends another builder.
     */
    public boolean extendsBuilder() {
        return extendedBuilderName != null;
    }

    /**
     * @return true if this builder would be of an abstract class. The builder will then not generate a build method.
     */
    public boolean extendsAbstractBuilder() {
        return extendedBuilderIsAbstract;
    }

    /**
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
    public TypeMirror getSourceClassName() {
        return sourceClassName;
    }

    /**
     * @return true if this builder is based on an abstract class.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * @return true if the copy of method should be generated
     */
    public boolean isCopyOfGenerationEnabled() {
        return builderConfiguration.isCopyOfEnabled();
    }

    /**
     * @param builderPassingConstructor whether or not the constructor accepts the builder
     */
    public void setBuilderPassingConstructor(boolean builderPassingConstructor) {
        this.builderPassingConstructor = builderPassingConstructor;
    }

    /**
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
            .filter(Member::hasSetter)
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

    public static BuilderDataBuilder builder() {
        return new BuilderDataBuilder();
    }

    public static class BuilderDataBuilder {
        private BuilderConfiguration builderConfiguration;
        private boolean isAbstract;
        private boolean extendedBuilderIsAbstract;
        private String extendedBuilderName;
        private String builderClassName;
        private TypeMirror sourceClassName;
        private String packageName;

        private BuilderDataBuilder() {
        }

        public BuilderDataBuilder setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public BuilderDataBuilder setSourceClassName(TypeMirror sourceClassName) {
            this.sourceClassName = sourceClassName;
            return this;
        }

        public BuilderDataBuilder setBuilderClassName(String builderClassName) {
            this.builderClassName = builderClassName;
            return this;
        }

        public BuilderDataBuilder setBuilderConfiguration(BuilderConfiguration builderConfiguration) {
            this.builderConfiguration = builderConfiguration;
            return this;
        }

        public BuilderDataBuilder setAbstract(boolean isAbstract) {
            this.isAbstract = isAbstract;
            return this;
        }

        public BuilderDataBuilder setExtendedBuilderIsAbstract(boolean extendedBuilderIsAbstract) {
            this.extendedBuilderIsAbstract = extendedBuilderIsAbstract;
            return this;
        }

        public BuilderDataBuilder setExtendedBuilderName(String extendedBuilderName) {
            this.extendedBuilderName = extendedBuilderName;
            return this;
        }

        public BuilderData build() {
            return new BuilderData(this);
        }
    }
}
