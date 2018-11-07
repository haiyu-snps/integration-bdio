/**
 * Integration Bdio
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
package com.synopsys.integration.bdio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.BdioExternalIdentifier;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class BdioPropertyHelperTest {
    private final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    public void testCreatingMavenExternalIds() {
        final ExternalId externalId = externalIdFactory.createMavenExternalId("group", "artifact", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("maven", actualExternalIdentifier.forge);
        assertEquals("group:artifact:version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNpmExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("npm", actualExternalIdentifier.forge);
        assertEquals("name@version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNugetExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NUGET, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("nuget", actualExternalIdentifier.forge);
        assertEquals("name/version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingPypiExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("pypi", actualExternalIdentifier.forge);
        assertEquals("name/version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingRubygemsExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("rubygems", actualExternalIdentifier.forge);
        assertEquals("name=version", actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingCocoapodsExternalIds() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.COCOAPODS, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals("cocoapods", actualExternalIdentifier.forge);
        assertEquals("name:version", actualExternalIdentifier.externalId);
    }

}
