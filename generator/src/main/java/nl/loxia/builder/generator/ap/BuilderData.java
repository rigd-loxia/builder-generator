package nl.loxia.builder.generator.ap;

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
    private final List<String> constructorMembers;
    private final String extendedBuilderName;
    private final SortedSet<BuilderData> innerClasses =
        new TreeSet<>((o1, o2) -> o1.getBuilderClassName().compareTo(o2.getBuilderClassName()));
    private final boolean isAbstract;
    private final boolean extendedBuilderIsAbstract;

    BuilderData(String packageName, TypeMirror sourceClassName, String builderClassName,
            String extendedBuilderName,
            boolean extendedBuilderIsAbstract, List<String> constructorMembers, boolean isAbstract) {
        this.packageName = packageName;
        this.sourceClassName = sourceClassName;
        this.builderClassName = builderClassName;
        this.extendedBuilderName = extendedBuilderName;
        this.extendedBuilderIsAbstract = extendedBuilderIsAbstract;
        this.constructorMembers = constructorMembers;
        this.isAbstract = isAbstract;
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
     * @return false if not all constructor arguments are represented as members.
     */
    public boolean isValide() {
        return constructorMembers.stream()
            .allMatch(name -> getMembers().stream().anyMatch(member -> member.getName().equals(name)));
    }
}
