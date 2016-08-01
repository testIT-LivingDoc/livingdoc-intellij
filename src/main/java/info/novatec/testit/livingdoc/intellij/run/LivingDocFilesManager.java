package info.novatec.testit.livingdoc.intellij.run;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * TODO Javadoc
 */
public class LivingDocFilesManager {

    private String repositoryUID;
    private String specificationName;

    public LivingDocFilesManager(@NotNull final String repositoryUID, @NotNull final String specificationName) {
        this.repositoryUID = repositoryUID;
        this.specificationName = specificationName;
    }

    public File createSpecificationFile() throws IOException {

        String systemTmpDir = System.getProperty("java.io.tmpdir");
        String specFileName = getFileName("specification", ".html");

        File file = new File(systemTmpDir, specFileName);
        file.createNewFile();  // TODO review necessity

        return file;
    }

    public File createReportFile() throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir"), getFileName("report", ".xml"));
        file.createNewFile(); // TODO review necessity

        return file;
    }

    private String getFileName(final String type, final String extension) {
        String prefix = repositoryUID.replaceAll("\\\\", "_").replaceAll("/", "_").replaceAll("-", "_");
        String altName = specificationName.replaceAll("\\\\", "_").replaceAll("/", "_").replaceAll("\"", "''");
        return String.format("%s_%s_%s%s", prefix, altName, type, extension);
    }
}
