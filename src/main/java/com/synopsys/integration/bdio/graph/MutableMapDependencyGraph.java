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
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class MutableMapDependencyGraph implements MutableDependencyGraph {
    private final ProjectDependency rootDependency;
    private final boolean isRootProjectPlaceholder;
    private final Map<ExternalId, Dependency> dependencies = new HashMap<>();
    private final Map<ExternalId, Set<ExternalId>> relationships = new HashMap<>();
    private final DependencyGraphCombiner dependencyGraphCombiner = new DependencyGraphCombiner();

    public MutableMapDependencyGraph() {
        this(Forge.GITHUB);
    }

    public MutableMapDependencyGraph(Forge forge) {
        this(new ProjectDependency(ExternalId.FACTORY.createModuleNamesExternalId(forge, UUID.randomUUID().toString())), true);
    }

    public MutableMapDependencyGraph(@NotNull ProjectDependency rootDependency) {
        this(rootDependency, false);
    }

    public MutableMapDependencyGraph(@NotNull ProjectDependency rootDependency, boolean isRootProjectPlaceholder) {
        this.rootDependency = rootDependency;
        this.isRootProjectPlaceholder = isRootProjectPlaceholder;
    }

    @Override
    public void addGraphAsChildrenToRoot(DependencyGraph sourceGraph) {
        dependencyGraphCombiner.addGraphAsChildrenToRoot(this, sourceGraph);
    }

    @Override
    public void addGraphAsChildrenToParent(Dependency parent, DependencyGraph sourceGraph) {
        dependencyGraphCombiner.addGraphAsChildrenToParent(this, parent, sourceGraph);
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
    @Nullable
    public Dependency getDependency(ExternalId dependency) {
        return dependencies.getOrDefault(dependency, null);
    }

    @Override
    public Set<Dependency> getChildrenForParent(ExternalId parent) {
        return getChildrenExternalIdsForParent(parent).stream()
            .map(dependencies::get)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<Dependency> getParentsForChild(ExternalId child) {
        return getParentExternalIdsForChild(child).stream()
            .map(dependencies::get)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<ExternalId> getChildrenExternalIdsForParent(ExternalId parent) {
        return relationships.getOrDefault(parent, new HashSet<>());
    }

    @Override
    public Set<ExternalId> getParentExternalIdsForChild(ExternalId child) {
        return relationships.entrySet().stream()
            .filter(externalIdSetEntry ->
                externalIdSetEntry.getValue().stream()
                    .anyMatch(child::equals)
            )
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
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
        return getRootDependencies().stream()
            .map(Dependency::getExternalId)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isRootProjectPlaceholder() {
        return isRootProjectPlaceholder;
    }

    @Override
    public ProjectDependency getRootDependency() {
        return rootDependency;
    }

    @Override
    public Set<Dependency> getRootDependencies() {
        return getChildrenForParent(rootDependency);
    }

    @Override
    public void addChildToRoot(Dependency child) {
        ensureDependencyExists(child);
        addParentWithChild(rootDependency, child);
    }

    @Override
    public void addChildrenToRoot(List<Dependency> children) {
        children.forEach(this::addChildToRoot);
    }

    @Override
    public void addChildrenToRoot(Set<Dependency> children) {
        children.forEach(this::addChildToRoot);
    }

    @Override
    public void addChildrenToRoot(Dependency... children) {
        Arrays.stream(children).forEach(this::addChildToRoot);
    }

    private void ensureDependencyExists(Dependency dependency) {
        if (isNotRoot(dependency)) {
            dependencies.putIfAbsent(dependency.getExternalId(), dependency);
        }
    }

    private void ensureDependencyAndRelationshipExists(Dependency dependency) {
        ensureDependencyExists(dependency);
        relationships.putIfAbsent(dependency.getExternalId(), new HashSet<>());
    }

    private void addRelationship(Dependency parent, Dependency child) {
        relationships.get(parent.getExternalId())
            .add(child.getExternalId());
    }

    private boolean isNotRoot(Dependency dependency) {
        return !rootDependency.equals(dependency);
    }

}
