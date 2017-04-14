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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;
import com.google.gson.Gson;

public class DependencyNodeTransformerTest {
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Test
    public void testTransformingDependencyNodes() throws URISyntaxException, IOException, JSONException {
        final List<DependencyNode> projectDependencies = new ArrayList<>();
        final ExternalId projectExternalId = new MavenExternalId(Forge.maven, "projectGroup", "projectName", "projectVersion");
        final DependencyNode root = new DependencyNode("projectName", "projectVersion", projectExternalId, projectDependencies);

        final ExternalId childExternalId = new MavenExternalId(Forge.maven, "componentGroup1", "componentArtifact1", "1.0.0");
        final DependencyNode child = new DependencyNode("componentArtifact1", "1.0.0", childExternalId, null);
        projectDependencies.add(child);

        final ExternalId transitiveExternalId = new MavenExternalId(Forge.maven, "transitiveGroup", "transitiveArtifact", "2.1.0");
        final DependencyNode transitive = new DependencyNode("transitiveArtifact", "2.1.0", transitiveExternalId, null);
        child.children = Arrays.asList(new DependencyNode[] { transitive });

        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyNodeTransformer dependencyNodeTransformer = new DependencyNodeTransformer(bdioNodeFactory, bdioPropertyHelper);
        final SimpleBdioDocument simpleBdioDocument = dependencyNodeTransformer.transformDependencyNode(root);

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
