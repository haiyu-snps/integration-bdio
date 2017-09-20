package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class ExternalIdFactory {
    public ExternalId createNameVersionExternalId(final Forge forge, final String name, final String version) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.name = name;
        externalId.version = version;
        return externalId;
    }

    public ExternalId createMavenExternalId(final String group, final String name, final String version) {
        final ExternalId externalId = createNameVersionExternalId(Forge.MAVEN, name, version);
        externalId.group = group;
        return externalId;
    }

    public ExternalId createArchitectureExternalId(final Forge forge, final String name, final String version, final String architecture) {
        final ExternalId externalId = createNameVersionExternalId(forge, name, version);
        externalId.architecture = architecture;
        return externalId;
    }

    public ExternalId createModuleNamesExternalId(final Forge forge, final String... moduleNames) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.moduleNames = moduleNames;
        return externalId;
    }

    public ExternalId createPathExternalId(final Forge forge, final String path) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.path = path;
        return externalId;
    }

}
