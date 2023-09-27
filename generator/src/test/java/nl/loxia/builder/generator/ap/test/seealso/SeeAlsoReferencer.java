package nl.loxia.builder.generator.ap.test.seealso;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class SeeAlsoReferencer {
    public static SeeAlsoReferencerBuilder<Void> builder() {
        return new SeeAlsoReferencerBuilder<>();
    }

    List<SeeAlsoAnnotated> listFields = new ArrayList<>();

    List<DuplicateField> duplicateFields = new ArrayList<>();

    public List<SeeAlsoAnnotated> getListFields() {
        return listFields;
    }

    public void setListFields(List<SeeAlsoAnnotated> listFields) {
        this.listFields = listFields;
    }

    public List<DuplicateField> getDuplicateFields() {
        return duplicateFields;
    }

    public void setDuplicateFields(List<DuplicateField> duplicateFields) {
        this.duplicateFields = duplicateFields;
    }
}
