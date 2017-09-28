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
package com.blackducksoftware.integration.hub.bdio.graph.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.DependencyId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;

public class LazyExternalIdDependencyGraphBuilder {
    private class LazyDependencyInfo {
        public Set<DependencyId> children = new HashSet<>();

        public DependencyId aliasId;
        public ExternalId externalId;
        public String name;
        public String version;
    }

    private final Set<DependencyId> rootDependencyIds = new HashSet<>();
    private final Map<DependencyId, DependencyId> aliases = new HashMap<>();
    private final Map<DependencyId, LazyDependencyInfo> dependencyInfo = new HashMap<>();

    private LazyDependencyInfo infoForId(final DependencyId id) {
        LazyDependencyInfo info = dependencyInfo.get(id);
        if (info.aliasId != null) {
            info = dependencyInfo.get(info.aliasId);
        }
        return info;
    }

    public DependencyGraph build() {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (final DependencyId id : dependencyInfo.keySet()) {
            final LazyDependencyInfo info = infoForId(id);
            final Dependency dep = new Dependency(info.name, info.version, info.externalId);

            for (final DependencyId child : info.children) {
                final LazyDependencyInfo childInfo = infoForId(child);

                graph.addParentWithChild(dep, new Dependency(childInfo.name, childInfo.version, childInfo.externalId));
            }

            if (rootDependencyIds.contains(id) || rootDependencyIds.contains(info.aliasId)) {
                graph.addChildToRoot(dep);
            }
        }

        return graph;
    }

    private void ensureDependencyInfoExists(final DependencyId dependencyId) {
        if (!dependencyInfo.containsKey(dependencyId)) {
            dependencyInfo.put(dependencyId, new LazyDependencyInfo());
        }
    }

    public void setDependencyAsAlias(final DependencyId realDependencyId, final DependencyId fakeDependencyId) {
        ensureDependencyInfoExists(realDependencyId);
        ensureDependencyInfoExists(fakeDependencyId);
        final LazyDependencyInfo info = dependencyInfo.get(realDependencyId);
        info.aliasId = fakeDependencyId;
    }

    public void setDependencyInfo(final DependencyId id, final String name, final String version, final ExternalId externalId) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.name = name;
        info.version = version;
        info.externalId = externalId;
    }

    public void setDependencyName(final DependencyId id, final String name) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.name = name;
    }

    public void setDependencyVersion(final DependencyId id, final String version) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.version = version;
    }

    public void setDependencyExternalId(final DependencyId id, final ExternalId externalId) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.externalId = externalId;
    }

    public void addParentWithChild(final DependencyId parent, final DependencyId child) {
        ensureDependencyInfoExists(child);
        ensureDependencyInfoExists(parent);
        dependencyInfo.get(parent).children.add(child);

    }

    public void addParentWithChildren(final DependencyId parent, final List<DependencyId> children) {
        for (final DependencyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addParentWithChildren(final DependencyId parent, final Set<DependencyId> children) {
        for (final DependencyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addParentWithChildren(final DependencyId parent, final DependencyId... children) {
        for (final DependencyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addChildWithParent(final DependencyId child, final DependencyId parent) {
        ensureDependencyInfoExists(child);
        ensureDependencyInfoExists(parent);
        dependencyInfo.get(parent).children.add(child);
    }

    public void addChildWithParents(final DependencyId child, final List<DependencyId> parents) {
        for (final DependencyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildWithParents(final DependencyId child, final Set<DependencyId> parents) {
        for (final DependencyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildWithParents(final DependencyId child, final DependencyId... parents) {
        for (final DependencyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildToRoot(final DependencyId child) {
        ensureDependencyInfoExists(child);
        rootDependencyIds.add(child);
    }

    public void addChildrenToRoot(final List<DependencyId> children) {
        for (final DependencyId child : children) {
            addChildToRoot(child);
        }
    }

    public void addChildrenToRoot(final Set<DependencyId> children) {
        for (final DependencyId child : children) {
            addChildToRoot(child);
        }
    }

    public void addChildrenToRoot(final DependencyId... children) {
        for (final DependencyId child : children) {
            addChildToRoot(child);
        }
    }

}
