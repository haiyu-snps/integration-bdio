/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public interface MutableDependencyGraph extends DependencyGraph {
    void addGraphAsChildrenToRoot(DependencyGraph sourceGraph);

    void addGraphAsChildrenToParent(Dependency parent, DependencyGraph sourceGraph);

    void addParentWithChild(Dependency parent, Dependency child);

    void addParentWithChildren(Dependency parent, List<Dependency> children);

    void addParentWithChildren(Dependency parent, Set<Dependency> children);

    void addParentWithChildren(Dependency parent, Dependency... children);

    void addChildWithParent(Dependency child, Dependency parent);

    void addChildWithParents(Dependency child, List<Dependency> parents);

    void addChildWithParents(Dependency child, Set<Dependency> parents);

    void addChildWithParents(Dependency child, Dependency... parents);

    void addChildToRoot(Dependency child);

    void addChildrenToRoot(List<Dependency> children);

    void addChildrenToRoot(Set<Dependency> children);

    void addChildrenToRoot(Dependency... children);

}
