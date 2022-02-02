/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph.builder;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

import java.util.*;

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

    private LazyDependencyInfo infoForId(final LazyId id) {
        LazyDependencyInfo info = dependencyInfo.get(id);
        if (info.getAliasId() != null) {
            info = dependencyInfo.get(info.getAliasId());
        }
        return info;
    }

    public DependencyGraph build() throws MissingExternalIdException {
        return build((lazyId, lazyDependencyInfo) -> {
            if (lazyDependencyInfo != null && lazyDependencyInfo.aliasId != null) {
                throw new MissingExternalIdException(lazyDependencyInfo.aliasId);
            } else {
                throw new MissingExternalIdException(lazyId);
            }
        });
    }

    public DependencyGraph build(LazyBuilderMissingExternalIdHandler lazyBuilderHandler) throws MissingExternalIdException {
        final MutableDependencyGraph mutableDependencyGraph = new MutableMapDependencyGraph();

        for (final LazyId lazyId : dependencyInfo.keySet()) {
            final LazyDependencyInfo lazyDependencyInfo = infoForId(lazyId);
            if (lazyDependencyInfo.getExternalId() == null) {
                final ExternalId handledExternalId = lazyBuilderHandler.handleMissingExternalId(lazyId, lazyDependencyInfo);
                if (handledExternalId == null || lazyId == null) {
                    throw new MissingExternalIdException(lazyId);
                } else {
                    lazyDependencyInfo.setExternalId(handledExternalId);
                }
            }
        }

        for (final LazyId lazyId : dependencyInfo.keySet()) {
            final LazyDependencyInfo lazyDependencyInfo = infoForId(lazyId);
            final Dependency dependency = new Dependency(lazyDependencyInfo.getName(), lazyDependencyInfo.getVersion(), lazyDependencyInfo.getExternalId());

            for (final LazyId child : lazyDependencyInfo.getChildren()) {
                final LazyDependencyInfo childInfo = infoForId(child);
                mutableDependencyGraph.addParentWithChild(dependency, new Dependency(childInfo.getName(), childInfo.getVersion(), childInfo.getExternalId()));
            }

            if (rootLazyIds.contains(lazyId) || rootLazyIds.contains(lazyDependencyInfo.getAliasId())) {
                mutableDependencyGraph.addChildToRoot(dependency);
            }
        }

        return mutableDependencyGraph;
    }

    private void ensureDependencyInfoExists(final LazyId lazyId) {
        if (!dependencyInfo.containsKey(lazyId)) {
            dependencyInfo.put(lazyId, new LazyDependencyInfo());
        }
    }

    public void setDependencyAsAlias(final LazyId realLazyId, final LazyId fakeLazyId) {
        ensureDependencyInfoExists(realLazyId);
        ensureDependencyInfoExists(fakeLazyId);
        final LazyDependencyInfo info = dependencyInfo.get(fakeLazyId);
        info.setAliasId(realLazyId);
    }

    public void setDependencyInfo(final LazyId id, final String name, final String version, final ExternalId externalId) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.setName(name);
        info.setVersion(version);
        info.setExternalId(externalId);
    }

    public void setDependencyName(final LazyId id, final String name) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.setName(name);
    }

    public void setDependencyVersion(final LazyId id, final String version) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.setVersion(version);
    }

    public void setDependencyExternalId(final LazyId id, final ExternalId externalId) {
        ensureDependencyInfoExists(id);
        final LazyDependencyInfo info = dependencyInfo.get(id);
        info.setExternalId(externalId);
    }

    public void addParentWithChild(final LazyId parent, final LazyId child) {
        ensureDependencyInfoExists(child);
        ensureDependencyInfoExists(parent);
        dependencyInfo.get(parent).getChildren().add(child);

    }

    public void addParentWithChildren(final LazyId parent, final List<LazyId> children) {
        for (final LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addParentWithChildren(final LazyId parent, final Set<LazyId> children) {
        for (final LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addParentWithChildren(final LazyId parent, final LazyId... children) {
        for (final LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    public void addChildWithParent(final LazyId child, final LazyId parent) {
        addParentWithChild(parent, child);
    }

    public void addChildWithParents(final LazyId child, final List<LazyId> parents) {
        for (final LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildWithParents(final LazyId child, final Set<LazyId> parents) {
        for (final LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildWithParents(final LazyId child, final LazyId... parents) {
        for (final LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    public void addChildToRoot(final LazyId child) {
        ensureDependencyInfoExists(child);
        rootLazyIds.add(child);
    }

    public void addChildrenToRoot(final List<LazyId> children) {
        for (final LazyId child : children) {
            addChildToRoot(child);
        }
    }

    public void addChildrenToRoot(final Set<LazyId> children) {
        for (final LazyId child : children) {
            addChildToRoot(child);
        }
    }

    public void addChildrenToRoot(final LazyId... children) {
        for (final LazyId child : children) {
            addChildToRoot(child);
        }
    }

}
