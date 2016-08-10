package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.BrowserLauncherImpl;
import com.intellij.ide.browsers.WebBrowserManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Thread to create a resource in the IntelliJ project from the result file and open it in a web browser.
 *
 * @see Runnable
 */
public class OpenResourceRunnable implements Runnable {

    private static final Logger LOG = Logger.getInstance("#info.novatec.testit.livingdoc.intellij.run.OpenResourceRunnable");
    private final Project project;
    private final File resultFile;

    public OpenResourceRunnable(final Project project, final File resultFile) {
        this.project = project;
        this.resultFile = resultFile;
    }

    @Override
    public void run() {
        VirtualFile projectDir = project.getBaseDir();
        String folderName = PluginProperties.getValue("livingdoc.dir.project");
        try {
            VirtualFile ldDir = projectDir.findChild(folderName);
            if (ldDir == null) {
                ldDir = projectDir.createChildDirectory(this, folderName);
            }

            VirtualFile ldFile = ldDir.findChild(resultFile.getName());
            if (ldFile == null) {
                ldFile = ldDir.createChildData(this, resultFile.getName());
            }

            InputStream inputStream = new FileInputStream(resultFile);
            ldFile.setBinaryContent(IOUtils.toByteArray(inputStream));
            inputStream.close();

            BrowserLauncher browser = new BrowserLauncherImpl();
            browser.browse(ldFile.getUrl(), WebBrowserManager.getInstance().getFirstActiveBrowser(),
                    project);

        } catch (IOException ioe) {
            LOG.error(ioe.getMessage());
        }
    }
}
