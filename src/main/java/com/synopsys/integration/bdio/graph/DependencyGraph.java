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
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public abstract class DependencyGraph {
    protected final Map<ExternalId, Dependency> dependencies = new HashMap<>();
    protected final Map<ExternalId, Set<ExternalId>> relationships = new HashMap<>();

    public abstract void addChildToRoot(Dependency child);

    public abstract Set<Dependency> getRootDependencies();

    public boolean hasDependency(ExternalId dependency) {
        return dependencies.containsKey(dependency);
    }

    public boolean hasDependency(Dependency dependency) {
        return dependencies.containsKey(dependency.getExternalId());
    }

    @Nullable
    public Dependency getDependency(ExternalId dependency) {
        return dependencies.getOrDefault(dependency, null);
    }

    public Set<Dependency> getChildrenForParent(ExternalId parent) {
        return getChildrenExternalIdsForParent(parent).stream()
            .map(dependencies::get)
            .collect(Collectors.toSet());
    }

    public Set<Dependency> getParentsForChild(ExternalId child) {
        return getParentExternalIdsForChild(child).stream()
            .map(dependencies::get)
            .collect(Collectors.toSet());
    }

    public Set<ExternalId> getChildrenExternalIdsForParent(ExternalId parent) {
        return relationships.getOrDefault(parent, new HashSet<>());
    }

    public Set<Dependency> getChildrenForParent(Dependency parent) {
        return getChildrenForParent(parent.getExternalId());
    }

    public Set<Dependency> getParentsForChild(Dependency child) {
        return getParentsForChild(child.getExternalId());
    }

    public Set<ExternalId> getChildrenExternalIdsForParent(Dependency parent) {
        return getChildrenExternalIdsForParent(parent.getExternalId());
    }

    public Set<ExternalId> getParentExternalIdsForChild(Dependency child) {
        return getParentExternalIdsForChild(child.getExternalId());
    }

    public Set<ExternalId> getParentExternalIdsForChild(ExternalId child) {
        return relationships.entrySet().stream()
            .filter(externalIdSetEntry ->
                externalIdSetEntry.getValue().stream()
                    .anyMatch(child::equals)
            )
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    public void addParentWithChild(Dependency parent, Dependency child) {
        ensureDependencyAndRelationshipExists(parent);
        ensureDependencyExists(child);
        addRelationship(parent, child);
    }

    public void addChildWithParent(Dependency child, Dependency parent) {
        addParentWithChild(parent, child);
    }

    public void addParentWithChildren(Dependency parent, List<Dependency> children) {
        ensureDependencyAndRelationshipExists(parent);
        for (Dependency child : children) {
            ensureDependencyExists(child);
            addRelationship(parent, child);
        }
    }

    public void addChildWithParents(Dependency child, List<Dependency> parents) {
        ensureDependencyExists(child);
        for (Dependency parent : parents) {
            ensureDependencyAndRelationshipExists(parent);
            addRelationship(parent, child);
        }

    }

    public void addParentWithChildren(Dependency parent, Set<Dependency> children) {
        ensureDependencyAndRelationshipExists(parent);
        for (Dependency child : children) {
            ensureDependencyExists(child);
            addRelationship(parent, child);
        }
    }

    public void addChildWithParents(Dependency child, Set<Dependency> parents) {
        ensureDependencyExists(child);
        for (Dependency parent : parents) {
            ensureDependencyAndRelationshipExists(parent);
            addRelationship(parent, child);
        }
    }

    public void addParentWithChildren(Dependency parent, Dependency... children) {
        addParentWithChildren(parent, Arrays.asList(children));
    }

    public void addChildWithParents(Dependency child, Dependency... parents) {
        addChildWithParents(child, Arrays.asList(parents));
    }

    public void addChildrenToRoot(List<Dependency> children) {
        children.forEach(this::addChildToRoot);
    }

    public void addChildrenToRoot(Set<Dependency> children) {
        children.forEach(this::addChildToRoot);
    }

    public void addChildrenToRoot(Dependency... children) {
        Arrays.stream(children).forEach(this::addChildToRoot);
    }

    protected void ensureDependencyExists(Dependency dependency) {
        dependencies.putIfAbsent(dependency.getExternalId(), dependency);
    }

    protected void ensureDependencyAndRelationshipExists(Dependency dependency) {
        ensureDependencyExists(dependency);
        relationships.putIfAbsent(dependency.getExternalId(), new HashSet<>());
    }

    protected void addRelationship(Dependency parent, Dependency child) {
        relationships.get(parent.getExternalId())
            .add(child.getExternalId());
    }
}
