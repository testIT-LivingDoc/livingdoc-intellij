package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import info.novatec.testit.livingdoc.document.Document;
import info.novatec.testit.livingdoc.repository.DocumentRepository;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintWriter;

/**
 * Command line initialization and environment configuration.
 *
 * @see JavaCommandLineState
 * @see RemoteRunConfiguration
 */
public class LivingDocRunProfileState extends JavaCommandLineState {

    private RemoteRunConfiguration runConfiguration;

    protected LivingDocRunProfileState(@NotNull ExecutionEnvironment env) {

        super(env);

        this.runConfiguration = (RemoteRunConfiguration) getEnvironment().getRunnerAndConfigurationSettings().getConfiguration();
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {

        final JavaParameters javaParameters = new JavaParameters();

        final int classPathType = JavaParameters.JDK_AND_CLASSES_AND_TESTS; // TODO review types and uses
        final String jreHome = runConfiguration.ALTERNATIVE_JRE_PATH_ENABLED ? runConfiguration.ALTERNATIVE_JRE_PATH : null;
        JavaParametersUtil.configureModule(runConfiguration.getConfigurationModule(), javaParameters, classPathType, jreHome);
        JavaParametersUtil.configureConfiguration(javaParameters, runConfiguration);

        //configureClassPath(javaParameters);

        javaParameters.setMainClass(runConfiguration.MAIN_CLASS_NAME);

        // To generate XML report
        javaParameters.getProgramParametersList().add("--xml");

        addLivingDocProgramParameterList(javaParameters);

        return javaParameters;
    }

    private void addLivingDocProgramParameterList(final JavaParameters javaParameters) throws ExecutionException {
        LivingDocFilesManager livingDocFileManager = new LivingDocFilesManager(
                runConfiguration.getRepositoryUID(),
                runConfiguration.getSpecificationName());
        try {
            File specificationFile = livingDocFileManager.createSpecificationFile();

            loadDocument(specificationFile);

            String specificationPath = specificationFile.getAbsolutePath();
            javaParameters.getProgramParametersList().add(specificationPath);

            File reportFile = livingDocFileManager.createReportFile();

            String reportPath = reportFile.getAbsolutePath();
            javaParameters.getProgramParametersList().add(reportPath);

            configureOSProcessHandler(javaParameters.createOSProcessHandler(), reportPath);

        } catch (Exception e) {
            // TODO Logger
            throw new ExecutionException(e);
        }
    }

    private void configureOSProcessHandler(@NotNull final OSProcessHandler osProcessHandler, final String reportPath) {

        osProcessHandler.startNotify();
        osProcessHandler.addProcessListener(new LivingDocProcessListener(runConfiguration, reportPath));
    }

    private void loadDocument(final File specificationFile) throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();

        DocumentRepository documentRepository = runConfiguration.getRepository().asDocumentRepository(
                classLoader, runConfiguration.getUser(), runConfiguration.getPass());

        String specificationParameter = runConfiguration.getSpecificationName();
        //TODO Add if it is using a current version: runConfiguration.getSpecificationName();  + ( usingCurrentVersion ? "?implemented=false" : "" );
        Document document = documentRepository.loadDocument(specificationParameter);

        PrintWriter pw = new PrintWriter(specificationFile);
        document.print(pw);
    }

}
