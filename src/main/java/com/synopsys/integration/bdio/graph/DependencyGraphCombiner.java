/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph;

import java.util.HashSet;
import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class DependencyGraphCombiner {
    public void addGraphAsChildrenToRoot(final MutableDependencyGraph destinationGraph, final DependencyGraph sourceGraph) {
        final Set<Dependency> encountered = new HashSet<>();
        for (final Dependency dependency : sourceGraph.getRootDependencies()) {
            destinationGraph.addChildToRoot(dependency);
            copyDependencyFromGraph(destinationGraph, dependency, sourceGraph, encountered);
        }
    }

    public void addGraphAsChildrenToParent(final MutableDependencyGraph destinationGraph, final Dependency parent, final DependencyGraph sourceGraph) {
        final Set<Dependency> encountered = new HashSet<>();
        for (final Dependency dependency : sourceGraph.getRootDependencies()) {
            destinationGraph.addChildWithParent(dependency, parent);
            copyDependencyFromGraph(destinationGraph, dependency, sourceGraph, encountered);
        }
    }

    public void copyDependencyFromGraph(final MutableDependencyGraph destinationGraph, final Dependency parentDependency, final DependencyGraph sourceGraph, final Set<Dependency> encountered) {
        for (final Dependency dependency : sourceGraph.getChildrenForParent(parentDependency)) {
            if (!encountered.contains(dependency)) {
                encountered.add(dependency);

                copyDependencyFromGraph(destinationGraph, dependency, sourceGraph, encountered);
            }
            destinationGraph.addChildWithParent(dependency, parentDependency);
        }
    }

}
