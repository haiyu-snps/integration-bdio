/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph.builder;

import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.exception.IntegrationException;

public class MissingExternalIdException extends IntegrationException {
    public MissingExternalIdException(DependencyId dependencyId) {
        super(String.format("A dependency (%s) in a relationship in the graph never had it's external id set.", dependencyId));
    }
}
