package com.blackducksoftware.integration.hub.bdio.simple.model;

public class MavenExternalId extends ExternalId {
    public String group;

    public MavenExternalId(final String group, final String name, final String version) {
        super(name, version);
        this.group = group;
    }

    @Override
    public String createExternalId(final Forge forge) {
        return group + forge.separator + name + forge.separator + version;
    }

}
