/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model.dependency;

import java.util.UUID;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

// Allows project dependency nodes to be identified from the graph
// Additional fields specific to projects can be added here as well.
public class ProjectDependency extends Dependency {
    private final boolean isPlaceholder;

    public ProjectDependency(String name, String version, ExternalId externalId) {
        super(name, version, externalId);
        this.isPlaceholder = false;
    }

    public ProjectDependency(String name, ExternalId externalId) {
        super(name, externalId);
        this.isPlaceholder = false;
    }

    public ProjectDependency(ExternalId externalId) {
        super(externalId);
        this.isPlaceholder = false;
    }

    public ProjectDependency(Dependency existingDependency) {
        super(existingDependency.getName(), existingDependency.getVersion(), existingDependency.getExternalId());
        this.isPlaceholder = false;
    }

    public ProjectDependency() {
        super(ExternalIdFactory.STATIC.createModuleNamesExternalId(Forge.GITHUB, UUID.randomUUID().toString()));
        this.isPlaceholder = true;
    }

    public boolean isPlaceholder() {
        return isPlaceholder;
    }

    // To ignore the placeholder field
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
