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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.simple.model.Dependency;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;

public class MapDependencyGraph implements DependencyGraph {

    Set<ExternalId> rootDependencies = new HashSet<>();
    Map<ExternalId, Dependency> dependencies = new HashMap<>();
    Map<ExternalId, Set<ExternalId>> relationships = new HashMap<>();

    public MapDependencyGraph(final Set<ExternalId> rootDependencies, final Map<ExternalId, Dependency> dependencies, final Map<ExternalId, Set<ExternalId>> relationships) {
        this.rootDependencies = rootDependencies;
        this.dependencies = dependencies;
        this.relationships = relationships;
    }

    private Set<Dependency> dependenciesFromExternalIds(final Set<ExternalId> ids) {
        final Set<Dependency> foundDependencies = new HashSet<>();
        for (final ExternalId id : ids) {
            if (dependencies.containsKey(id)) {
                foundDependencies.add(dependencies.get(id));
            }
        }
        return foundDependencies;
    }

    @Override
    public boolean hasDependency(final ExternalId id) {
        return dependencies.containsKey(id);
    }

    @Override
    public boolean hasDependency(final Dependency dependency) {
        return dependencies.containsKey(dependency.externalId);
    }

    @Override
    public Dependency getDependency(final ExternalId id) {
        if (dependencies.containsKey(id)) {
            return dependencies.get(id);
        }
        return null;
    }

    @Override
    public Set<Dependency> getChildrenForParent(final ExternalId parent) {
        final Set<ExternalId> childIds = getChildrenExternalIdsForParent(parent);
        return dependenciesFromExternalIds(childIds);
    }

    @Override
    public Set<Dependency> getParentsForChild(final ExternalId child) {
        final Set<ExternalId> parentIds = getParentExternalIdsForChild(child);
        return dependenciesFromExternalIds(parentIds);
    }

    @Override
    public Set<ExternalId> getChildrenExternalIdsForParent(final ExternalId parent) {
        final Set<ExternalId> children = new HashSet<>();
        if (relationships.containsKey(parent)) {
            for (final ExternalId id : relationships.get(parent)) {
                children.add(id);
            }
        }
        return children;
    }

    @Override
    public Set<ExternalId> getParentExternalIdsForChild(final ExternalId child) {
        final Set<ExternalId> parents = new HashSet<>();
        for (final ExternalId parentId : relationships.keySet()) {
            for (final ExternalId childId : relationships.get(parentId)) {
                if (childId.equals(child)) {
                    parents.add(parentId);
                }
            }
        }
        return parents;
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
    public Set<ExternalId> getRootDependencyExternalIds() {
        final HashSet<ExternalId> copy = new HashSet<>();
        copy.addAll(rootDependencies);
        return copy;
    }

    @Override
    public Set<Dependency> getRootDependencies() {
        return dependenciesFromExternalIds(getRootDependencyExternalIds());
    }

}
