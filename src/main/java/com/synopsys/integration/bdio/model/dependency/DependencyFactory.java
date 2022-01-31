/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model.dependency;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyFactory {
    private final ExternalIdFactory externalIdFactory;

    public DependencyFactory(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Dependency createPathDependency(final Forge forge, final String path) {
        return new Dependency(externalIdFactory.createPathExternalId(forge, path));
    }

    public Dependency createModuleNamesDependency(final Forge forge, final String... moduleNames) {
        return new Dependency(externalIdFactory.createModuleNamesExternalId(forge, moduleNames));
    }

    public Dependency createNameVersionDependency(final Forge forge, final String name, final String version) {
        return new Dependency(externalIdFactory.createNameVersionExternalId(forge, name, version));
    }

    public Dependency createNameVersionDependency(final Forge forge, final String name) {
        return createNameVersionDependency(forge, name, null);
    }

    public Dependency createYoctoDependency(final String layer, final String name, final String version) {
        return new Dependency(externalIdFactory.createYoctoExternalId(layer, name, version));
    }

    public Dependency createYoctoDependency(final String layer, final String name) {
        return createYoctoDependency(layer, name, null);
    }

    public Dependency createMavenDependency(final String group, final String name, final String version) {
        return new Dependency(externalIdFactory.createMavenExternalId(group, name, version));
    }

    public Dependency createMavenDependency(final String group, final String name) {
        return createMavenDependency(group, name, null);
    }

    public Dependency createArchitectureDependency(final Forge forge, final String name, final String version, final String architecture) {
        return new Dependency(externalIdFactory.createArchitectureExternalId(forge, name, version, architecture));
    }

    public Dependency createArchitectureDependency(final Forge forge, final String name, final String architecture) {
        return createArchitectureDependency(forge, name, null, architecture);
    }

}
