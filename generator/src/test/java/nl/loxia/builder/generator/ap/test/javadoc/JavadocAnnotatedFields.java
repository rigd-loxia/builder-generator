package nl.loxia.builder.generator.ap.test.javadoc;

import nl.loxia.builder.generator.annotations.Builder;
import nl.loxia.builder.generator.ap.test.BuildableObject;

@Builder
public class JavadocAnnotatedFields {

    /**
     * This field is used as an example for javadoc generation on with methods.
     */
    private BuildableObject object;

    public BuildableObject getObject() {
        return object;
    }

    public void setObject(BuildableObject object) {
        this.object = object;
    }
}
