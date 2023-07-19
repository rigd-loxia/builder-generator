package nl.loxia.builder.generator.ap;

import javax.lang.model.type.TypeMirror;

/**
 * Defines an alias for a type. Sometimes you have fields that refer to abstract/parent type while the actual implementation options
 * vary. This class handles the different options that can be used as implementations.
 *
 * @author Ben Zegveld
 */
public class Alias {

    private final TypeMirror type;
    private final String name;

    /**
     * The constructor.
     *
     * @param type - the type this alias uses.
     * @param name - the method name to use when making this implementation available.
     */
    public Alias(TypeMirror type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * gets the Type of the alias.
     *
     * @return Type of the alias.
     */
    public TypeMirror getType() {
        return type;
    }

    /**
     * gets the Name of the alias.
     *
     * @return Name of the alias.
     */
    public String getName() {
        return name;
    }
}
