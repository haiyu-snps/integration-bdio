/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class LazyExternalIdDependencyGraphBuilder {
    public static class LazyDependencyInfo {
        private Set<LazyId> children = new HashSet<>();
        private LazyId aliasId;
        private ExternalId externalId;
        private String name;
        private String version;

        public Set<LazyId> getChildren() {
            return children;
        }

        // TODO: Why is this unused? Shouldn't the tests at least be calling this? JM -04/2022
        public void setChildren(Set<LazyId> children) {
            this.children = children;
        }

        public LazyId getAliasId() {
            return aliasId;
        }

        public void setAliasId(LazyId aliasId) {
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

    private final Set<LazyId> rootLazyIds = new HashSet<>();
    private final Map<LazyId, LazyDependencyInfo> dependencyInfo = new HashMap<>();

    private LazyDependencyInfo infoForId(LazyId id) {
        LazyDependencyInfo info = dependencyInfo.get(id);
        if (info.getAliasId() != null) {
            info = dependencyInfo.get(info.getAliasId());
        }
        return info;
    }

    public BasicDependencyGraph build() throws MissingExternalIdException {
        return build((lazyId, lazyDependencyInfo) -> {
            if (lazyDependencyInfo != null && lazyDependencyInfo.aliasId != null) {
                throw new MissingExternalIdException(lazyDependencyInfo.aliasId);
            } else {
                throw new MissingExternalIdException(lazyId);
            }
        });
    }

    public BasicDependencyGraph build(LazyBuilderMissingExternalIdHandler lazyBuilderHandler) throws MissingExternalIdException {
        BasicDependencyGraph mutableDependencyGraph = new BasicDependencyGraph();

        for (LazyId lazyId : dependencyInfo.keySet()) {
            LazyDependencyInfo lazyDependencyInfo = infoForId(lazyId);
            if (lazyDependencyInfo.getExternalId() == null) {
                ExternalId handledExternalId = lazyBuilderHandler.handleMissingExternalId(lazyId, lazyDependencyInfo);
                if (handledExternalId == null || lazyId == null) {
                    throw new MissingExternalIdException(lazyId);
                } else {
                    lazyDependencyInfo.setExternalId(handledExternalId);
                }
            }
        }

        for (LazyId lazyId : dependencyInfo.keySet()) {
            LazyDependencyInfo lazyDependencyInfo = infoForId(lazyId);
            Dependency dependency = new Dependency(lazyDependencyInfo.getName(), lazyDependencyInfo.getVersion(), lazyDependencyInfo.getExternalId());

            for (LazyId child : lazyDependencyInfo.getChildren()) {
                LazyDependencyInfo childInfo = infoForId(child);
                mutableDependencyGraph.addParentWithChild(dependency, new Dependency(childInfo.getName(), childInfo.getVersion(), childInfo.getExternalId()));
            }

            if (rootLazyIds.contains(lazyId) || rootLazyIds.contains(lazyDependencyInfo.getAliasId())) {
                mutableDependencyGraph.addDirectDependency(dependency);
            }
        }

        return mutableDependencyGraph;
    }

    private void ensureDependencyInfoExists(LazyId lazyId) {
        dependencyInfo.computeIfAbsent(lazyId, key -> new LazyDependencyInfo());
    }

    public void setDependencyAsAlias(LazyId realLazyId, LazyId fakeLazyId) {
        ensureDependencyInfoExists(realLazyId);
        ensureDependencyInfoExists(fakeLazyId);
        LazyDependencyInfo info = dependencyInfo.get(fakeLazyId);
        info.setAliasId(realLazyId);
    }

    public void setDependencyInfo(LazyId id, String name, String version, ExternalId externalId) {
        ensureDependencyInfoExists(id);
        LazyDependencyInfo info = dependencyInfo.get(id);
        info.setName(name);
        info.setVersion(version);
        info.setExternalId(externalId);
    }

    public void setDependencyName(LazyId id, String name) {
        ensureDependencyInfoExists(id);
        LazyDependencyInfo info = dependencyInfo.get(id);
        info.setName(name);
    }

    public void setDependencyVersion(LazyId id, String version) {
        ensureDependencyInfoExists(id);
        LazyDependencyInfo info = dependencyInfo.get(id);
        info.setVersion(version);
    }

    public void setDependencyExternalId(LazyId id, ExternalId externalId) {
        ensureDependencyInfoExists(id);
        LazyDependencyInfo info = dependencyInfo.get(id);
        info.setExternalId(externalId);
    }

    public void addParentWithChild(LazyId parent, LazyId child) {
        ensureDependencyInfoExists(child);
        ensureDependencyInfoExists(parent);
        dependencyInfo.get(parent).getChildren().add(child);

    }

    public void addParentWithChildren(LazyId parent, List<LazyId> children) {
        for (LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addParentWithChildren(LazyId parent, Set<LazyId> children) {
        for (LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addParentWithChildren(LazyId parent, LazyId... children) {
        for (LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addChildWithParent(LazyId child, LazyId parent) {
        addParentWithChild(parent, child);
    }

    public void addChildWithParents(LazyId child, List<LazyId> parents) {
        for (LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildWithParents(LazyId child, Set<LazyId> parents) {
        for (LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildWithParents(LazyId child, LazyId... parents) {
        for (LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildToRoot(LazyId child) {
        ensureDependencyInfoExists(child);
        rootLazyIds.add(child);
    }

    public void addChildrenToRoot(List<LazyId> children) {
        for (LazyId child : children) {
            addChildToRoot(child);
        }
    }

    public void addChildrenToRoot(Set<LazyId> children) {
        for (LazyId child : children) {
            addChildToRoot(child);
        }
    }

    public void addChildrenToRoot(LazyId... children) {
        for (LazyId child : children) {
            addChildToRoot(child);
        }
    }

}
