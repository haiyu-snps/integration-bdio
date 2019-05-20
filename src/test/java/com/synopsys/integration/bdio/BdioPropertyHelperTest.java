package com.synopsys.integration.bdio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class BdioPropertyHelperTest {
    private final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    public void testCreatingMavenExternalIds() {
        final ExternalId externalId = externalIdFactory.createMavenExternalId("group", "artifact", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("maven", actualExternalIdentifier.forge);
        assertEquals("group:artifact:version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNpmExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("npmjs", actualExternalIdentifier.forge);
        assertEquals("name/version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNugetExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NUGET, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("nuget", actualExternalIdentifier.forge);
        assertEquals("name/version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingPypiExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("pypi", actualExternalIdentifier.forge);
        assertEquals("name/version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingRubygemsExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("rubygems", actualExternalIdentifier.forge);
        assertEquals("name/version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingCocoapodsExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.COCOAPODS, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("cocoapods", actualExternalIdentifier.forge);
        assertEquals("name:version", actualExternalIdentifier.externalId);
    }

}
