package com.blackducksoftware.integration.hub.bdio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer;
import com.blackducksoftware.integration.hub.bdio.model.BdioBillOfMaterials;
import com.blackducksoftware.integration.hub.bdio.model.BdioComponent;
import com.blackducksoftware.integration.hub.bdio.model.BdioExternalIdentifier;
import com.blackducksoftware.integration.hub.bdio.model.BdioNode;
import com.blackducksoftware.integration.hub.bdio.model.BdioProject;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;

public class SimpleBdioFactory {
    private final BdioNodeFactory bdioNodeFactory;
    private final BdioPropertyHelper bdioPropertyHelper;
    private final DependencyGraphTransformer dependencyGraphTransformer;

    public SimpleBdioFactory() {
        this.bdioPropertyHelper = new BdioPropertyHelper();
        this.bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        this.dependencyGraphTransformer = new DependencyGraphTransformer(bdioNodeFactory, bdioPropertyHelper);
    }

    public SimpleBdioFactory(final BdioNodeFactory bdioNodeFactory, final BdioPropertyHelper bdioPropertyHelper, final DependencyGraphTransformer dependencyGraphTransformer) {
        this.bdioNodeFactory = bdioNodeFactory;
        this.bdioPropertyHelper = bdioPropertyHelper;
        this.dependencyGraphTransformer = dependencyGraphTransformer;
    }

    public SimpleBdioDocument createSimpleBdioDocument(final String hubCodeLocationName, final String projectName, final String projectVersionName, final ExternalId projectExternalId) {
        final BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(hubCodeLocationName, projectName, projectVersionName);

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

    public SimpleBdioDocument createSimpleBdioDocument(final String hubCodeLocationName, final String projectName, final String projectVersionName, final ExternalId projectExternalId, final DependencyGraph dependencyGraph) {
        final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(hubCodeLocationName, projectName, projectVersionName, projectExternalId);

        populateComponents(simpleBdioDocument, projectExternalId, dependencyGraph);

        return simpleBdioDocument;
    }

}
