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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.blackducksoftware.integration.hub.bdio.simple.model.Dependency;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;

public class NodeDependencyGraph implements MutableDependencyGraph {

    private class GraphNode {
        public Dependency dependency;

        public final Set<ExternalId> relationships = new TreeSet<>();
    }

    final Set<ExternalId> rootDependencies = new HashSet<>();
    final Map<ExternalId, GraphNode> nodes = new HashMap<>();

    public NodeDependencyGraph() {
    }

    private GraphNode getOrCreateNode(final Dependency dependency) {
        ensureNodeExists(dependency);
        return nodes.get(dependency.externalId);
    }

    private void ensureNodeExists(final Dependency dependency) {
        if (!nodes.containsKey(dependency.externalId)) {
            final GraphNode node = new GraphNode();
            node.dependency = dependency;
            nodes.put(dependency.externalId, node);
        }
    }

    private void addRelationship(final GraphNode parentNode, final Dependency child) {
        parentNode.relationships.add(child.externalId);
    }

    private Set<Dependency> dependenciesFromExternalIds(final Set<ExternalId> ids) {
        final Set<Dependency> foundDependencies = new HashSet<>();
        for (final ExternalId id : ids) {
            if (nodes.containsKey(id)) {
                foundDependencies.add(nodes.get(id).dependency);
            }
        }
        return foundDependencies;
    }

    @Override
    public Set<Dependency> getChildrenForParent(final Dependency parent) {
        return getChildrenForParent(parent.externalId);
    }

    @Override
    public Set<Dependency> getParentsForChild(final Dependency child) {
        return getParentsForChild(child.externalId);
    }

    @Override
    public Set<ExternalId> getChildrenExternalIdsForParent(final Dependency parent) {
        return getChildrenExternalIdsForParent(parent.externalId);
    }

    @Override
    public Set<ExternalId> getParentExternalIdsForChild(final Dependency child) {
        return getParentExternalIdsForChild(child.externalId);
    }

    @Override
    public Set<Dependency> getChildrenForParent(final ExternalId parent) {
        final Set<ExternalId> childIds = getChildrenExternalIdsForParent(parent);
        return dependenciesFromExternalIds(childIds);
    }

    @Override
    public Set<Dependency> getParentsForChild(final ExternalId child) {
        final Set<ExternalId> ParentExternalIds = getParentExternalIdsForChild(child);
        return dependenciesFromExternalIds(ParentExternalIds);
    }

    @Override
    public Set<ExternalId> getChildrenExternalIdsForParent(final ExternalId parent) {
        final Set<ExternalId> children = new HashSet<>();
        if (nodes.containsKey(parent)) {
            for (final ExternalId id : nodes.get(parent).relationships) {
                children.add(id);
            }
        }
        return children;
    }

    @Override
    public Set<ExternalId> getParentExternalIdsForChild(final ExternalId child) {
        final Set<ExternalId> parents = new HashSet<>();
        for (final ExternalId parentId : nodes.keySet()) {
            for (final ExternalId childId : nodes.get(parentId).relationships) {
                if (childId.equals(child)) {
                    parents.add(parentId);
                }
            }
        }
        return parents;
    }

    @Override
    public void addParentWithChild(final Dependency parent, final Dependency child) {
        ensureNodeExists(child);
        final GraphNode parentNode = getOrCreateNode(parent);
        addRelationship(parentNode, child);
    }

    @Override
    public void addChildWithParent(final Dependency child, final Dependency parent) {
        addParentWithChild(child, parent);
    }

    @Override
    public void addParentWithChildren(final Dependency parent, final List<Dependency> children) {
        final GraphNode parentNode = getOrCreateNode(parent);
        for (final Dependency child : children) {
            ensureNodeExists(child);
            addRelationship(parentNode, child);
        }
    }

    @Override
    public void addChildWithParents(final Dependency child, final List<Dependency> parents) {
        ensureNodeExists(child);
        for (final Dependency parent : parents) {
            final GraphNode parentNode = getOrCreateNode(parent);
            addRelationship(parentNode, child);
        }

    }

    @Override
    public void addParentWithChildren(final Dependency parent, final Set<Dependency> children) {
        final GraphNode parentNode = getOrCreateNode(parent);
        for (final Dependency child : children) {
            ensureNodeExists(child);
            addRelationship(parentNode, child);
        }
    }

    @Override
    public void addChildWithParents(final Dependency child, final Set<Dependency> parents) {
        ensureNodeExists(child);
        for (final Dependency parent : parents) {
            final GraphNode parentNode = getOrCreateNode(parent);
            addRelationship(parentNode, child);
        }
    }

    @Override
    public void addParentWithChildren(final Dependency parent, final Dependency... children) {
        addParentWithChildren(parent, Arrays.asList(children));
    }

    @Override
    public void addChildWithParents(final Dependency child, final Dependency... parents) {
        addChildWithParents(child, Arrays.asList(parents));
    }

    @Override
    public boolean hasDependency(final Dependency dependency) {
        return hasDependency(dependency.externalId);
    }

    @Override
    public boolean hasDependency(final ExternalId dependency) {
        return nodes.containsKey(dependency);
    }

    @Override
    public Dependency getDependency(final ExternalId dependency) {
        if (nodes.containsKey(dependency)) {
            return nodes.get(dependency).dependency;
        }
        return null;
    }

    @Override
    public Set<Dependency> getRootDependencies() {
        return dependenciesFromExternalIds(getRootDependencyExternalIds());
    }

    @Override
    public Set<ExternalId> getRootDependencyExternalIds() {
        final HashSet<ExternalId> copy = new HashSet<>();
        copy.addAll(rootDependencies);
        return copy;
    }

    @Override
    public void addChildToRoot(final Dependency child) {
        ensureNodeExists(child);
        rootDependencies.add(child.externalId);
    }

    @Override
    public void addChildrenToRoot(final List<Dependency> children) {
        for (final Dependency child : children) {
            addChildToRoot(child);
        }
    }

    @Override
    public void addChildrenToRoot(final Set<Dependency> children) {
        for (final Dependency child : children) {
            addChildToRoot(child);
        }
    }

    @Override
    public void addChildrenToRoot(final Dependency... children) {
        for (final Dependency child : children) {
            addChildToRoot(child);
        }
    }
}
