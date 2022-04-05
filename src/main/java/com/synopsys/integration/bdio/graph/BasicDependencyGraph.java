/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class BasicDependencyGraph extends DependencyGraph {
    private final Set<ExternalId> rootDependencies = new HashSet<>();

    @Override
    public Set<Dependency> getRootDependencies() {
        return rootDependencies.stream()
            .map(dependencies::get)
            .collect(Collectors.toSet());
    }

    @Override
    public void addChildToRoot(Dependency child) {
        ensureDependencyExists(child);
        rootDependencies.add(child.getExternalId());
    }

    public void copyGraphToRoot(BasicDependencyGraph sourceGraph) {
        DependencyGraphUtil.copyRootDependencies(this, sourceGraph);
    }

    public void copyGraphToRoot(ProjectDependencyGraph sourceGraph) {
        ProjectDependency rootDependency = sourceGraph.getRootDependency();
        addChildToRoot(rootDependency);
        DependencyGraphUtil.copyRootDependenciesToParent(this, rootDependency, sourceGraph);
    }
}
