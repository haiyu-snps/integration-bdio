package com.synopsys.integration.bdio.model.externalid;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.Forge;

public class ExternalIdTest {
    private final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

    @Test
    public void testForgeEquality() {
        assertEquals(Forge.ANACONDA, new Forge("=", "/", "anaconda"));
    }

    @Test
    public void testCreatingExternalIds() {
        final ExternalId architectureExternalId = simpleBdioFactory.createArchitectureExternalId(Forge.CENTOS, "name", "version", "architecture");
        assertEquals("http:centos/name/version/architecture", architectureExternalId.createBdioId());
        assertEquals("name/version/architecture", architectureExternalId.createExternalId());

        final ExternalId mavenExternalId = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        assertEquals("http:maven/group/artifact/version", mavenExternalId.createBdioId());
        assertEquals("group:artifact:version", mavenExternalId.createExternalId());

        final ExternalId moduleNamesExternalId = simpleBdioFactory.createModuleNamesExternalId(Forge.CPAN, "name", "version", "something", "else");
        assertEquals("http:cpan/name/version/something/else", moduleNamesExternalId.createBdioId());
        assertEquals("name-version-something-else", moduleNamesExternalId.createExternalId());

        final ExternalId nameVersionExternalId = simpleBdioFactory.createNameVersionExternalId(Forge.PYPI, "name", "version");
        assertEquals("http:pypi/name/version", nameVersionExternalId.createBdioId());
        assertEquals("name/version", nameVersionExternalId.createExternalId());

        final ExternalId pathExternalId = simpleBdioFactory.createPathExternalId(Forge.GOGET, "name");
        assertEquals("http:goget/name", pathExternalId.createBdioId());
        assertEquals("name", pathExternalId.createExternalId());
    }

    @Test
    public void testEscapingBadUriCharacters() {
        final ExternalId nameVersionExternalId = simpleBdioFactory.createNameVersionExternalId(Forge.NPM, "name with spaces", "version with a - and a # and spaces");
        assertEquals("http:npm/name_with_spaces/version_with_a___and_a___and_spaces", nameVersionExternalId.createBdioId());
        assertEquals("name with spaces@version with a - and a # and spaces", nameVersionExternalId.createExternalId());
        assertEquals("name with spaces/version with a - and a # and spaces", nameVersionExternalId.createBlackDuckOriginId());
    }

    @Test
    public void testWithoutEnoughState() {
        final ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.name = "bundler";
        final String[] pieces = externalId.getExternalIdPieces();
        assertEquals(1, pieces.length);
        assertEquals("bundler", pieces[0]);
    }

    @Test
    @Deprecated
    public void testCreateDataId() {
        // this test should be removed once createDataId() is removed (obviously)
        final ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.name = "testName";
        externalId.version = "testVersion";
        assertEquals("http:maven/testName/testVersion", externalId.createBdioId());
    }

    @Test
    public void testBoilerplateCode() {
        final ExternalId externalIdA = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        final ExternalId externalIdB = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        assertEquals(externalIdA, externalIdB);
        assertEquals(externalIdA.hashCode(), externalIdB.hashCode());
        assertEquals(externalIdA.toString(), externalIdB.toString());
    }

}
