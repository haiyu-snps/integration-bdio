/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class SimpleBdioFactory {
    private final BdioPropertyHelper bdioPropertyHelper;
    private final BdioNodeFactory bdioNodeFactory;
    private final DependencyGraphTransformer dependencyGraphTransformer;
    private final ExternalIdFactory externalIdFactory;
    private final DependencyFactory dependencyFactory;
    private final Gson gson;

    public SimpleBdioFactory() {
        bdioPropertyHelper = new BdioPropertyHelper();
        bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        externalIdFactory = new ExternalIdFactory();
        dependencyFactory = new DependencyFactory(externalIdFactory);
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public SimpleBdioFactory(BdioPropertyHelper bdioPropertyHelper, BdioNodeFactory bdioNodeFactory, DependencyGraphTransformer dependencyGraphTransformer, ExternalIdFactory externalIdFactory, DependencyFactory dependencyFactory, Gson gson) {
        this.bdioPropertyHelper = bdioPropertyHelper;
        this.bdioNodeFactory = bdioNodeFactory;
        this.dependencyGraphTransformer = dependencyGraphTransformer;
        this.externalIdFactory = externalIdFactory;
        this.dependencyFactory = dependencyFactory;
        this.gson = gson;
    }

    public MutableDependencyGraph createMutableDependencyGraph() {
        return new MutableMapDependencyGraph();
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

    public DependencyFactory getDependencyFactory() {
        return dependencyFactory;
    }

}
