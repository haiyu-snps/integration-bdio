/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio;

import java.util.List;

import com.synopsys.integration.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.bdio.model.BdioNode;
import com.synopsys.integration.bdio.model.BdioRelationship;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class BdioPropertyHelper {
    public void addRelationships(final BdioNode node, final List<? extends BdioNode> children) {
        for (final BdioNode child : children) {
            addRelationship(node, child);
        }
    }

    public void addRelationship(final BdioNode node, final BdioNode child) {
        final BdioRelationship relationship = new BdioRelationship();
        relationship.related = child.id;
        relationship.relationshipType = "DYNAMIC_LINK";
        node.relationships.add(relationship);
    }

    public BdioExternalIdentifier createExternalIdentifier(final ExternalId externalId) {
        final BdioExternalIdentifier externalIdentifier = new BdioExternalIdentifier();
        externalIdentifier.externalId = externalId.createExternalId();
        externalIdentifier.forge = externalId.getForge().getName();
        externalIdentifier.externalIdMetaData = externalId;
        return externalIdentifier;
    }

}
