package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class PathExternalId extends ExternalId {
    public String path;

    public PathExternalId(final Forge forge, final String path) {
        super(forge);
        this.path = path;
    }

    @Override
    public String[] getExternalIdPieces() {
        return new String[] { path };
    }

}
