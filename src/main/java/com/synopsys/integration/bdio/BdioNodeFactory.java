/**
 * integration-bdio
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.SpdxCreator;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class BdioNodeFactory {
    public static final String UNKNOWN_LIBRARY_VERSION = "UnknownVersion";

    private static final String VERSION_RESOURCE_PATH = "com/synopsys/integration/bdio/version.txt";

    private final BdioPropertyHelper bdioPropertyHelper;

    public BdioNodeFactory(BdioPropertyHelper bdioPropertyHelper) {
        this.bdioPropertyHelper = bdioPropertyHelper;
    }

    public BdioBillOfMaterials createBillOfMaterials(String projectName, String projectVersion) {
        String codeLocationName = String.format("%s/%s Black Duck I/O Export", projectName, projectVersion);

        return createBillOfMaterials(codeLocationName, projectName, projectVersion);
    }

    public BdioBillOfMaterials createBillOfMaterials(String codeLocationName, String projectName, String projectVersion) {
        BdioBillOfMaterials billOfMaterials = new BdioBillOfMaterials();
        billOfMaterials.id = BdioId.createFromUUID(UUID.randomUUID().toString());
        if (StringUtils.isNotBlank(codeLocationName)) {
            billOfMaterials.spdxName = codeLocationName;
        } else {
            billOfMaterials.spdxName = String.format("%s/%s Black Duck I/O Export", projectName, projectVersion);
        }
        billOfMaterials.bdioSpecificationVersion = "1.1.0";

        billOfMaterials.creationInfo = new BdioCreationInfo();
        billOfMaterials.creationInfo.created = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        String version = BdioNodeFactory.UNKNOWN_LIBRARY_VERSION;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(BdioNodeFactory.VERSION_RESOURCE_PATH)) {
            if (inputStream != null) {
                version = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
            // Library version is not critical to BdioBillOfMaterials creation.
        }
        billOfMaterials.creationInfo.addSpdxCreator(SpdxCreator.createToolSpdxCreator("IntegrationBdio", version));

        return billOfMaterials;
    }

    public BdioProject createProject(String projectName, String projectVersion, BdioId bdioId, ExternalId externalId) {
        BdioExternalIdentifier externalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        return createProject(projectName, projectVersion, bdioId, externalIdentifier);
    }

    public BdioProject createProject(String projectName, String projectVersion, BdioId bdioId, BdioExternalIdentifier externalIdentifier) {
        BdioProject project = createProject(projectName, projectVersion, bdioId);
        project.bdioExternalIdentifier = externalIdentifier;

        return project;
    }

    public BdioProject createProject(String projectName, String projectVersion) {
        return createProject(projectName, projectVersion, BdioId.createFromPieces(projectName, projectVersion));
    }

    public BdioProject createProject(String projectName, String projectVersion, BdioId bdioId) {
        BdioProject project = new BdioProject();
        project.id = bdioId;
        project.name = projectName;
        project.version = projectVersion;

        return project;
    }

    public BdioComponent createComponent(String componentName, String componentVersion, ExternalId externalId) {
        BdioExternalIdentifier externalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        return createComponent(componentName, componentVersion, externalId.createBdioId(), externalIdentifier);
    }

    public BdioComponent createComponent(String componentName, String componentVersion, BdioId bdioId, BdioExternalIdentifier externalIdentifier) {
        BdioComponent component = new BdioComponent();
        component.id = bdioId;
        component.name = componentName;
        component.version = componentVersion;
        component.bdioExternalIdentifier = externalIdentifier;

        return component;
    }

}
