package com.synopsys.integration.bdio.simple;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioReader;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.utility.JsonTestUtils;

public class BdioReaderTest {
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Test
    public void testReaderOnSample() throws FileNotFoundException, IOException, URISyntaxException, JSONException {
        String expectedJson = jsonTestUtils.getExpectedJson("sample.jsonld");

        InputStream reader = new ByteArrayInputStream(expectedJson.getBytes(StandardCharsets.UTF_8.name()));
        SimpleBdioDocument doc = null;
        try (BdioReader bdioReader = new BdioReader(new Gson(), reader)) {
            doc = bdioReader.readSimpleBdioDocument();
        }

        assertNotNull(doc);

        assertNotNull(doc.billOfMaterials);
        assertNotNull(doc.billOfMaterials.relationships);
        assertEquals("1.1.0", doc.billOfMaterials.bdioSpecificationVersion);
        assertEquals(BdioId.createFromUUID("45772d33-5353-44f1-8681-3d8a15540646"), doc.billOfMaterials.id);
        assertEquals("BillOfMaterials", doc.billOfMaterials.type);
        assertTrue(doc.billOfMaterials.creationInfo.getCreator().contains("Tool: integration-bdio-test-0.0.1-SNAPSHOT"));
        assertEquals("gradleTestProject/99.5-SNAPSHOT Black Duck I/O Export", doc.billOfMaterials.spdxName);
        assertEquals(0, doc.billOfMaterials.relationships.size());

        assertNotNull(doc.project);
        assertNotNull(doc.project.bdioExternalIdentifier);
        assertNotNull(doc.project.relationships);
        assertEquals("99.5-SNAPSHOT", doc.project.version);
        assertEquals(new BdioId("http:maven/com_blackducksoftware_gradle_test/gradleTestProject/99_5_SNAPSHOT"), doc.project.id);
        assertEquals("Project", doc.project.type);
        assertEquals("gradleTestProject", doc.project.name);
        assertEquals("maven", doc.project.bdioExternalIdentifier.forge);
        assertEquals("com.blackducksoftware.gradle.test:gradleTestProject:99.5-SNAPSHOT", doc.project.bdioExternalIdentifier.externalId);

        assertEquals(1, doc.project.relationships.size());
        assertEquals(new BdioId("http:maven/org_apache_cxf/cxf_bundle/2_7_7"), doc.project.relationships.get(0).related);
        assertEquals("DYNAMIC_LINK", doc.project.relationships.get(0).relationshipType);

        assertNotNull(doc.components);
        assertEquals(4, doc.components.size());

        BdioComponent first = doc.components.get(0);

        assertNotNull(first);
        assertNotNull(first.bdioExternalIdentifier);
        assertNotNull(first.relationships);

        assertEquals("cxf-bundle", first.name);
        assertEquals("2.7.7", first.version);
        assertEquals(new BdioId("http:maven/org_apache_cxf/cxf_bundle/2_7_7"), first.id);
        assertEquals("Component", first.type);

        assertEquals("maven", first.bdioExternalIdentifier.forge);
        assertEquals("org.apache.cxf:cxf-bundle:2.7.7", first.bdioExternalIdentifier.externalId);

        assertEquals(2, first.relationships.size());
        assertEquals(new BdioId("http:maven/org_apache_velocity/velocity/1_7"), first.relationships.get(0).related);
        assertEquals("DYNAMIC_LINK", first.relationships.get(0).relationshipType);
        assertEquals(new BdioId("http:maven/commons_lang/commons_lang/2_6"), first.relationships.get(1).related);
        assertEquals("DYNAMIC_LINK", first.relationships.get(1).relationshipType);
    }

}
