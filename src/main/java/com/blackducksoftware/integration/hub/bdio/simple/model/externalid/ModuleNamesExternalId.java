package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class ModuleNamesExternalId implements ExternalId {
    public String[] moduleNames;

    public ModuleNamesExternalId(final String... moduleNames) {
        this.moduleNames = moduleNames;
    }

    @Override
    public String createExternalId(final Forge forge) {
        return StringUtils.join(moduleNames, forge.separator);
    }

}
