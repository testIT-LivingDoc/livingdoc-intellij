package org.livingdoc.intellij.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.util.ColorProgressBar;
import info.novatec.testit.livingdoc.document.Document;
import info.novatec.testit.livingdoc.repository.DocumentRepository;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.domain.ProjectSettings;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Command line initialization and environment configuration:<br>
 * <code>livingdoc [options] input ouput</code>
 *
 * @see JavaCommandLineState
 * @see RemoteRunConfiguration
 */
class RunProfileStateLivingDoc extends JavaCommandLineState {

    private static final Logger LOG = Logger.getInstance(RunProfileStateLivingDoc.class);

    private final RemoteRunConfiguration runConfiguration;
    private final FilesManager livingDocFileManager;

    RunProfileStateLivingDoc(@NotNull ExecutionEnvironment executionEnvironment) {

        super(executionEnvironment);

        this.runConfiguration = (RemoteRunConfiguration) executionEnvironment.getRunnerAndConfigurationSettings().getConfiguration();
        this.livingDocFileManager = new FilesManager(runConfiguration);
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {

        final JavaParameters javaParameters = addLivingDocProgramParameterList();

        final int classPathType = JavaParameters.JDK_AND_CLASSES_AND_TESTS;
        final String jreHome = runConfiguration.ALTERNATIVE_JRE_PATH_ENABLED ? runConfiguration.ALTERNATIVE_JRE_PATH : null;
        JavaParametersUtil.configureModule(runConfiguration.getConfigurationModule(), javaParameters, classPathType, jreHome);
        JavaParametersUtil.configureConfiguration(javaParameters, runConfiguration);

        javaParameters.setMainClass(runConfiguration.MAIN_CLASS_NAME);

        return javaParameters;
    }

    /**
     * {@link ProcessListenerLivingDoc#startNotified(ProcessEvent)} is the listener method for <code>osProcessHandler.startNotify()</code>
     */
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {

        OSProcessHandler osProcessHandler = super.startProcess();
        osProcessHandler.addProcessListener(new ProcessListenerLivingDoc(runConfiguration));
        osProcessHandler.startNotify(); //  start capturing the process output
        return osProcessHandler;
    }

    /**
     * <p>To override the default System Under Development class (used for fixture classes instantiation).<br>
     * The library with the specified class should be in the same directory as the runner. </p>
     * <br>
     * <code>-f CLASS;ARGS Use CLASS as the system under development and instantiate it with ARGS</code>
     */
    private JavaParameters addLivingDocProgramParameterList() throws ExecutionException {

        JavaParameters javaParameters = new JavaParameters();

        // Generate XML report (defaults to plain)
        javaParameters.getProgramParametersList().add("--xml");

        try {
            javaParameters.getProgramParametersList().add(getSpecificationInputPath());
            javaParameters.getProgramParametersList().add(getReportOutputPath());

        } catch (Exception e) {
            LOG.error(e);
            throw new ExecutionException(e);
        }
        return javaParameters;
    }

    @NotNull
    private String getReportOutputPath() throws IOException {
        File reportFile = livingDocFileManager.createReportFile();
        return reportFile.getAbsolutePath();
    }

    @NotNull
    private String getSpecificationInputPath() throws IOException, ExecutionException {

        File specificationFile = livingDocFileManager.createSpecificationFile();

        buildSpecificationFile(specificationFile);

        return specificationFile.getAbsolutePath();
    }

    private void buildSpecificationFile(@NotNull final File specificationFile) throws ExecutionException {

        ClassLoader classLoader = getClass().getClassLoader();

        ProjectSettings projectSettings = ProjectSettings.getInstance(runConfiguration.getProject());

        DocumentRepository documentRepository = runConfiguration.getRepository().asDocumentRepository(
                classLoader, projectSettings.getUser(), projectSettings.getPassword());

        String location = runConfiguration.getSpecificationName() + (runConfiguration.isCurrentVersion() ? "?implemented=false" : "");

        try (PrintWriter printWriter = new PrintWriter(specificationFile, String.valueOf(StandardCharsets.UTF_8))) {

            Document document = documentRepository.loadDocument(location);
            if (document != null) {
                document.print(printWriter);
            } else {
                LOG.error(I18nSupport.getValue("run.execution.error.document.null"));
            }
        } catch (Exception e) {
            runConfiguration.getStatusLine().setText(e.getMessage());
            runConfiguration.getStatusLine().setStatusColor(ColorProgressBar.RED);
            runConfiguration.getStatusLine().setFraction(100d);

            LOG.error(e);
            throw new ExecutionException(e);
        }
    }
}
