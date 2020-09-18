package com.synopsys.integration.bdio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    public void testConstructingDependencyNode() {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "name", "version");
        Dependency dependencyNode = new Dependency(externalId);
        assertEquals("npmjs", dependencyNode.getExternalId().getForge().toString(), "npmjs");
        assertEquals(new BdioId("http:npmjs/name/version"), dependencyNode.getExternalId().createBdioId());
        assertEquals("name/version", dependencyNode.getExternalId().createExternalId());
        assertEquals("name", dependencyNode.getName());
        assertEquals("version", dependencyNode.getVersion());
    }

    @Test
    public void testPathDependency() {
        String path = "some-path-that-might-be-long";
        ExternalId pathExternalId = externalIdFactory.createPathExternalId(Forge.CPAN, path);
        Dependency pathDependency = new Dependency(pathExternalId);
        assertEquals(path, pathDependency.getExternalId().createExternalId());
        assertEquals(path, pathDependency.getName());
        assertEquals(null, pathDependency.getVersion());
    }

    @Test
    public void testBoilerplateCode() {
        Dependency nodeA = new Dependency((String) null, (String) null, (ExternalId) null);
        Dependency nodeB = new Dependency((String) null, (String) null, (ExternalId) null);
        assertEquals(nodeA, nodeB);
        assertEquals(nodeA.hashCode(), nodeB.hashCode());
        assertEquals(nodeA.toString(), nodeB.toString());
    }

}
