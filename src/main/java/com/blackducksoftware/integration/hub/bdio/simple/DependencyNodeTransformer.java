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
     * The root DependencyNode should be the project, and its children would be its direct dependencies. This is
     * equivalent to calling transformDependencyNode(null, root).
     */
    public SimpleBdioDocument transformDependencyNode(final DependencyNode root) {
        return transformDependencyNode(null, root);
    }

    /**
     * The root DependencyNode should be the project, and its children would be its direct dependencies. The
     * hubCodeLocationName is optional and will likely be null for most cases.
     */
    public SimpleBdioDocument transformDependencyNode(final String hubCodeLocationName, final DependencyNode root) {
        final String projectName = root.name;
        final String projectVersionName = root.version;

        final BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(hubCodeLocationName, projectName, projectVersionName);

        final String projectId = root.externalId.createDataId();
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(root.externalId);
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId, projectExternalIdentifier);

        final SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument();
        simpleBdioDocument.billOfMaterials = billOfMaterials;
        simpleBdioDocument.project = project;

        for (final DependencyNode child : root.children) {
            final BdioComponent component = componentFromDependencyNode(child);
            bdioPropertyHelper.addRelationship(project, component);
        }

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
