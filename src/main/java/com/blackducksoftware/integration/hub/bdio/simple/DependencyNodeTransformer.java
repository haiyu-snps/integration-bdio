package com.blackducksoftware.integration.hub.bdio.simple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.simple.model.BdioBillOfMaterials;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioComponent;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioExternalIdentifier;
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioProject;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;

public class DependencyNodeTransformer {
    private final BdioNodeFactory bdioNodeFactory;

    private final BdioPropertyHelper bdioPropertyHelper;

    public DependencyNodeTransformer(final BdioNodeFactory bdioNodeFactory, final BdioPropertyHelper bdioPropertyHelper) {
        this.bdioNodeFactory = bdioNodeFactory;
        this.bdioPropertyHelper = bdioPropertyHelper;
    }

    /**
     * The root DependencyNode should be the project, and its children would be its direct dependencies.
     */
    public SimpleBdioDocument transformDependencyNode(final String hubCodeLocationName, final DependencyNode root) {
        final String projectName = root.name;
        final String projectVersionName = root.version;

        final BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(hubCodeLocationName, projectName, projectVersionName);

        final String projectId = root.externalId.createDataId();
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(root.externalId);
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId, projectExternalIdentifier);

        for (final DependencyNode child : root.children) {
            final BdioComponent component = componentFromDependencyNode(child);
            bdioPropertyHelper.addRelationship(project, component);
        }

        final SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument();
        simpleBdioDocument.billOfMaterials = billOfMaterials;
        simpleBdioDocument.project = project;

        final List<BdioComponent> bdioComponents = new ArrayList<>();
        for (final DependencyNode child : root.children) {
            transformDependencyGraph(bdioComponents, child, new HashSet<String>());
        }
        simpleBdioDocument.components = bdioComponents;

        return simpleBdioDocument;
    }

    private void transformDependencyGraph(final List<BdioComponent> bdioComponents, final DependencyNode dependencyNode, final Set<String> alreadyAddedIds) {
        transformDependencyNode(bdioComponents, dependencyNode, alreadyAddedIds);

        for (final DependencyNode child : dependencyNode.children) {
            transformDependencyGraph(bdioComponents, child, alreadyAddedIds);
        }
    }

    private void transformDependencyNode(final List<BdioComponent> bdioComponents, final DependencyNode dependencyNode, final Set<String> alreadyAddedIds) {
        final BdioComponent bdioComponent = componentFromDependencyNode(dependencyNode);
        final String dataId = dependencyNode.externalId.createDataId();

        final boolean newId = alreadyAddedIds.add(dataId);
        if (newId) {
            bdioComponents.add(bdioComponent);
            for (final DependencyNode child : dependencyNode.children) {
                final BdioComponent childComponent = componentFromDependencyNode(child);
                bdioPropertyHelper.addRelationship(bdioComponent, childComponent);
            }
        }
    }

    private BdioComponent componentFromDependencyNode(final DependencyNode dependencyNode) {
        final String componentName = dependencyNode.name;
        final String componentVersion = dependencyNode.version;
        final String componentId = dependencyNode.externalId.createDataId();
        final BdioExternalIdentifier componentExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(dependencyNode.externalId);

        final BdioComponent component = bdioNodeFactory.createComponent(componentName, componentVersion, componentId, componentExternalIdentifier);
        return component;
    }

}
