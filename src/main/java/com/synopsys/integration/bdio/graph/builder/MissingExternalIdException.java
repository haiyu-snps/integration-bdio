package com.synopsys.integration.bdio.graph.builder;

import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.exception.IntegrationException;

public class MissingExternalIdException extends IntegrationException {
    public MissingExternalIdException(DependencyId dependencyId) {
        super(String.format("A dependency (%s) in a relationship in the graph never had it's external id set.", dependencyId.toString()));
    }
}
