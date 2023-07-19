package nl.loxia.builder.generator.ap.test.seealso;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class SeeAlsoNotAbstractReferencer {
    public static SeeAlsoNotAbstractReferencerBuilder<Void> builder() {
        return new SeeAlsoNotAbstractReferencerBuilder<>();
    }

    List<SeeAlsoNotAbstractAnnotated> listFields = new ArrayList<>();

    public List<SeeAlsoNotAbstractAnnotated> getListFields() {
        return listFields;
    }

    public void setListFields(List<SeeAlsoNotAbstractAnnotated> listFields) {
        this.listFields = listFields;
    }
}
