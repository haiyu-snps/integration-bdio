package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class ModuleNamesExternalId extends ExternalId {
    public String[] moduleNames;

    public ModuleNamesExternalId(final Forge forge, final String... moduleNames) {
        super(forge);
        this.moduleNames = moduleNames;
    }

    @Override
    public String[] getExternalIdPieces() {
        return moduleNames;
    }

}
