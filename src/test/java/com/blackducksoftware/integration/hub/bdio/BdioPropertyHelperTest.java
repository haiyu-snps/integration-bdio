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
package com.blackducksoftware.integration.hub.bdio.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.BdioExternalIdentifier;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalIdFactory;

public class BdioPropertyHelperTest {
    private final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    public void testCreatingMavenExternalIds() {
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("maven", "group:artifact:version");

        final ExternalId externalId = externalIdFactory.createMavenExternalId("group", "artifact", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNpmExternalIds() {
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("npm", "name@version");

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNugetExternalIds() {
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("nuget", "name/version");

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NUGET, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingPypiExternalIds() {
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("pypi", "name/version");

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingRubygemsExternalIds() {
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("rubygems", "name=version");

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingCocoapodsExternalIds() {
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("cocoapods", "name:version");

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.COCOAPODS, "name", "version");
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(externalId);
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

}
