package com.synopsys.integration.bdio.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    public void testConstructingDependencyNode() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, "name", "version");
        final Dependency dependencyNode = new Dependency(externalId);
        assertEquals("npm", dependencyNode.externalId.forge.toString(), "npm");
        assertEquals("http:npm/name/version", dependencyNode.externalId.createBdioId());
        assertEquals("name@version", dependencyNode.externalId.createExternalId());
    }

    @Test
    public void testBoilerplateCode() {
        final Dependency nodeA = new Dependency((String) null, (String) null, (ExternalId) null);
        final Dependency nodeB = new Dependency((String) null, (String) null, (ExternalId) null);
        assertEquals(nodeA, nodeB);
        assertEquals(nodeA.hashCode(), nodeB.hashCode());
        assertEquals(nodeA.toString(), nodeB.toString());
    }

}
