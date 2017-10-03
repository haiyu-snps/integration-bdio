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
package com.blackducksoftware.integration.hub.bdio.model.externalid;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.blackducksoftware.integration.hub.bdio.model.Forge;

public class ExternalIdTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    public void testForgeEquality() {
        assertEquals(Forge.ANACONDA, new Forge("anaconda", "="));
    }

    @Test
    public void testCreatingExternalIds() {
        final ExternalId architectureExternalId = externalIdFactory.createArchitectureExternalId(Forge.CENTOS, "name", "version", "architecture");
        assertEquals("http:centos/name/version/architecture", architectureExternalId.createBdioId());
        assertEquals("name/version/architecture", architectureExternalId.createExternalId());

        final ExternalId mavenExternalId = externalIdFactory.createMavenExternalId("group", "artifact", "version");
        assertEquals("http:maven/group/artifact/version", mavenExternalId.createBdioId());
        assertEquals("group:artifact:version", mavenExternalId.createExternalId());

        final ExternalId moduleNamesExternalId = externalIdFactory.createModuleNamesExternalId(Forge.CPAN, "name", "version", "something", "else");
        assertEquals("http:cpan/name/version/something/else", moduleNamesExternalId.createBdioId());
        assertEquals("name-version-something-else", moduleNamesExternalId.createExternalId());

        final ExternalId nameVersionExternalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, "name", "version");
        assertEquals("http:pypi/name/version", nameVersionExternalId.createBdioId());
        assertEquals("name/version", nameVersionExternalId.createExternalId());

        final ExternalId pathExternalId = externalIdFactory.createPathExternalId(Forge.GOGET, "name");
        assertEquals("http:goget/name", pathExternalId.createBdioId());
        assertEquals("name", pathExternalId.createExternalId());
    }

    @Test
    public void testEscapingBadUriCharacters() {
        final ExternalId nameVersionExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, "name with spaces", "version with a - and a # and spaces");
        assertEquals("http:npm/name_with_spaces/version_with_a___and_a___and_spaces", nameVersionExternalId.createBdioId());
        assertEquals("name with spaces@version with a - and a # and spaces", nameVersionExternalId.createExternalId());
    }

    @Test
    public void testExceptionWithoutEnoughState() {
        final ExternalId externalId = new ExternalId(Forge.MAVEN);
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Not enough state was populated:");
        externalId.getExternalIdPieces();
    }

    @Test
    @Deprecated
    public void testCreateDataId() {
        // this test should be removed once createDataId() is removed (obviously)
        final ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.name = "testName";
        externalId.version = "testVersion";
        assertEquals("http:maven/testName/testVersion", externalId.createBdioId());
    }

    @Test
    public void testBoilerplateCode() {
        final ExternalId externalIdA = externalIdFactory.createMavenExternalId("group", "artifact", "version");
        final ExternalId externalIdB = externalIdFactory.createMavenExternalId("group", "artifact", "version");
        assertEquals(externalIdA, externalIdB);
        assertEquals(externalIdA.hashCode(), externalIdB.hashCode());
        assertEquals(externalIdA.toString(), externalIdB.toString());
    }

}
