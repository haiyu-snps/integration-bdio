package com.blackducksoftware.integration.hub.bdio.simple.model;

public class ExternalId {
    public final String name;

    public final String version;

    public ExternalId(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    public String createExternalId(final Forge forge) {
        return name + forge.separator + version;
    }
}
