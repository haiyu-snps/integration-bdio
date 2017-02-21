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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

public class BdioWriterTest {
    @Test
    public void testJsonWriter() throws FileNotFoundException, IOException, URISyntaxException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("sample.jsonld");
        final File file = new File(url.toURI().getPath());
        final String expectedJson = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

        final BdioBillOfMaterials bdioBillOfMaterials = new BdioBillOfMaterials();
        bdioBillOfMaterials.setId("uuid:45772d33-5353-44f1-8681-3d8a15540646");
        bdioBillOfMaterials.setName("gradleTestProject Black Duck I/O Export");
        bdioBillOfMaterials.setSpecVersion("1.1.0");

        final BdioProject bdioProject = new BdioProject();
        bdioProject.setId("mvn:com.blackducksoftware.gradle.test/gradleTestProject/99.5-SNAPSHOT");
        bdioProject.setName("gradleTestProject_ek2");
        bdioProject.setRevision("99.6-SNAPSHOT");
        bdioProject.setBdioExternalIdentifier(createBdioExternalIdentifier("maven", "com.blackducksoftware.gradle.test:gradleTestProject:99.5-SNAPSHOT"));

        final List<BdioRelationship> projectRelationships = new ArrayList<>();
        projectRelationships.add(createBdioRelationship("mvn:org.apache.cxf/cxf-bundle/2.7.7"));
        // projectRelationships.add(createBdioRelationship("mvn:commons-lang:commons-lang:2.6"));
        bdioProject.setRelationship(projectRelationships);

        final BdioComponent bdioComponent1 = createComponent("mvn:org.apache.cxf/cxf-bundle/2.7.7", "2.7.7", "maven", "org.apache.cxf:cxf-bundle:2.7.7");
        final BdioComponent bdioComponent2 = createComponent("mvn:org.apache.velocity/velocity/1.7", "1.7", "maven", "org.apache.velocity:velocity:1.7");
        final BdioComponent bdioComponent3 = createComponent("mvn:commons-collections:commons-collections:3.2.1", "3.2.1", "maven",
                "commons-collections:commons-collections:3.2.1");
        final BdioComponent bdioComponent4 = createComponent("mvn:commons-lang:commons-lang:2.6", "2.6", "maven", "commons-lang:commons-lang:2.6");

        final List<BdioRelationship> velocityRelationships = new ArrayList<>();
        velocityRelationships.add(createBdioRelationship("mvn:commons-collections/commons-collections/3.2.1"));
        velocityRelationships.add(createBdioRelationship("mvn:commons-lang/commons-lang/2.6"));
        bdioComponent2.setRelationship(velocityRelationships);

        final List<BdioRelationship> cxfRelationships = new ArrayList<>();
        cxfRelationships.add(createBdioRelationship("mvn:org.apache.velocity/velocity/1.7"));
        cxfRelationships.add(createBdioRelationship("mvn:commons-lang/commons-lang/2.6"));
        bdioComponent1.setRelationship(cxfRelationships);

        final List<BdioNode> bdioNodes = new ArrayList<>();
        bdioNodes.add(bdioBillOfMaterials);
        bdioNodes.add(bdioProject);
        bdioNodes.add(bdioComponent1);
        bdioNodes.add(bdioComponent2);
        bdioNodes.add(bdioComponent3);
        bdioNodes.add(bdioComponent4);

        final StringWriter writer = new StringWriter();
        try (BdioWriter bdioWriter = new BdioWriter(new Gson(), writer)) {
            bdioWriter.writeBdioNodes(bdioNodes);
        }

        assertEquals(expectedJson.replaceAll("\\s", ""), writer.toString().replaceAll("\\s", ""));
    }

    private BdioComponent createComponent(final String id, final String revision, final String externalSystemTypeId, final String externalId) {
        final BdioComponent bdioComponent = new BdioComponent();
        bdioComponent.setId(id);
        bdioComponent.setRevision(revision);
        bdioComponent.setBdioExternalIdentifier(createBdioExternalIdentifier(externalSystemTypeId, externalId));
        return bdioComponent;
    }

    private BdioExternalIdentifier createBdioExternalIdentifier(final String externalSystemTypeId, final String externalId) {
        final BdioExternalIdentifier bdioExternalIdentifier = new BdioExternalIdentifier();
        bdioExternalIdentifier.setExternalSystemTypeId(externalSystemTypeId);
        bdioExternalIdentifier.setExternalId(externalId);
        return bdioExternalIdentifier;
    }

    private BdioRelationship createBdioRelationship(final String related) {
        final BdioRelationship bdioRelationship = new BdioRelationship();
        bdioRelationship.setRelated(related);
        bdioRelationship.setRelationshipType("DYNAMIC_LINK");
        return bdioRelationship;
    }
}
