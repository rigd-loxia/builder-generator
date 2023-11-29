package nl.loxia.builder.generator.ap.test.constructor;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;

public class ConstructorPassTest {

    @ProcessorTest
    @WithClasses({ BuildableObject.class, ConstructorPass.class })
    void withBuilderChaining() {
        ConstructorPassBuilder<Void> constructorPassBuilder = new ConstructorPassBuilder<>();
        constructorPassBuilder.withSingleValue("test");
        constructorPassBuilder.withCollection(List.of("testcol"));
        constructorPassBuilder.withSingleValueBuildableObject().withVal("val");
        constructorPassBuilder.addCollectionBuildableObject().withVal("colVal");

        ConstructorPass build = constructorPassBuilder.build();

        assertThat(build.getSingleValue()).isEqualTo("test");
        assertThat(build.getCollection()).containsExactly("testcol");
        assertThat(build.getSingleValueBuildableObject().getVal()).isEqualTo("val");
        assertThat(build.getCollectionBuildableObject())
            .extracting(BuildableObject::getVal)
            .containsExactly("colVal");
    }

    @ProcessorTest
    @WithClasses({ BuildableObject.class, ConstructorPass.class })
    void withoutBuilderChaining() {
        ConstructorPassBuilder<Void> constructorPassBuilder = new ConstructorPassBuilder<>();
        constructorPassBuilder.withSingleValue("test");
        constructorPassBuilder.withCollection(List.of("testcol"));
        constructorPassBuilder.withSingleValueBuildableObject(new BuildableObjectBuilder<>().withVal("val").build());
        constructorPassBuilder.addCollectionBuildableObject(new BuildableObjectBuilder<>().withVal("colVal").build());

        ConstructorPass build = constructorPassBuilder.build();

        assertThat(build.getSingleValue()).isEqualTo("test");
        assertThat(build.getCollection()).containsExactly("testcol");
        assertThat(build.getSingleValueBuildableObject().getVal()).isEqualTo("val");
        assertThat(build.getCollectionBuildableObject())
            .extracting(BuildableObject::getVal)
            .containsExactly("colVal");
    }
}
