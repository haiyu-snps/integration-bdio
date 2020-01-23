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
package com.synopsys.integration.bdio.graph.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class LazyExternalIdDependencyGraphBuilder {
    static class LazyDependencyInfo {
        private Set<DependencyId> children = new HashSet<>();
        private DependencyId aliasId;
        private ExternalId externalId;
        private String name;
        private String version;

        public Set<DependencyId> getChildren() {
            return children;
        }

        public void setChildren(Set<DependencyId> children) {
            this.children = children;
        }

        public DependencyId getAliasId() {
            return aliasId;
        }

        public void setAliasId(DependencyId aliasId) {
            this.aliasId = aliasId;
        }

        public ExternalId getExternalId() {
            return externalId;
        }

        public void setExternalId(ExternalId externalId) {
            this.externalId = externalId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    private final Set<DependencyId> rootDependencyIds = new HashSet<>();
    private final Map<DependencyId, LazyDependencyInfo> dependencyInfo = new HashMap<>();

    private LazyDependencyInfo infoForId(final DependencyId id) {
        LazyDependencyInfo info = dependencyInfo.get(id);
        if (info.getAliasId() != null) {
            info = dependencyInfo.get(info.getAliasId());
        }
        return info;
    }

    public DependencyGraph build() throws MissingExternalIdException {
        return build(lazyDependencyInfo -> {
            if (lazyDependencyInfo != null && lazyDependencyInfo.aliasId != null) {
                throw new MissingExternalIdException(lazyDependencyInfo.aliasId);
            } else {
                throw new MissingExternalIdException(null);
            }
        });
    }

    public DependencyGraph build(LazyBuilderMissingExternalIdHandler lazyBuilderHandler) throws MissingExternalIdException {
        final MutableDependencyGraph mutableDependencyGraph = new MutableMapDependencyGraph();

        for (final DependencyId dependencyId : dependencyInfo.keySet()) {
            final LazyDependencyInfo lazyDependencyInfo = infoForId(dependencyId);
            if (lazyDependencyInfo.getExternalId() == null) {
                final ExternalId handledExternalId = lazyBuilderHandler.handleMissingExternalId(lazyDependencyInfo);
                if (handledExternalId == null || dependencyId == null) {
                    throw new MissingExternalIdException(dependencyId);
                } else {
                    lazyDependencyInfo.setExternalId(handledExternalId);
                }
            }
        }

        for (final DependencyId dependencyId : dependencyInfo.keySet()) {
            final LazyDependencyInfo lazyDependencyInfo = infoForId(dependencyId);
            final Dependency dependency = new Dependency(lazyDependencyInfo.getName(), lazyDependencyInfo.getVersion(), lazyDependencyInfo.getExternalId());

            for (final DependencyId child : lazyDependencyInfo.getChildren()) {
                final LazyDependencyInfo childInfo = infoForId(child);
                mutableDependencyGraph.addParentWithChild(dependency, new Dependency(childInfo.getName(), childInfo.getVersion(), childInfo.getExternalId()));
            }

            if (rootDependencyIds.contains(dependencyId) || rootDependencyIds.contains(lazyDependencyInfo.getAliasId())) {
                mutableDependencyGraph.addChildToRoot(dependency);
            }
        }

        return mutableDependencyGraph;
    }

    private void ensureDependencyInfoExists(final DependencyId dependencyId) {
        if (!dependencyInfo.containsKey(dependencyId)) {
            dependencyInfo.put(dependencyId, new LazyDependencyInfo());
        }
    }

    public void setDependencyAsAlias(final DependencyId realDependencyId, final DependencyId fakeDependencyId) {
        ensureDependencyInfoExists(realDependencyId);
        ensureDependencyInfoExists(fakeDependencyId);
        final LazyDependencyInfo info = dependencyInfo.get(fakeDependencyId);
        info.setAliasId(realDependencyId);
    }

    public void setDependencyInfo(final DependencyId id, final String name, final String version, final ExternalId externalId) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.setName(name);
        info.setVersion(version);
        info.setExternalId(externalId);
    }

    public void setDependencyName(final DependencyId id, final String name) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.setName(name);
    }

    public void setDependencyVersion(final DependencyId id, final String version) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.setVersion(version);
    }

    public void setDependencyExternalId(final DependencyId id, final ExternalId externalId) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.setExternalId(externalId);
    }

    public void addParentWithChild(final DependencyId parent, final DependencyId child) {
        ensureDependencyInfoExists(child);
        ensureDependencyInfoExists(parent);
        dependencyInfo.get(parent).getChildren().add(child);

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
        addParentWithChild(parent, child);
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
