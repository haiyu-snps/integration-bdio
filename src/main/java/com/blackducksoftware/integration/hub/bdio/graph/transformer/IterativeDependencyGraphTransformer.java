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
package com.blackducksoftware.integration.hub.bdio.graph.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory;
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.BdioBillOfMaterials;
import com.blackducksoftware.integration.hub.bdio.model.BdioComponent;
import com.blackducksoftware.integration.hub.bdio.model.BdioExternalIdentifier;
import com.blackducksoftware.integration.hub.bdio.model.BdioNode;
import com.blackducksoftware.integration.hub.bdio.model.BdioProject;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;

public class IterativeDependencyGraphTransformer implements DependencyGraphTransformer {
    private final BdioNodeFactory bdioNodeFactory;
    private final BdioPropertyHelper bdioPropertyHelper;

    public IterativeDependencyGraphTransformer(final BdioNodeFactory bdioNodeFactory, final BdioPropertyHelper bdioPropertyHelper) {
        this.bdioNodeFactory = bdioNodeFactory;
        this.bdioPropertyHelper = bdioPropertyHelper;
    }

    @Override
    public SimpleBdioDocument transformDependencyGraph(final String projectName, final String projectVersionName, final ExternalId projectExternalId, final DependencyGraph graph) {
        return transformDependencyGraph(null, projectName, projectVersionName, projectExternalId, graph);
    }

    /**
     * The hubCodeLocationName is optional and will likely be null for most cases.
     */
    @Override
    public SimpleBdioDocument transformDependencyGraph(final String hubCodeLocationName, final String projectName, final String projectVersionName, final ExternalId projectExternalId, final DependencyGraph graph) {
        final BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(hubCodeLocationName, projectName, projectVersionName);

        final String projectId = projectExternalId.createBdioId();
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(projectExternalId);
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId, projectExternalIdentifier);

        final SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument();
        simpleBdioDocument.billOfMaterials = billOfMaterials;
        simpleBdioDocument.project = project;

        final Map<ExternalId, BdioNode> existingComponents = new HashMap<>();
        existingComponents.put(projectExternalId, project);
        final List<BdioComponent> bdioComponents = transformDependencyGraph(graph, project, graph.getRootDependencies(), existingComponents);
        simpleBdioDocument.components = bdioComponents;

        return simpleBdioDocument;
    }

    @Override
    public List<BdioComponent> transformDependencyGraph(final DependencyGraph graph, final BdioNode currentNode, final Set<Dependency> dependencies, final Map<ExternalId, BdioNode> existingComponents) {
        final List<BdioComponent> newComponents = new ArrayList<>();
        final Queue<Dependency> unprocessed = new LinkedList<>(dependencies);

        while (unprocessed.size() > 0) {
            final Dependency next = unprocessed.remove();

            if (existingComponents.containsKey(next.externalId)) {
                continue;
            }

            final BdioComponent newNode = componentFromDependency(next);
            newComponents.add(newNode);
            existingComponents.put(next.externalId, newNode);

            final Set<Dependency> children = graph.getChildrenForParent(next);
            for (final Dependency child : children) {
                bdioPropertyHelper.addRelationship(newNode, componentFromDependency(child));

                if (!existingComponents.containsKey(child.externalId)) {
                    unprocessed.add(child);
                }
            }
        }

        for (final Dependency rootChild : graph.getRootDependencies()) {
            bdioPropertyHelper.addRelationship(currentNode, existingComponents.get(rootChild.externalId));
        }

        return newComponents;
    }

    private BdioComponent componentFromDependency(final Dependency dependency) {
        final String componentName = dependency.name;
        final String componentVersion = dependency.version;
        final String componentId = dependency.externalId.createBdioId();
        final BdioExternalIdentifier componentExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(dependency.externalId);

        final BdioComponent component = bdioNodeFactory.createComponent(componentName, componentVersion, componentId, componentExternalIdentifier);
        return component;
    }

}
