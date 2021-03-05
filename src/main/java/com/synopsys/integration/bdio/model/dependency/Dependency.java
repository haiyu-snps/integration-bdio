/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model.dependency;

import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class Dependency extends DependencyId {
    private String name;
    private String version;
    private ExternalId externalId;

    public Dependency(final String name, final String version, final ExternalId externalId) {
        this.name = name;
        this.version = version;
        this.externalId = externalId;
    }

    public Dependency(final String name, final ExternalId externalId) {
        this(name, externalId.getVersion(), externalId);
    }

    public Dependency(final ExternalId externalId) {
        this(externalId.getName(), externalId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ExternalId getExternalId() {
        return externalId;
    }

    public void setExternalId(ExternalId externalId) {
        this.externalId = externalId;
    }
}
