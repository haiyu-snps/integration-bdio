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
package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class ExternalIdTest {
    @Test
    public void testForgeEquality() {
        assertEquals(Forge.ANACONDA, new Forge("anaconda", "="));
    }

    @Test
    public void testCreatingExternalIds() {
        final ExternalId architectureExternalId = new ArchitectureExternalId(Forge.CENTOS, "name", "version", "architecture");
        assertEquals("http:centos/name/version/architecture", architectureExternalId.createDataId());
        assertEquals("name/version/architecture", architectureExternalId.createExternalId());

        final ExternalId mavenExternalId = new MavenExternalId("group", "artifact", "version");
        assertEquals("http:maven/group/artifact/version", mavenExternalId.createDataId());
        assertEquals("group:artifact:version", mavenExternalId.createExternalId());

        final ExternalId moduleNamesExternalId = new ModuleNamesExternalId(Forge.CPAN, "name", "version", "something", "else");
        assertEquals("http:cpan/name/version/something/else", moduleNamesExternalId.createDataId());
        assertEquals("name::version::something::else", moduleNamesExternalId.createExternalId());

        final ExternalId nameVersionExternalId = new NameVersionExternalId(Forge.PYPI, "name", "version");
        assertEquals("http:pypi/name/version", nameVersionExternalId.createDataId());
        assertEquals("name/version", nameVersionExternalId.createExternalId());

        final ExternalId pathExternalId = new PathExternalId(Forge.GOGET, "name");
        assertEquals("http:goget/name", pathExternalId.createDataId());
        assertEquals("name", pathExternalId.createExternalId());
    }

    @Test
    public void testEscapingBadUriCharacters() {
        final ExternalId nameVersionExternalId = new NameVersionExternalId(Forge.NPM, "name with spaces", "version with a - and a # and spaces");
        assertEquals("http:npm/name_with_spaces/version_with_a___and_a___and_spaces", nameVersionExternalId.createDataId());
        assertEquals("name with spaces@version with a - and a # and spaces", nameVersionExternalId.createExternalId());

    }

    @Test
    public void testBoilerplateCode() {
        final ExternalId externalIdA = new MavenExternalId(Forge.MAVEN, null, null, null);
        final ExternalId externalIdB = new MavenExternalId(Forge.MAVEN, null, null, null);
        assertEquals(externalIdA, externalIdB);
        assertEquals(externalIdA.hashCode(), externalIdB.hashCode());
        assertEquals(externalIdA.toString(), externalIdB.toString());
    }

}
