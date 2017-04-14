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

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.model.BdioBillOfMaterials;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioComponent;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioExternalIdentifier;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioProject;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class BdioWriter implements Closeable {
    private final Gson gson;

    private final JsonWriter jsonWriter;

    public BdioWriter(final Gson gson, final Writer writer) throws IOException {
        this.gson = gson;
        this.jsonWriter = new JsonWriter(writer);
        jsonWriter.setIndent("  ");
        jsonWriter.beginArray();
    }

    public BdioWriter(final Gson gson, final OutputStream outputStream) throws IOException {
        this.gson = gson;
        this.jsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        jsonWriter.setIndent("  ");
        jsonWriter.beginArray();
    }

    public void writeSimpleBdioDocument(final SimpleBdioDocument simpleBdioDocument) {
        final List<BdioNode> bdioNodes = new ArrayList<>();
        bdioNodes.add(simpleBdioDocument.billOfMaterials);
        bdioNodes.add(simpleBdioDocument.project);
        bdioNodes.addAll(simpleBdioDocument.components);

        writeBdioNodes(bdioNodes);
    }

    public void writeProject(final String hubCodeLocationName, final DependencyNode root)
            throws IOException {
        final BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(hubCodeLocationName, projectName, projectVersionName);

        final String projectId = idFromGav(root.getGav());
        final BdioExternalIdentifier projectExternalIdentifier = externalIdentifierFromGav(root.getGav());
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId, projectExternalIdentifier);

        for (final DependencyNode child : root.getChildren()) {
            final BdioComponent component = componentFromDependencyNode(child);
            bdioPropertyHelper.addRelationship(project, component);
        }

        try (BdioWriter bdioWriter = new BdioWriter(new Gson(), outputStream)) {
            bdioWriter.writeBdioNode(billOfMaterials);
            bdioWriter.writeBdioNode(project);

            for (final DependencyNode child : root.getChildren()) {
                writeDependencyGraph(bdioWriter, child);
            }
        }
    }

    private void writeDependencyGraph(final BdioWriter writer, final DependencyNode dependencyNode) throws IOException {
        writeDependencyNode(writer, dependencyNode);

        for (final DependencyNode child : dependencyNode.getChildren()) {
            writeDependencyGraph(writer, child);
        }
    }

    private void writeDependencyNode(final BdioWriter writer, final DependencyNode dependencyNode) throws IOException {
        final BdioComponent bdioComponent = componentFromDependencyNode(dependencyNode);
        final BdioExternalIdentifier externalIdentifier = bdioComponent.bdioExternalIdentifier;
        boolean alreadyAdded = false;
        if (!externalIds.add(externalIdentifier.externalId)) {
            alreadyAdded = true;
        }

        if (!alreadyAdded) {
            for (final DependencyNode child : dependencyNode.getChildren()) {
                final BdioComponent childComponent = componentFromDependencyNode(child);
                bdioPropertyHelper.addRelationship(bdioComponent, childComponent);
            }
            writer.writeBdioNode(bdioComponent);
        }
    }

    private BdioComponent componentFromDependencyNode(final DependencyNode dependencyNode) {
        final String componentName = dependencyNode.getGav().getArtifactId();
        final String componentVersion = dependencyNode.getGav().getVersion();
        final String componentId = idFromGav(dependencyNode.getGav());
        final BdioExternalIdentifier componentExternalIdentifier = externalIdentifierFromGav(dependencyNode.getGav());

        final BdioComponent component = bdioNodeFactory.createComponent(componentName, componentVersion, componentId, componentExternalIdentifier);
        return component;
    }

    public void writeBdioNodes(final List<BdioNode> bdioNodes) {
        for (final BdioNode bdioNode : bdioNodes) {
            writeBdioNode(bdioNode);
        }
    }

    public void writeBdioNode(final BdioNode bdioNode) {
        gson.toJson(bdioNode, bdioNode.getClass(), jsonWriter);
    }

    @Override
    public void close() throws IOException {
        jsonWriter.endArray();
        jsonWriter.close();
    }

}
