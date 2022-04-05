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

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class DependencyGraphUtil {
    private DependencyGraphUtil() {
        // Hiding constructor
    }

    public static void copyRootDependenciesToParent(DependencyGraph destinationGraph, Dependency parent, DependencyGraph sourceGraph) {
        Set<Dependency> encountered = new HashSet<>();
        for (Dependency dependency : sourceGraph.getRootDependencies()) {
            destinationGraph.addChildWithParent(dependency, parent);
            copyDependencyFromGraph(destinationGraph, dependency, sourceGraph, encountered);
        }
    }

    public static void copyRootDependencies(DependencyGraph destinationGraph, DependencyGraph sourceGraph) {
        Set<Dependency> encountered = new HashSet<>();
        for (Dependency dependency : sourceGraph.getRootDependencies()) {
            destinationGraph.addChildToRoot(dependency);
            copyDependencyFromGraph(destinationGraph, dependency, sourceGraph, encountered);
        }
    }

    private static void copyDependencyFromGraph(
        DependencyGraph destinationGraph,
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
