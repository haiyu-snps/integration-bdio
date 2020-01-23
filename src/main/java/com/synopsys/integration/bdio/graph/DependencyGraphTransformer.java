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
package com.synopsys.integration.bdio.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.bdio.BdioNodeFactory;
import com.synopsys.integration.bdio.BdioPropertyHelper;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.BdioNode;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class DependencyGraphTransformer {
    private final BdioPropertyHelper bdioPropertyHelper;
    private final BdioNodeFactory bdioNodeFactory;

    public DependencyGraphTransformer(BdioPropertyHelper bdioPropertyHelper, BdioNodeFactory bdioNodeFactory) {
        this.bdioPropertyHelper = bdioPropertyHelper;
        this.bdioNodeFactory = bdioNodeFactory;
    }

    public List<BdioComponent> transformDependencyGraph(DependencyGraph graph, BdioNode currentNode, Set<Dependency> dependencies, Map<ExternalId, BdioNode> existingComponents) {
        List<BdioComponent> addedComponents = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            if (!existingComponents.containsKey(dependency.getExternalId())) {
                BdioComponent addedNode = componentFromDependency(dependency);
                addedComponents.add(addedNode);
                existingComponents.put(dependency.getExternalId(), addedNode);
                List<BdioComponent> addedChildren = transformDependencyGraph(graph, addedNode, graph.getChildrenForParent(dependency), existingComponents);
                addedComponents.addAll(addedChildren);
            }
            bdioPropertyHelper.addRelationship(currentNode, existingComponents.get(dependency.getExternalId()));
        }

        return addedComponents;
    }

    private BdioComponent componentFromDependency(Dependency dependency) {
        String componentName = dependency.getName();
        String componentVersion = dependency.getVersion();
        BdioId componentId = dependency.getExternalId().createBdioId();
        BdioExternalIdentifier componentExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(dependency.getExternalId());

        BdioComponent component = bdioNodeFactory.createComponent(componentName, componentVersion, componentId, componentExternalIdentifier);
        return component;
    }

}
