package nl.loxia.builder.generator.ap;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    "nl.loxia.BuilderGenerator.methodPrefix", "nl.loxia.BuilderGenerator.verbose" })
public class BuilderProcessor extends AbstractProcessor {
    private class StackTraceElementCollector
            implements Collector<StackTraceElement[], List<StackTraceElement>, StackTraceElement[]> {

        private static final String STACK_TRACE_SEPARATOR = "stackTraceSeparator";

        @Override
        public Supplier<List<StackTraceElement>> supplier() {
            return () -> new ArrayList<>();
        }

        @Override
        public BiConsumer<List<StackTraceElement>, StackTraceElement[]> accumulator() {
            return (list, elements) -> {
                if (!list.isEmpty()) {
                    list.add(seperator());
                }
                list.addAll(List.of(elements));
            };
        }

        private StackTraceElement seperator() {
            return new StackTraceElement(STACK_TRACE_SEPARATOR, STACK_TRACE_SEPARATOR, STACK_TRACE_SEPARATOR, 0);
        }

        @Override
        public BinaryOperator<List<StackTraceElement>> combiner() {
            return (o1, o2) -> {
                List<StackTraceElement> result = new ArrayList<>();
                result.addAll(o1);
                result.add(seperator());
                result.addAll(o2);
                return result;
            };
        }

        @Override
        public Function<List<StackTraceElement>, StackTraceElement[]> finisher() {
            return list -> list.toArray(StackTraceElement[]::new);
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of();
        }

    }

    @SuppressWarnings("serial")
    private class ExceptionCollection extends RuntimeException {

        private final List<? extends Throwable> exceptions;

        public ExceptionCollection(List<? extends Throwable> exceptions) {
            this.exceptions = exceptions;
        }

        @Override
        public synchronized Throwable getCause() {
            return null; // this container has no causes, the contained exceptions do.
        }

        @Override
        public String getLocalizedMessage() {
            return exceptions.stream().map(Throwable::getLocalizedMessage).collect(Collectors.joining(System.lineSeparator()));
        }

        @Override
        public String getMessage() {
            return exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(System.lineSeparator()));
        }

        @Override
        public StackTraceElement[] getStackTrace() {
            return exceptions.stream().map(Throwable::getStackTrace).collect(new StackTraceElementCollector());
        }

        @Override
        public synchronized Throwable initCause(Throwable cause) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void printStackTrace() {
            exceptions.stream().forEach(Throwable::printStackTrace);
        }

        @Override
        public void printStackTrace(PrintStream s) {
            exceptions.stream().forEach(t -> t.printStackTrace(s));
        }

        @Override
        public void printStackTrace(PrintWriter s) {
            exceptions.stream().forEach(t -> t.printStackTrace(s));
        }

        @Override
        public void setStackTrace(StackTraceElement[] stackTrace) {
            throw new UnsupportedOperationException();
        }
    }

    private FreeMarkerWriter freeMarkerWriter;
    private boolean testCompiling;
    private EnvironmentConfiguration environmentConfiguration;
    private boolean verbose;

    /**
     * Annotation Processor which handles the needed setup for the {@link BuilderGenerator} to do its work.
     */
    public BuilderProcessor() {
        // needed for javadoc
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        freeMarkerWriter = new FreeMarkerWriter();
        testCompiling = processingEnv.getOptions().containsKey("testCompiling");
        verbose = processingEnv.getOptions().containsKey("nl.loxia.BuilderGenerator.verbose");
        environmentConfiguration = new EnvironmentConfiguration(processingEnv);
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            List<RuntimeException> exceptions = new ArrayList<>();
            // get and process any builders from this round
            Set<TypeElement> builders = getBuildersToGenerate(annotations, roundEnvironment);
            for (TypeElement typeElement : builders) {
                try {
                    processBuilder(typeElement);
                }
                catch (RuntimeException e) {
                    exceptions.add(e);
                }
            }
            if (!exceptions.isEmpty()) {
                if (exceptions.size() == 1) {
                    throw exceptions.get(0);
                }
                throw new ExceptionCollection(exceptions);
            }
        }
        return false;
    }

    private void processBuilder(TypeElement typeElement) {
        if (testCompiling && typeElement.getSimpleName().toString().startsWith("Erroneous")) {
            return;
        }
        if (verbose) {
            processingEnv.getMessager().printMessage(Kind.NOTE,
                "Generating builder for " + typeElement.getQualifiedName(), typeElement);
        }
        BuilderGenerator builderGenerator = new BuilderGenerator(environmentConfiguration, processingEnv.getTypeUtils(),
            processingEnv.getElementUtils(),
            processingEnv.getMessager(), typeElement);
        try {
            builderGenerator.generate(processingEnv.getFiler(), freeMarkerWriter);
        }
        catch (Exception e) {
            throw new BuilderProcessorException(e, builderGenerator.getActions());
        }
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
    private class BuilderProcessorException extends RuntimeException {
        public BuilderProcessorException(Exception e, String history) {
            super("Builder generator threw an exception with the following handling history: " + history, e);
        }
    }

}
