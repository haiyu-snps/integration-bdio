package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class NameVersionExternalId extends ExternalId {
    public final String name;

    public final String version;

    public NameVersionExternalId(final Forge forge, final String name, final String version) {
        super(forge);
        this.name = name;
        this.version = version;
    }

    @Override
    public String[] getExternalIdPieces() {
        return new String[] { name, version };
    }

}
