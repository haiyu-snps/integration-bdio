package com.blackducksoftware.integration.hub.bdio.simple.model;

import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;

public class DependencyNode {
    public final ExternalId externalId;

    public final List<DependencyNode> children;

    public DependencyNode(final ExternalId externalId, final List<DependencyNode> children) {
        this.externalId = externalId;
        this.children = children;
    }

}
