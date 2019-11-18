package com.synopsys.integration.bdio.graph.builder;

import com.synopsys.integration.bdio.model.externalid.ExternalId;

@FunctionalInterface
public interface LazyBuilderMissingExternalIdHandler {
    ExternalId handleMissingExternalId(LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo lazyDependencyInfo) throws MissingExternalIdException;
}
