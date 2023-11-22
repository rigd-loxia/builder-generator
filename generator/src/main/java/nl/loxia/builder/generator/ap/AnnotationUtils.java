package nl.loxia.builder.generator.ap;

import javax.lang.model.element.AnnotationMirror;

public interface AnnotationUtils {

    String SEE_ALSO = "SeeAlso";

    static boolean isSeeAlsoAnnotation(AnnotationMirror mirror) {
        return isAnnotationWithName(mirror, SEE_ALSO);
    }

    static boolean isAnnotationWithName(AnnotationMirror mirror, String name) {
        return mirror.getAnnotationType().asElement().getSimpleName().toString().equals(name);
    }
}
