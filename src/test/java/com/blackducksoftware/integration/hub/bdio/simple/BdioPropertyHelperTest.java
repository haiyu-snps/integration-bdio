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

public class BdioPropertyHelperTest {
    private final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();

    @Test
    public void testCreatingBdioId() {
        assertEquals("data:name/version", bdioPropertyHelper.createBdioId("name", "version"));
        assertEquals("data:group/artifact/version", bdioPropertyHelper.createBdioId("group", "artifact", "version"));
    }

    @Test
    public void testCreatingMavenExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createMavenExternalIdentifier("group", "artifact", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("maven", "group:artifact:version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNpmExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createNpmExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("npm", "name@version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingNugetExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createNugetExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("nuget", "name/version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingPypiExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createPypiExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("pypi", "name/version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingRubygemsExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createRubygemsExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("rubygems", "name=version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

    @Test
    public void testCreatingCocoapodsExternalIds() {
        final BdioExternalIdentifier actualExternalIdentifier = bdioPropertyHelper.createCocoapodsExternalIdentifier("name", "version");
        final BdioExternalIdentifier expectedExternalIdentifier = bdioPropertyHelper.createExternalIdentifier("cocoapods", "name:version");
        assertEquals(expectedExternalIdentifier.forge, actualExternalIdentifier.forge);
        assertEquals(expectedExternalIdentifier.externalId, actualExternalIdentifier.externalId);
    }

}
