package nl.loxia.builder.generator.ap.test.builderinheritence;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class BuilderInnerClass {
    InnerClass value;

    public InnerClass getValue() {
        return value;
    }

    public void setValue(InnerClass value) {
        this.value = value;
    }

    @Builder
    public static class InnerClass {
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
