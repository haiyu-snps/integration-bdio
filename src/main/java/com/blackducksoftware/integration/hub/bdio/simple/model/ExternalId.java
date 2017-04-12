package com.blackducksoftware.integration.hub.bdio.simple.model;

public class ExternalId {
    public String name;

    public String version;

    public String createExternalId(final Forge forge) {
        return name + forge.separator + version;
    }
}
