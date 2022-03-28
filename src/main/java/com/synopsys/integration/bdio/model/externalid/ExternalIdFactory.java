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
    public static final ExternalIdFactory STATIC = new ExternalIdFactory();

    public ExternalId createPathExternalId(Forge forge, String path) {
        ExternalId externalId = new ExternalId(forge);
        externalId.setPath(path);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createModuleNamesExternalId(Forge forge, String... moduleNames) {
        ExternalId externalId = new ExternalId(forge);
        externalId.setModuleNames(moduleNames);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createNameVersionExternalId(Forge forge, String name, String version) {
        ExternalId externalId = new ExternalId(forge);
        externalId.setName(name);
        externalId.setVersion(version);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createNameVersionExternalId(Forge forge, String name) {
        return createNameVersionExternalId(forge, name, null);
    }

    public ExternalId createYoctoExternalId(String layer, String name, String version) {
        ExternalId externalId = createNameVersionExternalId(Forge.YOCTO, name, version);
        externalId.setLayer(layer);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createYoctoExternalId(String layer, String name) {
        return createYoctoExternalId(layer, name, null);
    }

    public ExternalId createMavenExternalId(String group, String name, String version) {
        ExternalId externalId = createNameVersionExternalId(Forge.MAVEN, name, version);
        externalId.setGroup(group);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createMavenExternalId(String group, String name) {
        return createMavenExternalId(group, name, null);
    }

    public ExternalId createArchitectureExternalId(Forge forge, String name, String version, String architecture) {
        ExternalId externalId = createNameVersionExternalId(forge, name, version);
        externalId.setArchitecture(architecture);
        checkForValidity(externalId);
        return externalId;
    }

    public ExternalId createArchitectureExternalId(Forge forge, String name, String architecture) {
        return createArchitectureExternalId(forge, name, null, architecture);
    }

    /**
     * An ExternalId should be able to create a bdioId - if it can not, an appropriate IllegalStateException will be thrown.
     */
    private void checkForValidity(ExternalId externalId) {
        externalId.createBdioId();
    }

}
