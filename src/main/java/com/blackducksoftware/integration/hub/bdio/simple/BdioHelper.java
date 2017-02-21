/**
 * Hub Bdio Simple
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.bdio.simple;

import java.util.List;
import java.util.UUID;

public class BdioHelper {
    public BdioBillOfMaterials createBillOfMaterials(final String projectName) {
        final BdioBillOfMaterials billOfMaterials = new BdioBillOfMaterials();
        billOfMaterials.setId(String.format("uuid:%s", UUID.randomUUID()));
        billOfMaterials.setName(String.format("%s Black Duck I/O Export", projectName));
        billOfMaterials.setSpecVersion("1.1.0");

        return billOfMaterials;
    }

    public BdioProject createProject(final String projectName, final String projectVersion, final String id, final String externalSystemTypeId,
            final String externalId) {
        final BdioProject project = new BdioProject();
        project.setId(id);
        project.setName(projectName);
        project.setRevision(projectVersion);
        project.setBdioExternalIdentifier(createExternalIdentifier(externalSystemTypeId, externalId));

        return project;
    }

    public BdioProject createProject(final String projectName, final String projectVersion, final String id, final BdioExternalIdentifier externalIdentifier) {
        final BdioProject project = new BdioProject();
        project.setId(id);
        project.setName(projectName);
        project.setRevision(projectVersion);
        project.setBdioExternalIdentifier(externalIdentifier);

        return project;
    }

    public BdioComponent createComponent(final String componentName, final String componentVersion, final String id, final String externalSystemTypeId,
            final String externalId) {
        final BdioComponent component = new BdioComponent();
        component.setId(id);
        component.setName(componentName);
        component.setRevision(componentVersion);
        component.setBdioExternalIdentifier(createExternalIdentifier(externalSystemTypeId, externalId));

        return component;
    }

    public BdioComponent createComponent(final String componentName, final String componentVersion, final String id,
            final BdioExternalIdentifier externalIdentifier) {
        final BdioComponent component = new BdioComponent();
        component.setId(id);
        component.setName(componentName);
        component.setRevision(componentVersion);
        component.setBdioExternalIdentifier(externalIdentifier);

        return component;
    }

    public void addRelationships(final BdioNode node, final List<BdioNode> children) {
        for (final BdioNode child : children) {
            addRelationship(node, child);
        }
    }

    public void addRelationship(final BdioNode node, final BdioNode child) {
        final BdioRelationship singleRelationship = new BdioRelationship();
        singleRelationship.setRelated(child.getId());
        singleRelationship.setRelationshipType("DYNAMIC_LINK");
        node.addRelationship(singleRelationship);
    }

    public BdioExternalIdentifier createExternalIdentifier(final String externalSystemTypeId, final String externalId) {
        final BdioExternalIdentifier externalIdentifier = new BdioExternalIdentifier();
        externalIdentifier.setExternalId(externalId);
        externalIdentifier.setExternalSystemTypeId(externalSystemTypeId);
        return externalIdentifier;
    }

}
