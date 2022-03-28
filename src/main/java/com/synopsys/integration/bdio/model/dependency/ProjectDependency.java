/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model.dependency;

import com.synopsys.integration.bdio.model.externalid.ExternalId;

// Allows project dependency nodes to be identified from the graph
// Additional fields specific to projects can be added here as well.
public class ProjectDependency extends Dependency {
    public ProjectDependency(String name, String version, ExternalId externalId) {
        super(name, version, externalId);
    }

    public ProjectDependency(String name, ExternalId externalId) {
        super(name, externalId);
    }

    public ProjectDependency(ExternalId externalId) {
        super(externalId);
    }

    public ProjectDependency(Dependency existingDependency) {
        super(existingDependency.getName(), existingDependency.getVersion(), existingDependency.getExternalId());
    }
}
