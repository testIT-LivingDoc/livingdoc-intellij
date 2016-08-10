package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.openapi.diagnostic.Logger;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * To create the files used in LivingDoc execution.<br>
 * The temporal files are created in the path indicated for the system property: <code>java.io.tmpdir</code><br>
 * NOTE: File names are configured in <b>config.properties</b>
 */
public class LivingDocFilesManager {

    private static final Logger LOG = Logger.getInstance("#info.novatec.testit.livingdoc.intellij.run.LivingDocFilesManager");

    private static final String HTML = ".html";
    private static final String XML = ".xml";
    private static final String SEPARATOR = "_";
    private final String systemTmpDir;

    private final String repositoryUID;
    private final String specificationName;

    /**
     * The parameters are used to build the file names.
     *
     * @param repositoryUID     Unique identifier for LivingDoc repository
     * @param specificationName LivingDoc specification name
     */
    public LivingDocFilesManager(@NotNull final String repositoryUID, @NotNull final String specificationName) {

        this.repositoryUID = repositoryUID;
        this.specificationName = specificationName;

        this.systemTmpDir = System.getProperty("java.io.tmpdir");
    }

    /**
     * Return the temporal <b>specification</b> file.<br>
     * The file is created whether it doesn't exist.
     *
     * @return {@link File}
     * @throws IOException If an I/O error occurred
     */
    public File createSpecificationFile() throws IOException {

        return createFile(PluginProperties.getValue("livingdoc.file.specification"), HTML);
    }

    /**
     * Return the temporal <b>report</b> file.<br>
     * The file is created whether it doesn't exist.
     *
     * @return {@link File}
     * @throws IOException If an I/O error occurred
     */
    public File createReportFile() throws IOException {

        return createFile(PluginProperties.getValue("livingdoc.file.report"), XML);
    }

    /**
     * Return the temporal <b>result</b> file.<br>
     * The file is created whether it doesn't exist.
     *
     * @return {@link File}
     * @throws IOException If an I/O error occurred
     */
    public File createResultFile() throws IOException {

        return createFile(PluginProperties.getValue("livingdoc.file.results"), HTML);
    }

    private File createFile(final String fileType, final String extension) throws IOException {

        File file = new File(systemTmpDir, buildFileName(fileType, extension));

        if (!file.exists() && !file.createNewFile()) {
            LOG.debug("The file " + fileType + " has not been created");
        }

        return file;
    }

    private String buildFileName(final String fileType, final String extension) {
        String prefix = repositoryUID.replaceAll("\\\\", SEPARATOR).replaceAll("/", SEPARATOR).replaceAll("-", SEPARATOR);
        String altName = specificationName.replaceAll("\\\\", SEPARATOR).replaceAll("/", SEPARATOR).replaceAll("\"", "''");
        return String.format("%s_%s_%s%s", prefix, altName, fileType, extension);
    }
}
