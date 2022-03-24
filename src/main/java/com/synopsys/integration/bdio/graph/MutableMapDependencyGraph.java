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
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class MutableMapDependencyGraph implements MutableDependencyGraph {
    @Nullable
    private final ProjectDependency rootDependency;
    private final Set<ExternalId> rootDependencies = new HashSet<>();
    private final Map<ExternalId, Dependency> dependencies = new HashMap<>();
    private final Map<ExternalId, Set<ExternalId>> relationships = new HashMap<>();
    private final DependencyGraphCombiner dependencyGraphCombiner = new DependencyGraphCombiner();

    public MutableMapDependencyGraph() {
        this(null);
    }

    public MutableMapDependencyGraph(@Nullable ProjectDependency rootDependency) {
        this.rootDependency = rootDependency;
    }

    @Override
    public Optional<ProjectDependency> getRootDependency() {
        return Optional.ofNullable(rootDependency);
    }

    @Override
    public void addGraphAsChildrenToRoot(DependencyGraph sourceGraph) {
        dependencyGraphCombiner.addGraphAsChildrenToRoot(this, sourceGraph);
    }

    @Override
    public void addGraphAsChildrenToParent(Dependency parent, DependencyGraph sourceGraph) {
        if (isRoot(parent)) {
            addGraphAsChildrenToRoot(sourceGraph);
        } else {
            dependencyGraphCombiner.addGraphAsChildrenToParent(this, parent, sourceGraph);
        }
    }

    @Override
    public boolean hasDependency(ExternalId dependency) {
        return dependencies.containsKey(dependency);
    }

    @Override
    public boolean hasDependency(Dependency dependency) {
        return dependencies.containsKey(dependency.getExternalId());
    }

    @Override
    public Dependency getDependency(ExternalId dependency) {
        if (dependencies.containsKey(dependency)) {
            return dependencies.get(dependency);
        }
        return null;
    }

    @Override
    public Set<Dependency> getChildrenForParent(ExternalId parent) {
        Set<ExternalId> childIds = getChildrenExternalIdsForParent(parent);
        return dependenciesFromExternalIds(childIds);
    }

    @Override
    public Set<Dependency> getParentsForChild(ExternalId child) {
        Set<ExternalId> parentIds = getParentExternalIdsForChild(child);
        return dependenciesFromExternalIds(parentIds);
    }

    @Override
    public Set<ExternalId> getChildrenExternalIdsForParent(ExternalId parent) {
        Set<ExternalId> children = new HashSet<>();
        if (relationships.containsKey(parent)) {
            children.addAll(relationships.get(parent));
        }
        return children;
    }

    @Override
    public Set<ExternalId> getParentExternalIdsForChild(ExternalId child) {
        Set<ExternalId> parents = new HashSet<>();
        for (Map.Entry<ExternalId, Set<ExternalId>> externalIdSetEntry : relationships.entrySet()) {
            ExternalId parentId = externalIdSetEntry.getKey();
            for (ExternalId childId : externalIdSetEntry.getValue()) {
                if (childId.equals(child)) {
                    parents.add(parentId);
                }
            }
        }
        return parents;
    }

    @Override
    public Set<Dependency> getChildrenForParent(Dependency parent) {
        return getChildrenForParent(parent.getExternalId());
    }

    @Override
    public Set<Dependency> getParentsForChild(Dependency child) {
        return getParentsForChild(child.getExternalId());
    }

    @Override
    public Set<ExternalId> getChildrenExternalIdsForParent(Dependency parent) {
        return getChildrenExternalIdsForParent(parent.getExternalId());
    }

    @Override
    public Set<ExternalId> getParentExternalIdsForChild(Dependency child) {
        return getParentExternalIdsForChild(child.getExternalId());
    }

    @Override
    public void addParentWithChild(Dependency parent, Dependency child) {
        ensureDependencyAndRelationshipExists(parent);
        ensureDependencyExists(child);
        addRelationship(parent, child);
    }

    @Override
    public void addChildWithParent(Dependency child, Dependency parent) {
        addParentWithChild(parent, child);
    }

    @Override
    public void addParentWithChildren(Dependency parent, List<Dependency> children) {
        ensureDependencyAndRelationshipExists(parent);
        for (Dependency child : children) {
            ensureDependencyExists(child);
            addRelationship(parent, child);
        }
    }

    @Override
    public void addChildWithParents(Dependency child, List<Dependency> parents) {
        ensureDependencyExists(child);
        for (Dependency parent : parents) {
            ensureDependencyAndRelationshipExists(parent);
            addRelationship(parent, child);
        }

    }

    @Override
    public void addParentWithChildren(Dependency parent, Set<Dependency> children) {
        ensureDependencyAndRelationshipExists(parent);
        for (Dependency child : children) {
            ensureDependencyExists(child);
            addRelationship(parent, child);
        }
    }

    @Override
    public void addChildWithParents(Dependency child, Set<Dependency> parents) {
        ensureDependencyExists(child);
        for (Dependency parent : parents) {
            ensureDependencyAndRelationshipExists(parent);
            addRelationship(parent, child);
        }
    }

    @Override
    public void addParentWithChildren(Dependency parent, Dependency... children) {
        addParentWithChildren(parent, Arrays.asList(children));
    }

    @Override
    public void addChildWithParents(Dependency child, Dependency... parents) {
        addChildWithParents(child, Arrays.asList(parents));
    }

    @Override
    public Set<ExternalId> getRootDependencyExternalIds() {
        HashSet<ExternalId> copy = new HashSet<>();
        copy.addAll(rootDependencies);
        return copy;
    }

    @Override
    public Set<Dependency> getRootDependencies() {
        return dependenciesFromExternalIds(getRootDependencyExternalIds());
    }

    @Override
    public void addChildToRoot(Dependency child) {
        ensureDependencyExists(child);
        rootDependencies.add(child.getExternalId());
    }

    @Override
    public void addChildrenToRoot(List<Dependency> children) {
        for (Dependency child : children) {
            addChildToRoot(child);
        }
    }

    @Override
    public void addChildrenToRoot(Set<Dependency> children) {
        for (Dependency child : children) {
            addChildToRoot(child);
        }
    }

    @Override
    public void addChildrenToRoot(Dependency... children) {
        for (Dependency child : children) {
            addChildToRoot(child);
        }
    }

    private void ensureDependencyExists(Dependency dependency) {
        if (!isRoot(dependency) && !dependencies.containsKey(dependency.getExternalId())) {
            dependencies.put(dependency.getExternalId(), dependency);
        }
    }

    private void ensureDependencyAndRelationshipExists(Dependency dependency) {
        ensureDependencyExists(dependency);
        if (!isRoot(dependency) && !relationships.containsKey(dependency.getExternalId())) {
            relationships.put(dependency.getExternalId(), new HashSet<>());
        }
    }

    private void addRelationship(Dependency parent, Dependency child) {
        if (isRoot(parent)) {
            addChildToRoot(child);
        } else {
            relationships.get(parent.getExternalId()).add(child.getExternalId());
        }
    }

    private Set<Dependency> dependenciesFromExternalIds(Set<ExternalId> ids) {
        Set<Dependency> foundDependencies = new HashSet<>();
        for (ExternalId id : ids) {
            if (dependencies.containsKey(id)) {
                foundDependencies.add(dependencies.get(id));
            }
        }
        return foundDependencies;
    }

    private boolean isRoot(Dependency dependency) {
        return null != rootDependency && rootDependency.equals(dependency);
    }

}
