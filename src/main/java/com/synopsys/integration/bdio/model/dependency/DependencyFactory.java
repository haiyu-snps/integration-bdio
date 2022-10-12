/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model.dependency;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyFactory {
    private final ExternalIdFactory externalIdFactory;

    public DependencyFactory() {
        this(ExternalId.FACTORY);
    }

    public DependencyFactory(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Dependency createPathDependency(Forge forge, String path) {
        return new Dependency(externalIdFactory.createPathExternalId(forge, path), null);
    }

    public Dependency createModuleNamesDependency(Forge forge, String... moduleNames) {
        return new Dependency(externalIdFactory.createModuleNamesExternalId(forge, moduleNames), null);
    }

    public Dependency createNameVersionDependency(String scope, Forge forge, String name, String version) {
        return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(forge, name, version), scope);
    }

    public Dependency createNameVersionDependency(Forge forge, String name, String version) {
        return createNameVersionDependency(null, forge, name, version);
    }

    public Dependency createNameVersionDependency(Forge forge, String name) {
        return createNameVersionDependency(null, forge, name, null);
    }

    public Dependency createYoctoDependency(String scope, String layer, String name, String version) {
        return new Dependency(externalIdFactory.createYoctoExternalId(layer, name, version), scope);
    }

    public Dependency createYoctoDependency(String layer, String name, String version) {
        return createYoctoDependency(null, layer, name, version);
    }

    public Dependency createYoctoDependency(String layer, String name) {
        return createYoctoDependency(null, layer, name, null);
    }

    public Dependency createMavenDependency(String scope, String group, String name, String version) {
        return new Dependency(externalIdFactory.createMavenExternalId(group, name, version), scope);
    }

    public Dependency createMavenDependency(String group, String name, String version) {
        return createMavenDependency(null, group, name, version);
    }

    public Dependency createMavenDependency(String group, String name) {
        return createMavenDependency(null, group, name, null);
    }

    public Dependency createArchitectureDependency(Forge forge, String name, String version, String architecture) {
        return new Dependency(externalIdFactory.createArchitectureExternalId(forge, name, version, architecture), null);
    }

    public Dependency createArchitectureDependency(Forge forge, String name, String architecture) {
        return createArchitectureDependency(forge, name, null, architecture);
    }

}
