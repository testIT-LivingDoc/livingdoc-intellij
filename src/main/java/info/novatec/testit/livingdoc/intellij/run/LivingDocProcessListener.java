package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import info.novatec.testit.livingdoc.report.XmlReport;
import info.novatec.testit.livingdoc.server.domain.Execution;
import info.novatec.testit.livingdoc.server.domain.Specification;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;


/**
 * TODO javadoc
 *
 * @see ProcessAdapter
 */
public class LivingDocProcessListener extends ProcessAdapter {

    private RemoteRunConfiguration runConfiguration;
    private String reportPath;

    public LivingDocProcessListener(RemoteRunConfiguration runConfiguration, final String reportPath) {
        this.reportPath = reportPath;
        this.runConfiguration = runConfiguration;
    }

    @Override
    public void processTerminated(ProcessEvent processEvent) {

        if (processEvent.getExitCode() == 0) {
            try {
                File reportFile = new File(reportPath);

                // Caused by: java.lang.ClassCastException: org.apache.xerces.jaxp.DocumentBuilderFactoryImpl cannot be cast to javax.xml.parsers.DocumentBuilderFactory
                //at javax.xml.parsers.DocumentBuilderFactory.newInstance(Unknown Source)
                //at info.novatec.testit.livingdoc.report.XmlReport.<clinit>(XmlReport.java:83)

                XmlReport xmlReport = XmlReport.parse(reportFile);

                Specification specification = Specification.newInstance(runConfiguration.getSpecificationName());
                specification.setRepository(runConfiguration.getRepository());

                Execution execution = Execution.newInstance(specification, null, xmlReport);
                execution.setResults(xmlReport.getResults(0));

                specification.addExecution(execution);


                //openResult(buildEditorName(), );

            } catch (SAXException|IOException exception) {
                // TODO logger
            }
        }
    }

    private String buildEditorName() {
        return String.format("%s/%s", runConfiguration.getRepositoryUID(), runConfiguration.getSpecificationName());
    }
}
