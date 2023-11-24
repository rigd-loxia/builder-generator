package nl.loxia.builder.generator.ap.test.issues._16;

import java.util.ArrayList;
import java.util.List;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class OuterClassWithTwoLayersOfInnerClasses {

    public static OuterClassWithTwoLayersOfInnerClassesBuilder<Void> builder() {
        return new OuterClassWithTwoLayersOfInnerClassesBuilder<>();
    }

    protected List<InnerClass> innerClasses = new ArrayList<>();

    public List<InnerClass> getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(List<InnerClass> innerClasses) {
        this.innerClasses = innerClasses;
    }

    @Builder
    public static class InnerClass {

        public static OuterClassWithTwoLayersOfInnerClassesBuilder.InnerClassBuilder<Void> builder() {
            return new OuterClassWithTwoLayersOfInnerClassesBuilder.InnerClassBuilder<>();
        }

        protected InnerInnerClass field;

        public InnerInnerClass getField() {
            return field;
        }

        public void setField(InnerInnerClass field) {
            this.field = field;
        }

        @Builder
        public static class InnerInnerClass {

            public static OuterClassWithTwoLayersOfInnerClassesBuilder.InnerClassBuilder.InnerInnerClassBuilder<Void>
                    builder() {
                return new OuterClassWithTwoLayersOfInnerClassesBuilder.InnerClassBuilder.InnerInnerClassBuilder<>();
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
}
