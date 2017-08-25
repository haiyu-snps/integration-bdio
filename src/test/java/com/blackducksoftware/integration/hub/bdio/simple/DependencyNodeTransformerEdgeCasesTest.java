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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.BdioComponent;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;

public class DependencyNodeTransformerEdgeCasesTest {
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    private DependencyNode node(final String name, final String version, final String group, Set<DependencyNode> dependencies) {
        if (dependencies == null) {
            dependencies = new HashSet<>();
        }
        final ExternalId projectExternalId = new MavenExternalId(Forge.MAVEN, group, name, version);
        final DependencyNode root = new DependencyNode(name, version, projectExternalId, dependencies);
        return root;
    }

    private HashSet<DependencyNode> set(final DependencyNode... nodes) {
        final HashSet<DependencyNode> set = new HashSet<>();
        if (nodes != null) {
            for (final DependencyNode node : nodes) {
                if (node != null) {
                    set.add(node);
                }
            }
        }
        return set;
    }

    @Test(timeout = 30000)
    public void testTransformingExpensiveResursive() throws URISyntaxException, IOException, JSONException {
        // Here we generate a broad tree - for each new node, it becomes a child of all previous nodes
        // lets do it for [A,B,C,D]
        // after B we have A->B
        // after C we have A->C and A->B and B->C
        // after D we have A->D and A->C and C->D and B->D and A->B
        // so the final tree is
        // ..........A
        // ......../.|.\
        // .......D..C..B __
        // ..........|...\..\
        // ..........D....C..D
        // ...............|
        // ...............D
        // And it only gets wider from there.

        final HashSet<DependencyNode> generated = new HashSet<>();
        DependencyNode root = null;
        for (int i = 0; i < 200; i++) {
            final String name = "node" + i;
            final DependencyNode next = node(name, name, name, null);
            for (final DependencyNode parent : generated) {
                parent.children.add(next);
            }
            generated.add(next);
            if (i == 0) {
                root = next;
            }
        }

        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyNodeTransformer dependencyNodeTransformer = new DependencyNodeTransformer(bdioNodeFactory, bdioPropertyHelper);

        final SimpleBdioDocument simpleBdioDocument = dependencyNodeTransformer.transformDependencyNode(root);
        simpleBdioDocument.billOfMaterials.id = "uuid:123";

    }

    @Ignore // Currently fails as it does not reconcile a broken tree TODO: Reconcile broken tree!
    @Test
    public void testTransformingBrokenTree() throws URISyntaxException, IOException, JSONException {
        final DependencyNode childOne = node("one", "one", "one", null);
        final DependencyNode childTwo = node("two", "two", "two", null);

        final DependencyNode sharedLeft = node("shared", "shared", "shared", set(childOne));
        final DependencyNode sharedRight = node("shared", "shared", "shared", set(childTwo));

        final DependencyNode parentLeft = node("parentLeft", "parentLeft", "parentLeft", set(sharedLeft));
        final DependencyNode parentRight = node("parentRight", "parentRight", "parentRight", set(sharedRight));

        final DependencyNode root = node("root", "root", "root", set(parentLeft, parentRight));

        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyNodeTransformer dependencyNodeTransformer = new DependencyNodeTransformer(bdioNodeFactory, bdioPropertyHelper);
        final SimpleBdioDocument simpleBdioDocument = dependencyNodeTransformer.transformDependencyNode(root);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.billOfMaterials.id = "uuid:123";

        for (final BdioComponent component : simpleBdioDocument.components) {
            if (component.name == "shared") {
                assertEquals(component.relationships.size(), 2);
            }
        }

    }

}
