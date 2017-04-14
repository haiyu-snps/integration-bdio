package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class PathExternalId implements ExternalId {
    public String path;

    public PathExternalId(final String path) {
        this.path = path;
    }

    @Override
    public String createExternalId(final Forge forge) {
        return path;
    }

}
