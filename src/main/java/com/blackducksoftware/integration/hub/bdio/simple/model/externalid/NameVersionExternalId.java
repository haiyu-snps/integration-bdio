package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class NameVersionExternalId implements ExternalId {
    public final String name;

    public final String version;

    public NameVersionExternalId(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public String createExternalId(final Forge forge) {
        return name + forge.separator + version;
    }

}
