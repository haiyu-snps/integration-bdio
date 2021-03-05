/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph.builder;

import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

@FunctionalInterface
public interface LazyBuilderMissingExternalIdHandler {
    ExternalId handleMissingExternalId(final DependencyId dependencyId, final LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo lazyDependencyInfo) throws MissingExternalIdException;
}
