/**
 * integration-bdio
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.hub.bdio;

import java.util.List;

import com.synopsys.integration.hub.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.hub.bdio.model.BdioNode;
import com.synopsys.integration.hub.bdio.model.BdioRelationship;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;

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
        externalIdentifier.forge = externalId.forge.toString();
        externalIdentifier.externalIdMetaData = externalId;
        return externalIdentifier;
    }

}
