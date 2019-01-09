/**
 * integration-bdio
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.bdio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.model.BdioBillOfMaterials;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioCreationInfo;
import com.synopsys.integration.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.SpdxCreator;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class BdioNodeFactory {
    private final BdioPropertyHelper bdioPropertyHelper;

    public BdioNodeFactory(final BdioPropertyHelper bdioPropertyHelper) {
        this.bdioPropertyHelper = bdioPropertyHelper;
    }

    public BdioBillOfMaterials createBillOfMaterials(final String codeLocationName, final String projectName, final String projectVersion) {
        final BdioBillOfMaterials billOfMaterials = new BdioBillOfMaterials();
        billOfMaterials.id = String.format("uuid:%s", UUID.randomUUID());
        if (StringUtils.isNotBlank(codeLocationName)) {
            billOfMaterials.spdxName = codeLocationName;
        } else {
            billOfMaterials.spdxName = String.format("%s/%s Black Duck I/O Export", projectName, projectVersion);
        }
        billOfMaterials.bdioSpecificationVersion = "1.1.0";

        billOfMaterials.creationInfo = new BdioCreationInfo();
        billOfMaterials.creationInfo.created = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        String version = "UnknownVersion";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("com/blackducksoftware/integration/bdio/version.txt")) {
            version = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (final IOException e) {
        }
        billOfMaterials.creationInfo.addSpdxCreator(SpdxCreator.createToolSpdxCreator("IntegrationBdio", version));

        return billOfMaterials;
    }

    public BdioProject createProject(final String projectName, final String projectVersion, final String bdioId, final ExternalId externalId) {
        final BdioExternalIdentifier externalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        return createProject(projectName, projectVersion, bdioId, externalIdentifier);
    }

    public BdioProject createProject(final String projectName, final String projectVersion, final String bdioId, final BdioExternalIdentifier externalIdentifier) {
        final BdioProject project = new BdioProject();
        project.id = bdioId;
        project.name = projectName;
        project.version = projectVersion;
        project.bdioExternalIdentifier = externalIdentifier;

        return project;
    }

    public BdioComponent createComponent(final String componentName, final String componentVersion, final ExternalId externalId) {
        final BdioExternalIdentifier externalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        return createComponent(componentName, componentVersion, externalId.createBdioId(), externalIdentifier);
    }

    public BdioComponent createComponent(final String componentName, final String componentVersion, final String bdioId, final BdioExternalIdentifier externalIdentifier) {
        final BdioComponent component = new BdioComponent();
        component.id = bdioId;
        component.name = componentName;
        component.version = componentVersion;
        component.bdioExternalIdentifier = externalIdentifier;

        return component;
    }

}
