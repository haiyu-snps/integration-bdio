/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph.builder;

import com.synopsys.integration.exception.IntegrationException;

public class MissingExternalIdException extends IntegrationException {
    public MissingExternalIdException(LazyId lazyId) {
        super(String.format("A dependency (%s) in a relationship in the graph never had it's external id set.", lazyId));
    }

}
