package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class ArchitectureExternalId extends NameVersionExternalId {
    public String architecture;

    public ArchitectureExternalId(final Forge forge, final String name, final String version, final String architecture) {
        super(forge, name, version);
        this.architecture = architecture;
    }

    @Override
    public String[] getExternalIdPieces() {
        return new String[] { name, version, architecture };
    }

}
