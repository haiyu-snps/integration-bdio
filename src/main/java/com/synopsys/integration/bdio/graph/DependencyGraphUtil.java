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
import java.util.function.Supplier;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class DependencyGraphUtil {
    private DependencyGraphUtil() {
        // Hiding constructor
    }

    public static void copyDirectDependencies(DependencyGraph destinationGraph, DependencyGraph sourceGraph) {
        copyDependencies(destinationGraph, sourceGraph, sourceGraph::getDirectDependencies);
    }

    public static void copyDirectDependenciesToParent(DependencyGraph destinationGraph, Dependency parent, DependencyGraph sourceGraph) {
        copyDependenciesToParent(destinationGraph, parent, sourceGraph, sourceGraph::getDirectDependencies);
    }

    public static void copyRootDependencies(DependencyGraph destinationGraph, DependencyGraph sourceGraph) {
        copyDependencies(destinationGraph, sourceGraph, sourceGraph::getRootDependencies);
    }

    public static void copyRootDependenciesToParent(DependencyGraph destinationGraph, Dependency parent, DependencyGraph sourceGraph) {
        copyDependenciesToParent(destinationGraph, parent, sourceGraph, sourceGraph::getRootDependencies);
    }

    public static void copyDependencies(DependencyGraph destinationGraph, DependencyGraph sourceGraph, Supplier<Set<Dependency>> dependencies) {
        Set<Dependency> encountered = new HashSet<>();
        for (Dependency dependency : dependencies.get()) {
            destinationGraph.addDirectDependency(dependency);
            copyDependencyTreeFromGraph(destinationGraph, dependency, sourceGraph, encountered);
        }
    }

    public static void copyDependenciesToParent(DependencyGraph destinationGraph, Dependency parent, DependencyGraph sourceGraph, Supplier<Set<Dependency>> dependencies) {
        Set<Dependency> encountered = new HashSet<>();
        for (Dependency dependency : dependencies.get()) {
            destinationGraph.addChildWithParent(dependency, parent);
            copyDependencyTreeFromGraph(destinationGraph, dependency, sourceGraph, encountered);
        }
    }

    private static void copyDependencyTreeFromGraph(
        DependencyGraph destinationGraph,
        Dependency parentDependency,
        DependencyGraph sourceGraph,
        Set<Dependency> encountered
    ) {
        for (Dependency dependency : sourceGraph.getChildrenForParent(parentDependency)) {
            if (!encountered.contains(dependency)) {
                encountered.add(dependency);
                copyDependencyTreeFromGraph(destinationGraph, dependency, sourceGraph, encountered);
            }
            destinationGraph.addChildWithParent(dependency, parentDependency);
        }
    }

}
