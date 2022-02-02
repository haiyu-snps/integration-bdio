/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph.builder;

import com.synopsys.integration.bdio.model.externalid.ExternalId;

@FunctionalInterface
public interface LazyBuilderMissingExternalIdHandler {
    ExternalId handleMissingExternalId(final LazyId lazyId, final LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo lazyDependencyInfo) throws MissingExternalIdException;

}
