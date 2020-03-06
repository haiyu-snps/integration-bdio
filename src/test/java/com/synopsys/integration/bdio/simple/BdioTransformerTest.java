package com.synopsys.integration.bdio.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioReader;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioRelationship;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.utility.JsonTestUtils;

public class BdioTransformerTest {
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Test
    public void testTransformingDependencyGraphSample() throws URISyntaxException, IOException, JSONException {
        testTransformingDependencyGraphs("sample.jsonld", true);
    }

    @Test
    public void testTransformingDependencyGraphSampleEdge() throws URISyntaxException, IOException, JSONException {
        // this is testing re-creating a simple bdio document when the externalIdMetadata fields are not populated
        // in practice, this should not happen with files created with this library, but may happen with older versions, or other bdio files.
        testTransformingDependencyGraphs("sample-edge.jsonld", false);
    }

    public void testTransformingDependencyGraphs(String filename, boolean testEqualityOfMetadata) throws URISyntaxException, IOException {
        String expectedJson = jsonTestUtils.getExpectedJson(filename);

        Reader reader = new StringReader(expectedJson);
        SimpleBdioDocument doc;
        try (BdioReader bdioReader = new BdioReader(new Gson(), reader)) {
            doc = bdioReader.readSimpleBdioDocument();
        }

        Map<String, Forge> forgeMap = new HashMap<>();
        forgeMap.put("maven", Forge.MAVEN);
        BdioTransformer transformer = new BdioTransformer(forgeMap);

        DependencyGraph graph = transformer.transformToDependencyGraph(doc.getProject(), doc.getComponents());

        assertEquals(1, graph.getRootDependencies().size());

        ExternalId projectId = new ExternalId(Forge.MAVEN);
        projectId.setGroup("com.blackducksoftware.gradle.test");
        projectId.setName("gradleTestProject");
        projectId.setVersion("99.5-SNAPSHOT");

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(doc.getProject().name, doc.getProject().version, projectId, graph);

        simpleBdioDocument.getBillOfMaterials().id = doc.getBillOfMaterials().id;
        simpleBdioDocument.getBillOfMaterials().creationInfo = doc.getBillOfMaterials().creationInfo;

        assertTrue(EqualsBuilder.reflectionEquals(simpleBdioDocument.getBillOfMaterials(), doc.getBillOfMaterials()));
        assertTrue(EqualsBuilder.reflectionEquals(simpleBdioDocument.getProject(), doc.getProject(), "bdioExternalIdentifier", "relationships"));
        if (!testEqualityOfMetadata) {
            simpleBdioDocument.getProject().bdioExternalIdentifier.externalIdMetaData = null;
        }
        assertEquals(simpleBdioDocument.getProject().bdioExternalIdentifier.externalId, doc.getProject().bdioExternalIdentifier.externalId, "externalIdMetaData");
        assertRelationships(simpleBdioDocument.getProject().relationships, doc.getProject().relationships);

        assertEquals(doc.getComponents().size(), simpleBdioDocument.getComponents().size());
        for (BdioComponent expected : simpleBdioDocument.getComponents()) {
            boolean fnd = false;
            for (BdioComponent actual : doc.getComponents()) {
                if (expected.id.equals(actual.id)) {
                    assertFalse(fnd);
                    fnd = true;

                    assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "bdioExternalIdentifier", "relationships"));
                    if (!testEqualityOfMetadata) {
                        expected.bdioExternalIdentifier.externalIdMetaData = null;
                    }
                    assertEquals(expected.bdioExternalIdentifier.externalId, actual.bdioExternalIdentifier.externalId, "externalIdMetaData");
                    assertRelationships(expected.relationships, actual.relationships);

                }
            }
            assertTrue(fnd, expected.id.toString());
        }
    }

    private void assertRelationships(List<BdioRelationship> expectedList, List<BdioRelationship> actualList) {
        assertEquals(expectedList.size(), actualList.size());
        for (BdioRelationship expected : expectedList) {
            boolean fnd = false;
            for (BdioRelationship actual : actualList) {
                if (expected.related.equals(actual.related)) {
                    assertFalse(fnd);
                    fnd = true;

                    assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
                }
            }
            assertTrue(fnd, expected.related.toString());
        }
    }

}
