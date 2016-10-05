package info.novatec.testit.livingdoc.intellij.gui.toolwindows;


import com.intellij.icons.AllIcons;
import info.novatec.testit.livingdoc.intellij.common.Icons;
import info.novatec.testit.livingdoc.intellij.domain.*;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RepositoryViewUtilsTest {

    private boolean hasError = false;
    private DocumentNode documentNode;
    private Node node;
    private SpecificationNode specificationNode;

    @Before
    public void setUp() {
        documentNode = new DocumentNode("tittle");
        specificationNode = new SpecificationNode(documentNode, node);

    }

    @Test
    public void getModuleNode(){

        Node childNode;
        ModuleNode parentNode;

        parentNode = new ModuleNode("grandpa","module");
        childNode = new Node("child",Icons.ERROR, NodeType.ERROR, parentNode);
        node = new Node("grandchild",Icons.ERROR, NodeType.ERROR, childNode);

        Assert.assertNotNull(RepositoryViewUtils.getModuleNode(node));
    }


    @Test
    public void getRepositoryNode(){
        RepositoryNode rn1;

        String repoNodeName = "repoNode";
        rn1 = new RepositoryNode(repoNodeName, null);
        SpecificationNode parentSN = new SpecificationNode(documentNode, rn1);
        SpecificationNode childSN = new SpecificationNode(documentNode, parentSN);

        RepositoryNode result = RepositoryViewUtils.getRepositoryNode(childSN);
        Assert.assertNotNull(result);
        Assert.assertEquals(repoNodeName, result.getName());
    }

    @Test
    public void getResultIconWorkingError(){

        hasError = true;
        specificationNode.setUsingCurrentVersion(true);

        Assert.assertEquals(Icons.ERROR_WORKING, RepositoryViewUtils.getResultIcon(hasError, specificationNode));

    }

    @Test
    public void getResultIconWorkingSuccess(){

        hasError = false;
        specificationNode.setUsingCurrentVersion(true);

        Assert.assertEquals(Icons.SUCCESS_WORKING, RepositoryViewUtils.getResultIcon(hasError, specificationNode));

    }

    @Test
    public void getResultIconDiffError(){

        hasError = true;
        specificationNode.setCanBeImplemented(true);

        Assert.assertEquals(Icons.ERROR_DIFF, RepositoryViewUtils.getResultIcon(hasError, specificationNode));

    }

    @Test
    public void getResultIconDiffSuccess(){

        hasError = false;
        specificationNode.setCanBeImplemented(true);

        Assert.assertEquals(Icons.SUCCESS_DIFF, RepositoryViewUtils.getResultIcon(hasError, specificationNode));
    }


    @Test
    public void getNodeIconExeWork(){

        specificationNode.setExecutable(true);
        specificationNode.setUsingCurrentVersion(true);

        Assert.assertEquals(Icons.EXE_WORKING, RepositoryViewUtils.getNodeIcon(specificationNode));
    }

    @Test
    public void getNodeIconExeDiff(){

        specificationNode.setExecutable(true);
        specificationNode.setCanBeImplemented(true);

        Assert.assertEquals(Icons.EXE_DIFF, RepositoryViewUtils.getNodeIcon(specificationNode));
    }

    @Test
    public void getNodeIconFolder(){

        specificationNode.setExecutable(false);

        Assert.assertEquals(AllIcons.Nodes.Folder, RepositoryViewUtils.getNodeIcon(specificationNode));
    }

}
