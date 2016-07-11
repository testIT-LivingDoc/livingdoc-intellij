package info.novatec.testit.livingdoc.intellij.util;

import info.novatec.testit.livingdoc.intellij.model.LDProject;
import info.novatec.testit.livingdoc.server.domain.Repository;
import org.junit.Assert;
import org.junit.Test;

public class UIUtilsTest {

    @Test
    public void validateCredentials() {

        // TODO units tests
        LDProject ldProject = new LDProject(null);
        Repository repository = new Repository();

        boolean result = UIUtils.validateCredentials(ldProject, repository);
        Assert.assertFalse(result);
    }
}