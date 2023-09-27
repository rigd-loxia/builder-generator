package nl.loxia.builder.generator.ap.test.seealso;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.mapstruct.ap.testutil.ProcessorTest;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.GeneratedSource;

class SeeAlsoTest {

    @RegisterExtension
    private final GeneratedSource generatedSource = new GeneratedSource();

    @ProcessorTest
    @WithClasses({ SeeAlso.class, SeeAlsoAnnotated.class, SeeAlsoAnnotated2.class, SeeAlsoReferencer.class, SeeAlsoTarget.class,
        DuplicateField.class })
    void seeAlsoTests() {
        generatedSource.forBuilder(SeeAlsoReferencer.class)
            .content()
            .contains("addSeeAlsoTarget")
            .doesNotContain("withSeeAlsoTarget")
            .doesNotContain("addSeeAlsoAnnotated")
            .doesNotContain("withSeeAlsoAnnotated")
            .doesNotContain(
                "    public DuplicateFieldBuilder<? extends SeeAlsoReferencerBuilder<PARENT>> addDuplicateFields() {"
                    + System.lineSeparator()
                    + "        DuplicateFieldBuilder<SeeAlsoReferencerBuilder<PARENT>> child = new DuplicateFieldBuilder<>(this);"
                    + System.lineSeparator()
                    + "        listFieldsBuilders.add(child);"
                    + System.lineSeparator()
                    + "        return child;"
                    + System.lineSeparator()
                    + "    }"
                    + System.lineSeparator()
                    + "")
            .contains(
                "    public DuplicateFieldBuilder<? extends SeeAlsoReferencerBuilder<PARENT>> addDuplicateFields() {"
                    + System.lineSeparator()
                    + "        DuplicateFieldBuilder<SeeAlsoReferencerBuilder<PARENT>> child = new DuplicateFieldBuilder<>(this);"
                    + System.lineSeparator()
                    + "        duplicateFieldsBuilders.add(child);"
                    + System.lineSeparator()
                    + "        return child;"
                    + System.lineSeparator()
                    + "    }"
                    + System.lineSeparator()
                    + "");
    }

    @ProcessorTest
    @WithClasses({ SeeAlso.class, SeeAlsoAnnotated.class, SeeAlsoAnnotated2.class, SeeAlsoReferencer.class, SeeAlsoTarget.class,
        DuplicateField.class })
    void copyOfCollectionsTest() {
        SeeAlsoReferencer referencer = new SeeAlsoReferencer();
        referencer.duplicateFields.add(new DuplicateFieldBuilder<>().withDup("a").withVal("b").build());

        SeeAlsoReferencer copy = SeeAlsoReferencerBuilder.copyOf(referencer).build();

        assertThat(copy.getDuplicateFields().get(0).getDup()).isEqualTo(referencer.getDuplicateFields().get(0).getDup());
        assertThat(copy.getDuplicateFields().get(0).getVal()).isEqualTo(referencer.getDuplicateFields().get(0).getVal());
        assertThat(copy.getDuplicateFields().get(0)).isNotSameAs(referencer.getDuplicateFields().get(0));
    }

    @ProcessorTest
    @WithClasses({ SeeAlso.class, SeeAlsoNotAbstractAnnotated.class, SeeAlsoNotAbstractField1.class,
        SeeAlsoNotAbstractField2.class, SeeAlsoNotAbstractReferencer.class })
    void seeAlsoCollectionWithNotAbstractType() {
        SeeAlsoNotAbstractReferencer referencer = new SeeAlsoNotAbstractReferencer();
        referencer.listFields.add(new SeeAlsoNotAbstractField1Builder<>().withField1("a").withBase("b").build());

        SeeAlsoNotAbstractReferencer copy = SeeAlsoNotAbstractReferencerBuilder.copyOf(referencer).build();

        assertThat(copy.getListFields().get(0).getBase()).isEqualTo(referencer.getListFields().get(0).getBase());
        assertThat(((SeeAlsoNotAbstractField1) copy.getListFields().get(0)).getField1())
            .isEqualTo(((SeeAlsoNotAbstractField1) referencer.getListFields().get(0)).getField1());
        assertThat(copy.getListFields().get(0)).isNotSameAs(referencer.getListFields().get(0));
    }
}
