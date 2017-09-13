/**
 * Integration Bdio
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.bdio.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.Dependency;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;
import com.google.gson.Gson;

public class DependencyGraphTransformerTest {
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Test
    public void testTransformingDependencyNodes() throws URISyntaxException, IOException, JSONException {

        final Set<Dependency> projectDependencies = new HashSet<>();
        final ExternalId projectExternalId = new MavenExternalId(Forge.MAVEN, "projectGroup", "projectName", "projectVersion");
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        final ExternalId childExternalId = new MavenExternalId(Forge.MAVEN, "componentGroup1", "componentArtifact1", "1.0.0");
        final Dependency child = new Dependency("componentArtifact1", "1.0.0", childExternalId);
        dependencyGraph.addChildrenToRoot(child);

        final ExternalId transitiveExternalId = new MavenExternalId(Forge.MAVEN, "transitiveGroup", "transitiveArtifact", "2.1.0");
        final Dependency transitive = new Dependency("transitiveArtifact", "2.1.0", transitiveExternalId);
        dependencyGraph.addParentWithChild(child, transitive);

        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyNodeTransformer = new RecursiveDependencyGraphTransformer(bdioNodeFactory, bdioPropertyHelper);
        final SimpleBdioDocument simpleBdioDocument = dependencyNodeTransformer.transformDependencyGraph(null, "projectName", "projectVersion", projectExternalId, dependencyGraph);

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
