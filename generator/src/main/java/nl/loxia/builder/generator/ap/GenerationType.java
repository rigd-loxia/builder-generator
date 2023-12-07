package nl.loxia.builder.generator.ap;

import javax.lang.model.type.TypeMirror;

/**
 * Used to represent a mirror type. Contains methods to retrieve the required information.
 */
public class GenerationType {
    private final String type;
    private final String packageName;
    private final GenerationType subType;

    /**
     * Creates a generation type which represents a class.
     *
     * @param mirror - the typeMirror used to generate a class reference in code.
     * @param packageName - the package in which this type mirror lives.
     */
    public GenerationType(TypeMirror mirror, String packageName) {
        this(mirror, packageName, null);
    }

    /**
     * Creates a generation type which represents a class.
     *
     * @param mirror - the typeMirror used to generate a class reference in code.
     * @param packageName - the package in which this type mirror lives.
     * @param subType - the generationType for the subType, if present.
     */
    public GenerationType(TypeMirror mirror, String packageName, GenerationType subType) {
        type = mirror.toString();
        this.packageName = packageName;
        this.subType = subType;
    }

    /**
     * Creates a generation type which represents a class.
     *
     * @param type - the type used to generate a class reference in code.
     * @param packageName - the package in which this type mirror lives.
     */
    public GenerationType(String type, String packageName) {
        this.type = type;
        this.packageName = packageName;
        subType = null;
    }

    /**
     * This is the fully qualified class name for this type.
     *
     * @return the type for usage in code generation
     */
    public String getType() {
        return type;
    }

    /**
     * This is the fully qualified class name for this type. Filtered as much as possible.
     *
     * @param packageName - the packageName of the builder in which this type is used.
     * @return the type for usage in code generation
     */
    public String getType(String packageName) {
        if (this.packageName.equals(packageName)) {
            return type.substring(packageName.length() + 1);// remove the packageName including the '.' following it.
        }
        else if (this.packageName.equals("java.lang")) {
            return type.substring("java.lang.".length());
        }
        else if (subType != null && subType.getPackageName().equals(packageName)) {
            return type.replaceFirst("<" + packageName.replace(".", "\\.") + "\\.", "<");
        }
        return type;
    }

    /**
     * This is the fully qualified class name for this type without generics.
     *
     * @param packageName - the packageName of the builder in which this type is used.
     * @return the type for usage in code generation
     */
    public String getTypeWithoutGenerics(String packageName) {
        return getType(packageName).replaceAll("<.*>", "");
    }

    /**
     * This is the package name for this type.
     *
     * @return the package name of this type mirror as determined by the java compiler api instead of manually determining this.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * This returns the subtype if present.
     *
     * @return the subtype as a generationType or otherwise null
     */
    public GenerationType getSubType() {
        return subType;
    }
}
