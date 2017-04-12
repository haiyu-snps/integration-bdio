package com.blackducksoftware.integration.hub.bdio.simple.model;

public class MavenExternalId extends ExternalId {
    public String group;

    @Override
    public String createExternalId(final Forge forge) {
        return group + forge.separator + name + forge.separator + version;
    }

}
