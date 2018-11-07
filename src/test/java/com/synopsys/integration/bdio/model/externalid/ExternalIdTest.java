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
package com.synopsys.integration.bdio.model.externalid;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.model.Forge;

public class ExternalIdTest {
    private final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

    @Test
    public void testForgeEquality() {
        assertEquals(Forge.ANACONDA, new Forge("=", "/", "anaconda"));
    }

    @Test
    public void testCreatingExternalIds() {
        final ExternalId architectureExternalId = simpleBdioFactory.createArchitectureExternalId(Forge.CENTOS, "name", "version", "architecture");
        assertEquals("http:centos/name/version/architecture", architectureExternalId.createBdioId());
        assertEquals("name/version/architecture", architectureExternalId.createExternalId());

        final ExternalId mavenExternalId = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        assertEquals("http:maven/group/artifact/version", mavenExternalId.createBdioId());
        assertEquals("group:artifact:version", mavenExternalId.createExternalId());

        final ExternalId moduleNamesExternalId = simpleBdioFactory.createModuleNamesExternalId(Forge.CPAN, "name", "version", "something", "else");
        assertEquals("http:cpan/name/version/something/else", moduleNamesExternalId.createBdioId());
        assertEquals("name-version-something-else", moduleNamesExternalId.createExternalId());

        final ExternalId nameVersionExternalId = simpleBdioFactory.createNameVersionExternalId(Forge.PYPI, "name", "version");
        assertEquals("http:pypi/name/version", nameVersionExternalId.createBdioId());
        assertEquals("name/version", nameVersionExternalId.createExternalId());

        final ExternalId pathExternalId = simpleBdioFactory.createPathExternalId(Forge.GOGET, "name");
        assertEquals("http:goget/name", pathExternalId.createBdioId());
        assertEquals("name", pathExternalId.createExternalId());
    }

    @Test
    public void testEscapingBadUriCharacters() {
        final ExternalId nameVersionExternalId = simpleBdioFactory.createNameVersionExternalId(Forge.NPM, "name with spaces", "version with a - and a # and spaces");
        assertEquals("http:npm/name_with_spaces/version_with_a___and_a___and_spaces", nameVersionExternalId.createBdioId());
        assertEquals("name with spaces@version with a - and a # and spaces", nameVersionExternalId.createExternalId());
        assertEquals("name with spaces/version with a - and a # and spaces", nameVersionExternalId.createBlackDuckOriginId());
    }

    @Test
    public void testWithoutEnoughState() {
        final ExternalId externalId = new ExternalId(Forge.MAVEN);
        externalId.name = "bundler";
        assertEquals(new String[] { "bundler" }, externalId.getExternalIdPieces());
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
        final ExternalId externalIdA = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        final ExternalId externalIdB = simpleBdioFactory.createMavenExternalId("group", "artifact", "version");
        assertEquals(externalIdA, externalIdB);
        assertEquals(externalIdA.hashCode(), externalIdB.hashCode());
        assertEquals(externalIdA.toString(), externalIdB.toString());
    }

}
