package nl.loxia.builder.generator.ap;

import java.io.IOException;
import java.io.Writer;

import javax.tools.JavaFileObject;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * The writer class for writing java files.
 *
 * @author Ben Zegveld
 */
public class FreeMarkerWriter {

    private final Configuration cfg;

    /**
     * The constructor containing the default configuration.
     */
    public FreeMarkerWriter() {
        cfg = new Configuration(Configuration.VERSION_2_3_29);

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:
        cfg.setClassForTemplateLoading(FreeMarkerWriter.class, "/freemarker");

        // From here we will set the settings recommended for new projects. These
        // aren't the defaults for backward compatibilty.

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8");

        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.setLogTemplateExceptions(false);

        // Wrap unchecked exceptions thrown during template processing into TemplateException-s:
        cfg.setWrapUncheckedExceptions(true);

        // Do not fall back to higher scopes when reading a null loop variable:
        cfg.setFallbackOnNullLoopVariable(false);
    }

    /**
     * Writes the given source file with the supplied builder data using the defined structure in the ftl files.
     *
     * @param sourceFile - Java file to write the code to.
     * @param builderData - Java Bean containing the required information to generate the builder code.
     * @throws TemplateException - if an exception occurs during template processing. (see {@link Template#process(Object, Writer)})
     * @throws IOException - if an I/O exception occurs during writing to the writer. (see {@link Template#process(Object, Writer)})
     */
    public void write(JavaFileObject sourceFile, BuilderData builderData) throws IOException, TemplateException {
        Template template = cfg.getTemplate("Builder.ftl");
        try (Writer writer = sourceFile.openWriter()) {
            template.process(builderData, writer);
        }
    }
}
