/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model.externalid;

import com.synopsys.integration.bdio.model.Forge;

public class ExternalIdFactory {
    public ExternalId createPathExternalId(final Forge forge, final String path) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.setPath(path);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createModuleNamesExternalId(final Forge forge, final String... moduleNames) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.setModuleNames(moduleNames);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createNameVersionExternalId(final Forge forge, final String name, final String version) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.setName(name);
        externalId.setVersion(version);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createNameVersionExternalId(final Forge forge, final String name) {
        return createNameVersionExternalId(forge, name, null);
    }

    public ExternalId createYoctoExternalId(final String layer, final String name, final String version) {
        final ExternalId externalId = createNameVersionExternalId(Forge.YOCTO, name, version);
        externalId.setLayer(layer);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createYoctoExternalId(final String layer, final String name) {
        return createYoctoExternalId(layer, name, null);
    }

    public ExternalId createMavenExternalId(final String group, final String name, final String version) {
        final ExternalId externalId = createNameVersionExternalId(Forge.MAVEN, name, version);
        externalId.setGroup(group);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createMavenExternalId(final String group, final String name) {
        return createMavenExternalId(group, name, null);
    }

    public ExternalId createArchitectureExternalId(final Forge forge, final String name, final String version, final String architecture) {
        final ExternalId externalId = createNameVersionExternalId(forge, name, version);
        externalId.setArchitecture(architecture);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createArchitectureExternalId(final Forge forge, final String name, final String architecture) {
        return createArchitectureExternalId(forge, name, null, architecture);
    }

    /**
     * An ExternalId should be able to create a bdioId - if it can not, an appropriate IllegalStateException will be thrown.
     */
    private void checkForValidity(final ExternalId externalId) {
        externalId.createBdioId();
    }

}
