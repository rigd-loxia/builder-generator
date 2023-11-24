package nl.loxia.builder.generator.ap;

import javax.lang.model.element.AnnotationMirror;

/**
 * Utility class for annotation mirror checks.
 */
class AnnotationUtils {

    private AnnotationUtils() {
        // This is a utility class.
    }

    private static final String SEE_ALSO = "SeeAlso";

    /**
     * @param mirror - the annotation mirror to be checked
     * @return true if the mirror supplied is named {@code SeeAlso}
     */
    static boolean isSeeAlsoAnnotation(AnnotationMirror mirror) {
        return isAnnotationWithName(mirror, SEE_ALSO);
    }

    /**
     * @param mirror - the annotation mirror to be checked
     * @param name - the name to check for
     * @return true if the mirror supplied is named as supplied
     */
    static boolean isAnnotationWithName(AnnotationMirror mirror, String name) {
        return mirror.getAnnotationType().asElement().getSimpleName().toString().equals(name);
    }
}
