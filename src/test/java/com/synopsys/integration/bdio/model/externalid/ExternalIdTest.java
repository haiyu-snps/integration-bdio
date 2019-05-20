package com.synopsys.integration.bdio.model.externalid;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.Forge;

public class ExternalIdTest {
    private final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

    @Test
    public void testForgeEquality() {
        assertEquals(Forge.ANACONDA, new Forge("/", "anaconda"));
    }

    @Test
    public void testCreatingExternalIds() {
        ExternalId architectureExternalId = simpleBdioFactory.createArchitectureExternalId(Forge.CENTOS, "name", "version", "architecture");
        assertEquals(new BdioId("http:centos/name/version/architecture"), architectureExternalId.createBdioId());
        assertEquals("name/version/architecture", architectureExternalId.getExternalId());

        ExternalId mavenExternalId = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        assertEquals(new BdioId("http:maven/group/artifact/version"), mavenExternalId.createBdioId());
        assertEquals("group:artifact:version", mavenExternalId.getExternalId());

        ExternalId moduleNamesExternalId = simpleBdioFactory.createModuleNamesExternalId(Forge.CPAN, "name", "version", "something", "else");
        assertEquals(new BdioId("http:cpan/name/version/something/else"), moduleNamesExternalId.createBdioId());
        assertEquals("name/version/something/else", moduleNamesExternalId.getExternalId());

        ExternalId nameVersionExternalId = simpleBdioFactory.createNameVersionExternalId(Forge.PYPI, "name", "version");
        assertEquals(new BdioId("http:pypi/name/version"), nameVersionExternalId.createBdioId());
        assertEquals("name/version", nameVersionExternalId.getExternalId());

        ExternalId pathExternalId = simpleBdioFactory.createPathExternalId(Forge.GOGET, "name");
        assertEquals(new BdioId("http:goget/name"), pathExternalId.createBdioId());
        assertEquals("name", pathExternalId.getExternalId());
    }

    @Test
    public void testEscapingBadUriCharacters() {
        ExternalId nameVersionExternalId = simpleBdioFactory.createNameVersionExternalId(Forge.NPMJS, "name with spaces", "version with a - and a # and spaces");
        assertEquals(new BdioId("http:npmjs/name_with_spaces/version_with_a___and_a___and_spaces"), nameVersionExternalId.createBdioId());
        assertEquals("name with spaces/version with a - and a # and spaces", nameVersionExternalId.getExternalId());
    }

    @Test
    public void testWithoutEnoughState() {
        ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.name = "bundler";
        String[] pieces = externalId.getExternalIdPieces();
        assertEquals(1, pieces.length);
        assertEquals("bundler", pieces[0]);
    }

    @Test
    @Deprecated
    public void testCreateDataId() {
        // this test should be removed once createDataId() is removed (obviously)
        ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.name = "testName";
        externalId.version = "testVersion";
        assertEquals(new BdioId("http:maven/testName/testVersion"), externalId.createBdioId());
    }

    @Test
    public void testBoilerplateCode() {
        ExternalId externalIdA = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        ExternalId externalIdB = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        assertEquals(externalIdA, externalIdB);
        assertEquals(externalIdA.hashCode(), externalIdB.hashCode());
        assertEquals(externalIdA.toString(), externalIdB.toString());
    }

}
