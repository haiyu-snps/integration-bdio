package com.blackducksoftware.integration.hub.bdio.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BdioCreationInfoTest {
    @Test
    public void testAddingMultipleCreators() {
        final BdioCreationInfo bdioCreationInfo = new BdioCreationInfo();
        bdioCreationInfo.addSpdxCreator(SpdxCreator.createToolSpdxCreator("a simple tool", "2.0"));
        bdioCreationInfo.addSpdxCreator(SpdxCreator.createToolSpdxCreator("another simple tool", "1.0"));

        assertEquals(2, bdioCreationInfo.getCreator().size());
        assertEquals("Tool: a simple tool-2.0", bdioCreationInfo.getCreator().get(0));
    }

    @Test
    public void testSettingPrimaryCreator() {
        final BdioCreationInfo bdioCreationInfo = new BdioCreationInfo();
        bdioCreationInfo.addSpdxCreator(SpdxCreator.createToolSpdxCreator("a simple tool", "2.0"));
        bdioCreationInfo.setPrimarySpdxCreator(SpdxCreator.createToolSpdxCreator("another simple tool", "1.0"));

        assertEquals(2, bdioCreationInfo.getCreator().size());
        assertEquals("Tool: another simple tool-1.0", bdioCreationInfo.getCreator().get(0));
    }

}
