package com.blackducksoftware.integration.hub.bdio.simple.model;

import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ArchitectureExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ModuleNamesExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId;

public enum Forge {
    anaconda("=", NameVersionExternalId.class),
    bower("#", NameVersionExternalId.class),
    centos("/", ArchitectureExternalId.class),
    cocoapods(":", NameVersionExternalId.class),
    cpan(":", ModuleNamesExternalId.class),
    goget("", PathExternalId.class),
    maven(":", MavenExternalId.class),
    npm("@", NameVersionExternalId.class),
    nuget("/", NameVersionExternalId.class),
    pypi("/", NameVersionExternalId.class),
    rubygems("=", NameVersionExternalId.class);

    public final String separator;

    public final Class<? extends ExternalId> externalIdClass;

    private Forge(final String separator, final Class<? extends ExternalId> externalIdClass) {
        this.separator = separator;
        this.externalIdClass = externalIdClass;
    }

}
