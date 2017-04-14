package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class ArchitectureExternalId extends NameVersionExternalId {
    public String architecture;

    public ArchitectureExternalId(final String name, final String version, final String architecture) {
        super(name, version);
        this.architecture = architecture;
    }

    @Override
    public String createExternalId(final Forge forge) {
        return name + forge.separator + version + forge.separator + architecture;
    }

}
