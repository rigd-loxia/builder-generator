package nl.loxia.builder.generator.ap.test.issues._16;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;
import nl.loxia.builder.generator.ap.test.issues._16.differentpackage.DifferentClass;

@Builder
public class OuterClass {

    public static OuterClassBuilder<Void> builder() {
        return new OuterClassBuilder<>();
    }

    protected List<InnerClass> innerClasses = new ArrayList<>();
    private DifferentClass differentClass;

    public DifferentClass getDifferentClass() {
        return differentClass;
    }

    public void setDifferentClass(DifferentClass differentClass) {
        this.differentClass = differentClass;
    }

    public List<InnerClass> getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(List<InnerClass> innerClasses) {
        this.innerClasses = innerClasses;
    }

    @Builder
    public static class InnerClass {

        public static OuterClassBuilder.InnerClassBuilder<Void> builder() {
            return new OuterClassBuilder.InnerClassBuilder<>();
        }

        protected String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
