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
package com.synopsys.integration.bdio.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.bdio.BdioNodeFactory;
import com.synopsys.integration.bdio.BdioPropertyHelper;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.bdio.model.BdioNode;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class DependencyGraphTransformer {
    private final BdioPropertyHelper bdioPropertyHelper;
    private final BdioNodeFactory bdioNodeFactory;

    public DependencyGraphTransformer(final BdioPropertyHelper bdioPropertyHelper, final BdioNodeFactory bdioNodeFactory) {
        this.bdioPropertyHelper = bdioPropertyHelper;
        this.bdioNodeFactory = bdioNodeFactory;
    }

    public List<BdioComponent> transformDependencyGraph(final DependencyGraph graph, final BdioNode currentNode, final Set<Dependency> dependencies, final Map<ExternalId, BdioNode> existingComponents) {
        final List<BdioComponent> addedComponents = new ArrayList<>();
        for (final Dependency dependency : dependencies) {
            if (!existingComponents.containsKey(dependency.externalId)) {
                final BdioComponent addedNode = componentFromDependency(dependency);
                addedComponents.add(addedNode);
                existingComponents.put(dependency.externalId, addedNode);
                final List<BdioComponent> addedChildren = transformDependencyGraph(graph, addedNode, graph.getChildrenForParent(dependency), existingComponents);
                addedComponents.addAll(addedChildren);
            }
            bdioPropertyHelper.addRelationship(currentNode, existingComponents.get(dependency.externalId));
        }

        return addedComponents;
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
