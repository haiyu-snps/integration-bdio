package com.synopsys.integration.bdio.graph.transformer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioWriter;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.bdio.utility.JsonTestUtils;

public class DependencyGraphTransformerTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Test
    public void testTransformingDocument() throws URISyntaxException, IOException, JSONException {
        final ExternalId projectExternalId = externalIdFactory.createMavenExternalId("projectGroup", "projectName", "projectVersion");
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        final ExternalId childExternalId = externalIdFactory.createMavenExternalId("componentGroup1", "componentArtifact1", "1.0.0");
        final Dependency child = new Dependency("componentArtifact1", "1.0.0", childExternalId);
        dependencyGraph.addChildrenToRoot(child);

        final ExternalId transitiveExternalId = externalIdFactory.createMavenExternalId("transitiveGroup", "transitiveArtifact", "2.1.0");
        final Dependency transitive = new Dependency("transitiveArtifact", "2.1.0", transitiveExternalId);
        dependencyGraph.addParentWithChild(child, transitive);

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(null, "projectName", "projectVersion", projectExternalId, dependencyGraph);

        // we are overriding the default value of a new creation info just to pass the json comparison
        simpleBdioDocument.billOfMaterials.creationInfo = null;

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.billOfMaterials.id = "uuid:123";

        final String expectedJson = jsonTestUtils.getExpectedJson("transformer.jsonld");

        final Writer writer = new StringWriter();
        try (BdioWriter bdioWriter = new BdioWriter(new Gson(), writer)) {
            bdioWriter.writeSimpleBdioDocument(simpleBdioDocument);
        }

        final String actualJson = writer.toString();
        jsonTestUtils.verifyJsonArraysEqual(expectedJson, actualJson);
    }

}
