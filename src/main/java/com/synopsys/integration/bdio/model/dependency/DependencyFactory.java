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
    public static final DependencyFactory STATIC = new DependencyFactory(ExternalIdFactory.STATIC);

    private final ExternalIdFactory externalIdFactory;

    public DependencyFactory(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Dependency createPathDependency(Forge forge, String path) {
        return new Dependency(externalIdFactory.createPathExternalId(forge, path));
    }

    public Dependency createModuleNamesDependency(Forge forge, String... moduleNames) {
        return new Dependency(externalIdFactory.createModuleNamesExternalId(forge, moduleNames));
    }

    public Dependency createNameVersionDependency(Forge forge, String name, String version) {
        return new Dependency(externalIdFactory.createNameVersionExternalId(forge, name, version));
    }

    public Dependency createNameVersionDependency(Forge forge, String name) {
        return createNameVersionDependency(forge, name, null);
    }

    public Dependency createYoctoDependency(String layer, String name, String version) {
        return new Dependency(externalIdFactory.createYoctoExternalId(layer, name, version));
    }

    public Dependency createYoctoDependency(String layer, String name) {
        return createYoctoDependency(layer, name, null);
    }

    public Dependency createMavenDependency(String group, String name, String version) {
        return new Dependency(externalIdFactory.createMavenExternalId(group, name, version));
    }

    public Dependency createMavenDependency(String group, String name) {
        return createMavenDependency(group, name, null);
    }

    public Dependency createArchitectureDependency(Forge forge, String name, String version, String architecture) {
        return new Dependency(externalIdFactory.createArchitectureExternalId(forge, name, version, architecture));
    }

    public Dependency createArchitectureDependency(Forge forge, String name, String architecture) {
        return createArchitectureDependency(forge, name, null, architecture);
    }

}
