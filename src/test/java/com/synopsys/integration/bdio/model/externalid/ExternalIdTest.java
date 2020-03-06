package com.synopsys.integration.bdio.model.externalid;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.Forge;

import java.util.function.Function;

public class ExternalIdTest {
    private final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

    @Test
    public void testForgeEquality() {
        assertEquals(Forge.ANACONDA, new Forge("/", "anaconda"));
    }

    @Test
    public void testCreatingExternalIds() {
        assertExternalIdOkay(Forge.GOGET, (forge -> simpleBdioFactory.createPathExternalId(forge, "name")), "name", null, "http:goget/name", "name");

        assertExternalIdOkay(Forge.CPAN, (forge -> simpleBdioFactory.createModuleNamesExternalId(forge, "name", "version", "something", "else")), "name", "version", "http:cpan/name/version/something/else", "name/version/something/else");

        assertExternalIdOkay(Forge.CENTOS, (forge -> simpleBdioFactory.createArchitectureExternalId(forge, "name", "version", "architecture")), "name", "version", "http:centos/name/version/architecture", "name/version/architecture");

        assertExternalIdOkay(Forge.PYPI, (forge -> simpleBdioFactory.createNameVersionExternalId(forge, "name", "version")), "name", "version", "http:pypi/name/version", "name/version");
        assertExternalIdOkay(Forge.RUBYGEMS, (forge -> simpleBdioFactory.createNameVersionExternalId(forge, "name")), "name", null, "http:rubygems/name", "name");

        assertExternalIdOkay(Forge.MAVEN, (forge -> simpleBdioFactory.createMavenExternalId("group", "artifact", "version")), "artifact", "version", "http:maven/group/artifact/version", "group:artifact:version");
        assertExternalIdOkay(Forge.MAVEN, (forge -> simpleBdioFactory.createMavenExternalId("group", "artifact")), "artifact", null, "http:maven/group/artifact", "group:artifact");

        assertExternalIdOkay(Forge.YOCTO, (forge -> simpleBdioFactory.createYoctoExternalId("layer", "name", "version")), "name", "version", "http:yocto/layer/name/version", "layer/name/version");
        assertExternalIdOkay(Forge.YOCTO, (forge -> simpleBdioFactory.createYoctoExternalId("layer", "name")), "name", null, "http:yocto/layer/name", "layer/name");
    }

    private void assertExternalIdOkay(Forge forge, Function<Forge, ExternalId> externalIdCreator, String name, String version, String expectedBdioId, String expectedExternalId) {
        ExternalId externalId = externalIdCreator.apply(forge);
        assertEquals(new BdioId(expectedBdioId), externalId.createBdioId());
        assertEquals(expectedExternalId, externalId.createExternalId());
        assertEquals(externalId, ExternalId.createFromExternalId(forge, externalId.createExternalId(), name, version));
    }

    @Test
    public void testEscapingBadUriCharacters() {
        ExternalId nameVersionExternalId = simpleBdioFactory.createNameVersionExternalId(Forge.NPMJS, "name with spaces", "version with a - and a # and spaces");
        assertEquals(new BdioId("http:npmjs/name+with+spaces/version+with+a+-+and+a+%23+and+spaces"), nameVersionExternalId.createBdioId());
        assertEquals("name with spaces/version with a - and a # and spaces", nameVersionExternalId.createExternalId());
    }

    @Test
    public void testWithoutEnoughState() {
        ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.setName("bundler");
        String[] pieces = externalId.getExternalIdPieces();
        assertEquals(1, pieces.length);
        assertEquals("bundler", pieces[0]);
    }

    @Test
    public void testCreateBdioId() {
        ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.setName("testName");
        externalId.setVersion("testVersion");
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

    @Test
    public void testMavenWithoutVersion() {
        ExternalId externalId = simpleBdioFactory.createMavenExternalId("thegroup", "thename", null);
        assertEquals("thegroup:thename", externalId.createExternalId());
        assertEquals(new BdioId("http:maven/thegroup/thename"), externalId.createBdioId());
    }

    @Test
    public void testNameWithoutVersion() {
        ExternalId externalId = simpleBdioFactory.createNameVersionExternalId(Forge.RUBYGEMS, "thename", null);
        assertEquals("thename", externalId.createExternalId());
        assertEquals(new BdioId("http:rubygems/thename"), externalId.createBdioId());
    }

    @Test
    public void testYoctoWithoutVersion() {
        ExternalId externalId = simpleBdioFactory.createYoctoExternalId("thelayer", "thename", null);
        assertEquals("thelayer/thename", externalId.createExternalId());
        assertEquals(new BdioId("http:yocto/thelayer/thename"), externalId.createBdioId());
    }

    @Test
    public void testUnexpectedState() {
        ExternalId externalId = new ExternalId(Forge.PYPI);
        externalId.setVersion("1.0.0");
        externalId.setArchitecture("i586");
        assertEquals("1.0.0/i586", externalId.createExternalId());
        assertEquals(new BdioId("http:pypi/1.0.0/i586"), externalId.createBdioId());
    }

}
