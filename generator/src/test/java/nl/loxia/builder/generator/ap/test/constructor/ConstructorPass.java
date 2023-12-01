package nl.loxia.builder.generator.ap.test.constructor;

import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;
import nl.loxia.builder.generator.ap.test.BuildableObject;

@Builder
public class ConstructorPass {

    private final String singleValue;
    private final List<String> collection;
    private final BuildableObject singleValueBuildableObject;
    private final List<BuildableObject> collectionBuildableObject;

    public ConstructorPass(ConstructorPassBuilder<?> builder) {
        singleValue = builder.getSingleValue();
        collection = builder.getCollection();
        singleValueBuildableObject = builder.getSingleValueBuildableObject();
        collectionBuildableObject = builder.getCollectionBuildableObject();
    }

    public List<String> getCollection() {
        return collection;
    }

    public String getSingleValue() {
        return singleValue;
    }

    public List<BuildableObject> getCollectionBuildableObject() {
        return collectionBuildableObject;
    }

    public BuildableObject getSingleValueBuildableObject() {
        return singleValueBuildableObject;
    }
}
