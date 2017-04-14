package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class MavenExternalId extends NameVersionExternalId {
    public String group;

    public MavenExternalId(final Forge forge, final String group, final String artifact, final String version) {
        super(forge, artifact, version);
        this.group = group;
    }

    @Override
    public String[] getExternalIdPieces() {
        return new String[] { group, name, version };
    }

}
