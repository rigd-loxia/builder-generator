package nl.loxia.builder.generator.ap.test.typeshortening;

import nl.loxia.builder.generator.annotations.Builder;

@Builder
public class JavaLangSubpackageTypes {

    private java.lang.annotation.Annotation annotation;

    public java.lang.annotation.Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(java.lang.annotation.Annotation annotation) {
        this.annotation = annotation;
    }
}
