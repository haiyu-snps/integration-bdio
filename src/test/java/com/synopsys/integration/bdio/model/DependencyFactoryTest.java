package com.synopsys.integration.bdio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyFactoryTest {
    private ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);

    @Test
    public void testDependencyFactory() {
        ExternalId externalId = externalIdFactory.createArchitectureExternalId(Forge.ALPINE, "a_name", "a_version", "architecture");
        Dependency dependency = dependencyFactory.createArchitectureDependency(Forge.ALPINE, "a_name", "a_version", "architecture");
        assertDependency("a_name", "a_version", externalId, dependency);

        externalId = externalIdFactory.createMavenExternalId("group", "b_name", "b_version");
        dependency = dependencyFactory.createMavenDependency("group", "b_name", "b_version");
        assertDependency("b_name", "b_version", externalId, dependency);

        externalId = externalIdFactory.createPathExternalId(Forge.CPAN, "one/two/three/four/five");
        dependency = dependencyFactory.createPathDependency(Forge.CPAN, "one/two/three/four/five");
        assertDependency("one/two/three/four/five", null, externalId, dependency);

        externalId = externalIdFactory.createYoctoExternalId("layer", "c_name", "c_version");
        dependency = dependencyFactory.createYoctoDependency("layer", "c_name", "c_version");
        assertDependency("c_name", "c_version", externalId, dependency);

        externalId = externalIdFactory.createModuleNamesExternalId(Forge.GOLANG, "hewey", "dewey", "louie");
        dependency = dependencyFactory.createModuleNamesDependency(Forge.GOLANG, "hewey", "dewey", "louie");
        assertDependency(null, null, externalId, dependency);

        externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "d_name", "d_version");
        dependency = dependencyFactory.createNameVersionDependency(Forge.RUBYGEMS, "d_name", "d_version");
        assertDependency("d_name", "d_version", externalId, dependency);
    }

    private void assertDependency(String name, String version, ExternalId externalId, Dependency dependency) {
        assertEquals(name, dependency.getName());
        assertEquals(version, dependency.getVersion());
        assertEquals(externalId, dependency.getExternalId());
    }

}
