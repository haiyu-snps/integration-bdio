/**
 * integration-bdio
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.bdio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.BdioBillOfMaterials;
import com.blackducksoftware.integration.hub.bdio.model.BdioComponent;
import com.blackducksoftware.integration.hub.bdio.model.BdioExternalIdentifier;
import com.blackducksoftware.integration.hub.bdio.model.BdioNode;
import com.blackducksoftware.integration.hub.bdio.model.BdioProject;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SimpleBdioFactory {
    private final BdioPropertyHelper bdioPropertyHelper;
    private final BdioNodeFactory bdioNodeFactory;
    private final DependencyGraphTransformer dependencyGraphTransformer;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;

    public SimpleBdioFactory() {
        this.bdioPropertyHelper = new BdioPropertyHelper();
        this.bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        this.dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        this.externalIdFactory = new ExternalIdFactory();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public SimpleBdioFactory(final BdioPropertyHelper bdioPropertyHelper, final BdioNodeFactory bdioNodeFactory, final DependencyGraphTransformer dependencyGraphTransformer, final ExternalIdFactory externalIdFactory, final Gson gson) {
        this.bdioPropertyHelper = bdioPropertyHelper;
        this.bdioNodeFactory = bdioNodeFactory;
        this.dependencyGraphTransformer = dependencyGraphTransformer;
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
    }

    public MutableDependencyGraph createMutableDependencyGraph() {
        return new MutableMapDependencyGraph();
    }

    public Dependency createDependency(final String name, final String version, final ExternalId externalId) {
        return new Dependency(name, version, externalId);
    }

    public BdioWriter createBdioWriter(final Writer writer) throws IOException {
        return new BdioWriter(gson, writer);
    }

    public BdioWriter createBdioWriter(final OutputStream outputStream) throws IOException {
        return new BdioWriter(gson, outputStream);
    }

    public void writeSimpleBdioDocument(final BdioWriter bdioWriter, final SimpleBdioDocument simpleBdioDocument) {
        bdioWriter.writeSimpleBdioDocument(simpleBdioDocument);
    }

    public void writeSimpleBdioDocumentToFile(final File bdioFile, final SimpleBdioDocument simpleBdioDocument) throws IOException {
        try (BdioWriter bdioWriter = createBdioWriter(new FileOutputStream(bdioFile))) {
            writeSimpleBdioDocument(bdioWriter, simpleBdioDocument);
        }
    }

    public SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, final String projectName, final String projectVersionName, final ExternalId projectExternalId) {
        final BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(codeLocationName, projectName, projectVersionName);

        final String projectId = projectExternalId.createBdioId();
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(projectExternalId);
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId, projectExternalIdentifier);

        final SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument();
        simpleBdioDocument.billOfMaterials = billOfMaterials;
        simpleBdioDocument.project = project;

        return simpleBdioDocument;
    }

    public void populateComponents(final SimpleBdioDocument simpleBdioDocument, final ExternalId projectExternalId, final DependencyGraph dependencyGraph) {
        final Map<ExternalId, BdioNode> existingComponents = new HashMap<>();
        existingComponents.put(projectExternalId, simpleBdioDocument.project);

        final List<BdioComponent> bdioComponents = dependencyGraphTransformer.transformDependencyGraph(dependencyGraph, simpleBdioDocument.project, dependencyGraph.getRootDependencies(), existingComponents);
        simpleBdioDocument.components = bdioComponents;
    }

    public SimpleBdioDocument createSimpleBdioDocument(final String projectName, final String projectVersionName, final ExternalId projectExternalId) {
        return createSimpleBdioDocument(null, projectName, projectVersionName, projectExternalId);
    }

    public SimpleBdioDocument createSimpleBdioDocument(final String projectName, final String projectVersionName, final ExternalId projectExternalId, final DependencyGraph dependencyGraph) {
        final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(projectName, projectVersionName, projectExternalId);

        populateComponents(simpleBdioDocument, projectExternalId, dependencyGraph);

        return simpleBdioDocument;
    }

    public SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, final String projectName, final String projectVersionName, final ExternalId projectExternalId, final DependencyGraph dependencyGraph) {
        final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId);

        populateComponents(simpleBdioDocument, projectExternalId, dependencyGraph);

        return simpleBdioDocument;
    }

    public ExternalId createNameVersionExternalId(final Forge forge, final String name, final String version) {
        return externalIdFactory.createNameVersionExternalId(forge, name, version);
    }

    public ExternalId createMavenExternalId(final String group, final String name, final String version) {
        return externalIdFactory.createMavenExternalId(group, name, version);
    }

    public ExternalId createArchitectureExternalId(final Forge forge, final String name, final String version, final String architecture) {
        return externalIdFactory.createArchitectureExternalId(forge, name, version, architecture);
    }

    public ExternalId createModuleNamesExternalId(final Forge forge, final String... moduleNames) {
        return externalIdFactory.createModuleNamesExternalId(forge, moduleNames);
    }

    public ExternalId createPathExternalId(final Forge forge, final String path) {
        return externalIdFactory.createPathExternalId(forge, path);
    }

    public BdioPropertyHelper getBdioPropertyHelper() {
        return bdioPropertyHelper;
    }

    public BdioNodeFactory getBdioNodeFactory() {
        return bdioNodeFactory;
    }

    public DependencyGraphTransformer getDependencyGraphTransformer() {
        return dependencyGraphTransformer;
    }

    public ExternalIdFactory getExternalIdFactory() {
        return externalIdFactory;
    }
}
