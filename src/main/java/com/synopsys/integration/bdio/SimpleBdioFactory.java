/**
 * integration-bdio
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.bdio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraphTransformer;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.BdioBillOfMaterials;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.BdioNode;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class SimpleBdioFactory {
    private final BdioPropertyHelper bdioPropertyHelper;
    private final BdioNodeFactory bdioNodeFactory;
    private final DependencyGraphTransformer dependencyGraphTransformer;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;

    public SimpleBdioFactory() {
        bdioPropertyHelper = new BdioPropertyHelper();
        bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        externalIdFactory = new ExternalIdFactory();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public SimpleBdioFactory(BdioPropertyHelper bdioPropertyHelper, BdioNodeFactory bdioNodeFactory, DependencyGraphTransformer dependencyGraphTransformer, ExternalIdFactory externalIdFactory, Gson gson) {
        this.bdioPropertyHelper = bdioPropertyHelper;
        this.bdioNodeFactory = bdioNodeFactory;
        this.dependencyGraphTransformer = dependencyGraphTransformer;
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
    }

    public MutableDependencyGraph createMutableDependencyGraph() {
        return new MutableMapDependencyGraph();
    }

    public Dependency createDependency(String name, String version, ExternalId externalId) {
        return new Dependency(name, version, externalId);
    }

    public BdioWriter createBdioWriter(Writer writer) throws IOException {
        return new BdioWriter(gson, writer);
    }

    public BdioWriter createBdioWriter(OutputStream outputStream) throws IOException {
        return new BdioWriter(gson, outputStream);
    }

    public void writeSimpleBdioDocument(BdioWriter bdioWriter, SimpleBdioDocument simpleBdioDocument) {
        bdioWriter.writeSimpleBdioDocument(simpleBdioDocument);
    }

    public void writeSimpleBdioDocumentToFile(File bdioFile, SimpleBdioDocument simpleBdioDocument) throws IOException {
        try (BdioWriter bdioWriter = createBdioWriter(new FileOutputStream(bdioFile))) {
            writeSimpleBdioDocument(bdioWriter, simpleBdioDocument);
        }
    }

    public SimpleBdioDocument createSimpleBdioDocument(String codeLocationName, String projectName, String projectVersionName, ExternalId projectExternalId) {
        BdioId projectId = projectExternalId.createBdioId();
        BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId);

        BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(projectExternalId);
        project.bdioExternalIdentifier = projectExternalIdentifier;

        return createSimpleBdioDocument(codeLocationName, project);
    }

    public SimpleBdioDocument createSimpleBdioDocument(String codeLocationName, String projectName, String projectVersionName) {
        BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName);
        return createSimpleBdioDocument(codeLocationName, project);
    }

    private SimpleBdioDocument createSimpleBdioDocument(String codeLocationName, BdioProject project) {
        BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(codeLocationName, project.name, project.version);

        SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument();
        simpleBdioDocument.setBillOfMaterials(billOfMaterials);
        simpleBdioDocument.setProject(project);

        return simpleBdioDocument;
    }

    public void populateComponents(SimpleBdioDocument simpleBdioDocument, ExternalId projectExternalId, DependencyGraph dependencyGraph) {
        Map<ExternalId, BdioNode> existingComponents = new HashMap<>();
        existingComponents.put(projectExternalId, simpleBdioDocument.getProject());

        List<BdioComponent> bdioComponents = dependencyGraphTransformer.transformDependencyGraph(dependencyGraph, simpleBdioDocument.getProject(), dependencyGraph.getRootDependencies(), existingComponents);
        simpleBdioDocument.setComponents(bdioComponents);
    }

    public SimpleBdioDocument createSimpleBdioDocument(String projectName, String projectVersionName, ExternalId projectExternalId) {
        return createSimpleBdioDocument(null, projectName, projectVersionName, projectExternalId);
    }

    public SimpleBdioDocument createSimpleBdioDocument(String projectName, String projectVersionName, ExternalId projectExternalId, DependencyGraph dependencyGraph) {
        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(projectName, projectVersionName, projectExternalId);

        populateComponents(simpleBdioDocument, projectExternalId, dependencyGraph);

        return simpleBdioDocument;
    }

    public SimpleBdioDocument createSimpleBdioDocument(String codeLocationName, String projectName, String projectVersionName, ExternalId projectExternalId, DependencyGraph dependencyGraph) {
        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId);

        populateComponents(simpleBdioDocument, projectExternalId, dependencyGraph);

        return simpleBdioDocument;
    }

    public ExternalId createPathExternalId(Forge forge, String path) {
        return externalIdFactory.createPathExternalId(forge, path);
    }

    public ExternalId createModuleNamesExternalId(Forge forge, String... moduleNames) {
        return externalIdFactory.createModuleNamesExternalId(forge, moduleNames);
    }

    public ExternalId createNameVersionExternalId(Forge forge, String name, String version) {
        return externalIdFactory.createNameVersionExternalId(forge, name, version);
    }

    public ExternalId createNameVersionExternalId(Forge forge, String name) {
        return externalIdFactory.createNameVersionExternalId(forge, name);
    }

    public ExternalId createYoctoExternalId(String layer, String name, String version) {
        return externalIdFactory.createYoctoExternalId(layer, name, version);
    }

    public ExternalId createYoctoExternalId(String layer, String name) {
        return externalIdFactory.createYoctoExternalId(layer, name);
    }

    public ExternalId createMavenExternalId(String group, String name, String version) {
        return externalIdFactory.createMavenExternalId(group, name, version);
    }

    public ExternalId createMavenExternalId(String group, String name) {
        return externalIdFactory.createMavenExternalId(group, name);
    }

    public ExternalId createArchitectureExternalId(Forge forge, String name, String version, String architecture) {
        return externalIdFactory.createArchitectureExternalId(forge, name, version, architecture);
    }

    public ExternalId createArchitectureExternalId(Forge forge, String name, String architecture) {
        return externalIdFactory.createArchitectureExternalId(forge, name, architecture);
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
