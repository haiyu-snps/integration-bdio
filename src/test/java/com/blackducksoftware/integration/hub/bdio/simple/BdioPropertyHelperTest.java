package com.blackducksoftware.integration.hub.bdio.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.BdioExternalIdentifier;

public class BdioPropertyHelperTest {
    private final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();

    @Test
    public void testCreatingBdioId() {
        assertEquals("data:name/version", bdioPropertyHelper.createBdioId("name", "version"));
        assertEquals("data:group/artifact/version", bdioPropertyHelper.createBdioId("group", "artifact", "version"));
    }

    @Test
    public void testCreatingMavenExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createMavenExternalIdentifier("group", "artifact", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("maven", "group:artifact:version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNpmExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createNpmExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("npm", "name@version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNugetExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createNugetExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("nuget", "name/version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingPypiExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createPypiExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("pypi", "name/version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingRubygemsExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createRubygemsExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("rubygems", "name=version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

}
