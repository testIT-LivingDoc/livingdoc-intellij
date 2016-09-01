package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.openapi.diagnostic.Logger;
import info.novatec.testit.livingdoc.document.Document;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.repository.DocumentRepository;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Command line initialization and environment configuration:<br>
 * <code>livingdoc [options] input ouput</code>
 *
 * @see JavaCommandLineState
 * @see RemoteRunConfiguration
 */
public class LivingDocRunProfileState extends JavaCommandLineState {

    private static final Logger LOG = Logger.getInstance(LivingDocRunProfileState.class);

    private final RemoteRunConfiguration runConfiguration;
    private final LivingDocFilesManager livingDocFileManager;

    LivingDocRunProfileState(@NotNull ExecutionEnvironment executionEnvironment) {

        super(executionEnvironment);

        this.runConfiguration = (RemoteRunConfiguration) executionEnvironment.getRunnerAndConfigurationSettings().getConfiguration();
        this.livingDocFileManager = new LivingDocFilesManager(runConfiguration);
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {

        final JavaParameters javaParameters = new JavaParameters();

        final int classPathType = JavaParameters.JDK_AND_CLASSES_AND_TESTS;
        final String jreHome = runConfiguration.ALTERNATIVE_JRE_PATH_ENABLED ? runConfiguration.ALTERNATIVE_JRE_PATH : null;
        JavaParametersUtil.configureModule(runConfiguration.getConfigurationModule(), javaParameters, classPathType, jreHome);
        JavaParametersUtil.configureConfiguration(javaParameters, runConfiguration);

        javaParameters.setMainClass(runConfiguration.MAIN_CLASS_NAME);

        // Generate XML report (defaults to plain)
        javaParameters.getProgramParametersList().add("--xml");

        addLivingDocProgramParameterList(javaParameters);

        return javaParameters;
    }

    /**
     * {@link LivingDocProcessListener#startNotified(ProcessEvent)} is the listener method for <code>osProcessHandler.startNotify()</code>
     */
    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {

        OSProcessHandler osProcessHandler =  super.startProcess();
        osProcessHandler.addProcessListener(new LivingDocProcessListener(runConfiguration));
        osProcessHandler.startNotify(); //  start capturing the process output
        return osProcessHandler;
    }

    private void addLivingDocProgramParameterList(final JavaParameters javaParameters) throws ExecutionException {

        try {
            javaParameters.getProgramParametersList().add(getSpecificationInputPath());
            javaParameters.getProgramParametersList().add(getReportOutputPath());

        } catch (Exception e) {
            LOG.error(e);
            throw new ExecutionException(e);
        }
    }

    private String getReportOutputPath() throws IOException {
        File reportFile = livingDocFileManager.createReportFile();
        return reportFile.getAbsolutePath();
    }

    private String getSpecificationInputPath() throws IOException, ExecutionException {

        File specificationFile = livingDocFileManager.createSpecificationFile();

        buildSpecificationFile(specificationFile);

        return specificationFile.getAbsolutePath();
    }

    private void buildSpecificationFile(@NotNull final File specificationFile) throws ExecutionException {

        ClassLoader classLoader = getClass().getClassLoader();

        DocumentRepository documentRepository = runConfiguration.getRepository().asDocumentRepository(
                classLoader, runConfiguration.getUser(), runConfiguration.getPass());

        String location = runConfiguration.getSpecificationName() + (runConfiguration.isCurrentVersion() ? "?implemented=false" : "");

        try (PrintWriter printWriter = new PrintWriter(specificationFile)) {

            Document document = documentRepository.loadDocument(location);
            if (document != null) {
                document.print(printWriter);
            } else {
                LOG.error(I18nSupport.getValue("run.execution.error.documentnull"));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ExecutionException(e);
        }
    }

}
