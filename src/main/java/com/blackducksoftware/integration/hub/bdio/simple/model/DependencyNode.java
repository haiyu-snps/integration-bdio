package com.blackducksoftware.integration.hub.bdio.simple.model;

import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;

/**
 * The externalId is required, but name, version, and children are all optional.
 */
public class DependencyNode {
    public final String name;

    public final String version;

    public final ExternalId externalId;

    public final List<DependencyNode> children;

    public DependencyNode(final String name, final String version, final ExternalId externalId, final List<DependencyNode> children) {
        this.name = name;
        this.version = version;
        this.externalId = externalId;
        this.children = children;
    }

    public DependencyNode(final String name, final ExternalId externalId, final List<DependencyNode> children) {
        this.name = name;
        this.version = null;
        this.externalId = externalId;
        this.children = children;
    }

    public DependencyNode(final ExternalId externalId, final List<DependencyNode> children) {
        this.name = null;
        this.version = null;
        this.externalId = externalId;
        this.children = children;
    }

    public DependencyNode(final ExternalId externalId) {
        this.name = null;
        this.version = null;
        this.externalId = externalId;
        this.children = null;
    }

}
