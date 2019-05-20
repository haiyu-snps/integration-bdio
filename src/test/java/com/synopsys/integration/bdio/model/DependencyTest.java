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
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, "name", "version");
        Dependency dependencyNode = new Dependency(externalId);
        assertEquals("npm", dependencyNode.externalId.forge.toString(), "npm");
        assertEquals(new BdioId("http:npm/name/version"), dependencyNode.externalId.createBdioId());
        assertEquals("name/version", dependencyNode.externalId.getExternalId());
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
