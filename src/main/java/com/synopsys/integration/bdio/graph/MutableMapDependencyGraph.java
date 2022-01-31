/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class MutableMapDependencyGraph implements MutableDependencyGraph {
    private final Dependency rootDependency;
    private final Set<ExternalId> rootDependencies = new HashSet<>();
    private final Map<ExternalId, Dependency> dependencies = new HashMap<>();
    private final Map<ExternalId, Set<ExternalId>> relationships = new HashMap<>();
    private final DependencyGraphCombiner dependencyGraphCombiner = new DependencyGraphCombiner();

    public MutableMapDependencyGraph() {
        rootDependency = null;
    }

    public MutableMapDependencyGraph(Dependency rootDependency) {
        this.rootDependency = rootDependency;
    }

    @Override
    public void addGraphAsChildrenToRoot(final DependencyGraph sourceGraph) {
        dependencyGraphCombiner.addGraphAsChildrenToRoot(this, sourceGraph);
    }

    @Override
    public void addGraphAsChildrenToParent(final Dependency parent, final DependencyGraph sourceGraph) {
        if (isRoot(parent)) {
            addGraphAsChildrenToRoot(sourceGraph);
        } else {
            dependencyGraphCombiner.addGraphAsChildrenToParent(this, parent, sourceGraph);
        }
    }

    @Override
    public boolean hasDependency(final ExternalId dependency) {
        return dependencies.containsKey(dependency);
    }

    @Override
    public boolean hasDependency(final Dependency dependency) {
        return dependencies.containsKey(dependency.getExternalId());
    }

    @Override
    public Dependency getDependency(final ExternalId dependency) {
        if (dependencies.containsKey(dependency)) {
            return dependencies.get(dependency);
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
            children.addAll(relationships.get(parent));
        }
        return children;
    }

    @Override
    public Set<ExternalId> getParentExternalIdsForChild(final ExternalId child) {
        final Set<ExternalId> parents = new HashSet<>();
        for (final Map.Entry<ExternalId, Set<ExternalId>> externalIdSetEntry : relationships.entrySet()) {
            final ExternalId parentId = externalIdSetEntry.getKey();
            for (final ExternalId childId : externalIdSetEntry.getValue()) {
                if (childId.equals(child)) {
                    parents.add(parentId);
                }
            }
        }
        return parents;
    }

    @Override
    public Set<Dependency> getChildrenForParent(final Dependency parent) {
        return getChildrenForParent(parent.getExternalId());
    }

    @Override
    public Set<Dependency> getParentsForChild(final Dependency child) {
        return getParentsForChild(child.getExternalId());
    }

    @Override
    public Set<ExternalId> getChildrenExternalIdsForParent(final Dependency parent) {
        return getChildrenExternalIdsForParent(parent.getExternalId());
    }

    @Override
    public Set<ExternalId> getParentExternalIdsForChild(final Dependency child) {
        return getParentExternalIdsForChild(child.getExternalId());
    }

    @Override
    public void addParentWithChild(final Dependency parent, final Dependency child) {
        ensureDependencyAndRelationshipExists(parent);
        ensureDependencyExists(child);
        addRelationship(parent, child);
    }

    @Override
    public void addChildWithParent(final Dependency child, final Dependency parent) {
        addParentWithChild(parent, child);
    }

    @Override
    public void addParentWithChildren(final Dependency parent, final List<Dependency> children) {
        ensureDependencyAndRelationshipExists(parent);
        for (final Dependency child : children) {
            ensureDependencyExists(child);
            addRelationship(parent, child);
        }
    }

    @Override
    public void addChildWithParents(final Dependency child, final List<Dependency> parents) {
        ensureDependencyExists(child);
        for (final Dependency parent : parents) {
            ensureDependencyAndRelationshipExists(parent);
            addRelationship(parent, child);
        }

    }

    @Override
    public void addParentWithChildren(final Dependency parent, final Set<Dependency> children) {
        ensureDependencyAndRelationshipExists(parent);
        for (final Dependency child : children) {
            ensureDependencyExists(child);
            addRelationship(parent, child);
        }
    }

    @Override
    public void addChildWithParents(final Dependency child, final Set<Dependency> parents) {
        ensureDependencyExists(child);
        for (final Dependency parent : parents) {
            ensureDependencyAndRelationshipExists(parent);
            addRelationship(parent, child);
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
    public Set<ExternalId> getRootDependencyExternalIds() {
        final HashSet<ExternalId> copy = new HashSet<>();
        copy.addAll(rootDependencies);
        return copy;
    }

    @Override
    public Set<Dependency> getRootDependencies() {
        return dependenciesFromExternalIds(getRootDependencyExternalIds());
    }

    @Override
    public void addChildToRoot(final Dependency child) {
        ensureDependencyExists(child);
        rootDependencies.add(child.getExternalId());
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

    private void ensureDependencyExists(final Dependency dependency) {
        if (!isRoot(dependency) && !dependencies.containsKey(dependency.getExternalId())) {
            dependencies.put(dependency.getExternalId(), dependency);
        }
    }

    private void ensureDependencyAndRelationshipExists(final Dependency dependency) {
        ensureDependencyExists(dependency);
        if (!isRoot(dependency) && !relationships.containsKey(dependency.getExternalId())) {
            relationships.put(dependency.getExternalId(), new HashSet<>());
        }
    }

    private void addRelationship(final Dependency parent, final Dependency child) {
        if (isRoot(parent)) {
            addChildToRoot(child);
        } else {
            relationships.get(parent.getExternalId()).add(child.getExternalId());
        }
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

    private boolean isRoot(final Dependency dependency) {
        return null != rootDependency && rootDependency.equals(dependency);
    }

}
