package nl.loxia.builder.generator.ap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementKindVisitor9;
import javax.tools.Diagnostic.Kind;

import nl.loxia.builder.generator.annotations.Builder;

/**
 * Annotation Processor which handles the needed setup for the {@link BuilderGenerator} to do its work.
 *
 * @author Ben Zegveld
 */
@SupportedAnnotationTypes("nl.loxia.builder.generator.annotations.Builder")
@SupportedOptions({ "testCompiling", "nl.loxia.BuilderGenerator.copyOfMethodGeneration",
    "nl.loxia.BuilderGenerator.methodPrefix" })
public class BuilderProcessor extends AbstractProcessor {
    private FreeMarkerWriter freeMarkerWriter;
    private boolean testCompiling;
    private EnvironmentConfiguration environmentConfiguration;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        freeMarkerWriter = new FreeMarkerWriter();
        testCompiling = processingEnv.getOptions().containsKey("testCompiling");
        environmentConfiguration = new EnvironmentConfiguration(processingEnv);
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            // get and process any builders from this round
            Set<TypeElement> builders = getBuildersToGenerate(annotations, roundEnvironment);
            for (TypeElement typeElement : builders) {
                if (testCompiling && typeElement.getSimpleName().toString().startsWith("Erroneous")) {
                    continue;
                }
                new BuilderGenerator(environmentConfiguration, processingEnv.getTypeUtils(),
                    processingEnv.getMessager(), typeElement).generate(processingEnv.getFiler(), freeMarkerWriter);
            }
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Set<TypeElement> getBuildersToGenerate(final Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnvironment) {
        Set<TypeElement> builderTypes = new HashSet<>();

        for (TypeElement annotation : annotations) {
            // Indicates that the annotation's type isn't on the class path of the compiled
            // project. Let the compiler deal with that and print an appropriate error.
            if (annotation.getKind() != ElementKind.ANNOTATION_TYPE) {
                continue;
            }

            try {
                Set<? extends Element> annotatedClasses = roundEnvironment.getElementsAnnotatedWith(annotation);
                for (Element element : annotatedClasses) {
                    TypeElement typeElement = asTypeElement(element);

                    // on some JDKs, RoundEnvironment.getElementsAnnotatedWith( ... ) returns types with
                    // annotations unknown to the compiler, even though they are not declared Builders
                    if (typeElement != null && typeElement.getAnnotation(Builder.class) != null &&
                        typeElement.getEnclosingElement().getKind() == ElementKind.PACKAGE) {
                        builderTypes.add(typeElement);
                    }
                }
            }
            catch (Throwable t) { // whenever that may happen, but just to stay on the save side
                handleUncaughtError(annotation, t);
            }
        }
        return builderTypes;
    }

    private void handleUncaughtError(Element element, Throwable thrown) {
        StringWriter sw = new StringWriter();
        thrown.printStackTrace(new PrintWriter(sw));

        String reportableStacktrace = sw.toString().replace(System.lineSeparator(), "  ");

        processingEnv.getMessager().printMessage(
            Kind.ERROR, "Internal error in the builder processor: " + reportableStacktrace, element);
    }

    private TypeElement asTypeElement(Element element) {
        return element.accept(
            new ElementKindVisitor9<TypeElement, Void>() {
                @Override
                public TypeElement visitTypeAsInterface(TypeElement e, Void p) {
                    return e;
                }

                @Override
                public TypeElement visitTypeAsClass(TypeElement e, Void p) {
                    return e;
                }

            }, null);
    }

    @SuppressWarnings("serial")
    private class BuilderProcessorInitializationException extends RuntimeException {
        public BuilderProcessorInitializationException(Exception e) {
            super(e);
        }
    }

}
