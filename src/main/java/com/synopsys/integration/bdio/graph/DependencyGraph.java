/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph;

import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public interface DependencyGraph {
    Set<Dependency> getRootDependencies();

    Set<ExternalId> getRootDependencyExternalIds();

    boolean hasDependency(Dependency dependency);

    boolean hasDependency(ExternalId dependency);

    Dependency getDependency(ExternalId dependency);

    Set<Dependency> getChildrenForParent(Dependency parent);

    Set<ExternalId> getChildrenExternalIdsForParent(Dependency parent);

    Set<Dependency> getChildrenForParent(ExternalId parent);

    Set<ExternalId> getChildrenExternalIdsForParent(ExternalId parent);

    Set<ExternalId> getParentExternalIdsForChild(Dependency child);

    Set<Dependency> getParentsForChild(ExternalId child);

    Set<Dependency> getParentsForChild(Dependency child);

    Set<ExternalId> getParentExternalIdsForChild(ExternalId child);

}
