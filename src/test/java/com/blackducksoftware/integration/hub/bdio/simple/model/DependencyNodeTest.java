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
package com.blackducksoftware.integration.hub.bdio.simple.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalIdFactory;

public class DependencyNodeTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    public void testConstructingDependencyNode() {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, "name", "version");
        final DependencyNode dependencyNode = new DependencyNode(externalId);
        assertEquals("npm", dependencyNode.externalId.forge.toString(), "npm");
        assertEquals("http:npm/name/version", dependencyNode.externalId.createBdioId());
        assertEquals("name@version", dependencyNode.externalId.createExternalId());
    }

    @Test
    public void testBoilerplateCode() {
        final DependencyNode nodeA = new DependencyNode((String) null, (String) null, (ExternalId) null);
        final DependencyNode nodeB = new DependencyNode((String) null, (String) null, (ExternalId) null);
        assertEquals(nodeA, nodeB);
        assertEquals(nodeA.hashCode(), nodeB.hashCode());
        assertEquals(nodeA.toString(), nodeB.toString());
    }

}
