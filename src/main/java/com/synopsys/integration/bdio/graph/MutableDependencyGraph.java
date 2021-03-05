/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
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

    void addParentWithChild(final Dependency parent, final Dependency child);

    void addParentWithChildren(final Dependency parent, final List<Dependency> children);

    void addParentWithChildren(final Dependency parent, final Set<Dependency> children);

    void addParentWithChildren(final Dependency parent, final Dependency... children);

    void addChildWithParent(final Dependency child, final Dependency parent);

    void addChildWithParents(final Dependency child, final List<Dependency> parents);

    void addChildWithParents(final Dependency child, final Set<Dependency> parents);

    void addChildWithParents(final Dependency child, final Dependency... parents);

    void addChildToRoot(final Dependency child);

    void addChildrenToRoot(final List<Dependency> children);

    void addChildrenToRoot(final Set<Dependency> children);

    void addChildrenToRoot(final Dependency... children);

}
