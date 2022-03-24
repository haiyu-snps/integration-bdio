/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;

public class DependencyGraphCombiner {
    public void addGraphAsChildrenToRoot(MutableDependencyGraph destinationGraph, DependencyGraph sourceGraph) {
        Optional<ProjectDependency> rootDependency = sourceGraph.getRootDependency();
        if (rootDependency.isPresent()) {
            destinationGraph.addChildToRoot(rootDependency.get());
            copyRootDependenciesToParent(destinationGraph, sourceGraph, rootDependency.get());
        } else {
            copyRootDependencies(destinationGraph, sourceGraph);
        }

    }

    public void copyRootDependenciesToParent(MutableDependencyGraph destinationGraph, DependencyGraph sourceGraph, Dependency parent) {
        Set<Dependency> encountered = new HashSet<>();
        for (Dependency dependency : sourceGraph.getRootDependencies()) {
            destinationGraph.addChildWithParent(dependency, parent);
            copyDependencyFromGraph(destinationGraph, dependency, sourceGraph, encountered);
        }
    }

    public void copyRootDependencies(MutableDependencyGraph destinationGraph, DependencyGraph sourceGraph) {
        Set<Dependency> encountered = new HashSet<>();
        for (Dependency dependency : sourceGraph.getRootDependencies()) {
            destinationGraph.addChildToRoot(dependency);
            copyDependencyFromGraph(destinationGraph, dependency, sourceGraph, encountered);
        }
    }

    public void addGraphAsChildrenToParent(MutableDependencyGraph destinationGraph, Dependency parent, DependencyGraph sourceGraph) {
        Optional<ProjectDependency> projectDependency = sourceGraph.getRootDependency();
        Dependency actualParent = parent;
        if (projectDependency.isPresent()) {
            actualParent = projectDependency.get();
            destinationGraph.addParentWithChild(parent, actualParent);
        }
        copyRootDependenciesToParent(destinationGraph, sourceGraph, actualParent);
    }

    public void copyDependencyFromGraph(
        MutableDependencyGraph destinationGraph,
        Dependency parentDependency,
        DependencyGraph sourceGraph,
        Set<Dependency> encountered
    ) {
        for (Dependency dependency : sourceGraph.getChildrenForParent(parentDependency)) {
            if (!encountered.contains(dependency)) {
                encountered.add(dependency);

                copyDependencyFromGraph(destinationGraph, dependency, sourceGraph, encountered);
            }
            destinationGraph.addChildWithParent(dependency, parentDependency);
        }
    }

}
