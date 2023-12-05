package nl.loxia.builder.generator.ap.test.javadoc;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;
import nl.loxia.builder.generator.ap.test.BuildableObject;
import nl.loxia.builder.generator.ap.test.seealso.SeeAlsoAnnotated;

@Builder
public class JavadocAnnotatedFields {

    /**
     * This field is used as an example for javadoc generation on with methods.
     */
    private BuildableObject object;

    /**
     * List field with javadoc.
     */
    private final List<BuildableObject> list = new ArrayList<>();

    /**
     * See also field with javadoc.
     */
    private final List<SeeAlsoAnnotated> seeAlso = new ArrayList<>();

    public BuildableObject getObject() {
        return object;
    }

    public void setObject(BuildableObject object) {
        this.object = object;
    }

    public List<BuildableObject> getList() {
        return list;
    }

    public List<SeeAlsoAnnotated> getSeeAlso() {
        return seeAlso;
    }
}
