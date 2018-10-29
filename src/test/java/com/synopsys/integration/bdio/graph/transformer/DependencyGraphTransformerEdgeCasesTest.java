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
package com.synopsys.integration.bdio.graph.transformer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;
import org.junit.Test;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyGraphTransformerEdgeCasesTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    private Dependency node(final String name, final String version, final String group) {
        final ExternalId projectExternalId = externalIdFactory.createMavenExternalId(group, name, version);
        final Dependency root = new Dependency(name, version, projectExternalId);
        return root;
    }

    @Test(timeout = 30000)
    public void testTransformingExpensiveRecursiveTree() throws URISyntaxException, IOException, JSONException {
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

        final HashSet<Dependency> generated = new HashSet<>();
        final HashMap<Dependency, Integer> counts = new HashMap<>();

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        for (int i = 0; i < 200; i++) {
            final String name = "node" + i;
            final Dependency next = node(name, name, name);
            for (final Dependency parent : generated) {
                counts.put(parent, counts.get(parent) + 1);
                graph.addParentWithChild(parent, next);
            }
            generated.add(next);
            counts.put(next, 0);
            if (i == 0) {
                graph.addChildToRoot(next);
            }
        }

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "dumb", "dumbVer");
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("dumb", "dumbVer", id, graph);
        simpleBdioDocument.billOfMaterials.id = "uuid:123";

        int found = 0;
        for (final BdioComponent component : simpleBdioDocument.components) {
            Dependency node = null;
            for (final Dependency candidate : generated) {
                if (component.name == candidate.name) {
                    assertEquals(null, node);
                    node = candidate;
                    found++;
                }
            }

            assertEquals(counts.get(node).intValue(), component.relationships.size());
        }
        assertEquals(generated.size(), found);
    }

    @Test
    public void testTransformingBrokenTree() throws URISyntaxException, IOException, JSONException {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        final Dependency childOne = node("one", "one", "one");
        final Dependency childTwo = node("two", "two", "two");

        final Dependency sharedLeft = node("shared", "shared", "shared");
        final Dependency sharedRight = node("shared", "shared", "shared");

        graph.addParentWithChild(sharedLeft, childOne);
        graph.addParentWithChild(sharedRight, childTwo);

        final Dependency parentLeft = node("parentLeft", "parentLeft", "parentLeft");
        final Dependency parentRight = node("parentRight", "parentRight", "parentRight");

        graph.addParentWithChild(parentLeft, sharedLeft);
        graph.addParentWithChild(parentRight, sharedRight);

        graph.addChildrenToRoot(parentLeft, parentRight);

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "dumb", "dumbVer");
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("dumb", "dumbVer", id, graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.billOfMaterials.id = "uuid:123";

        for (final BdioComponent component : simpleBdioDocument.components) {
            if (component.name == "shared") {
                assertEquals(component.relationships.size(), 2);
            }
        }

    }

    @Test
    public void testTransformingBrokenTreeLeftHasNodeRightEmpty() throws URISyntaxException, IOException, JSONException {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        final Dependency childOne = node("one", "one", "one");

        final Dependency sharedLeft = node("shared", "shared", "shared");
        final Dependency sharedRight = node("shared", "shared", "shared");

        graph.addParentWithChild(sharedLeft, childOne);

        final Dependency parentLeft = node("parentLeft", "parentLeft", "parentLeft");
        final Dependency parentRight = node("parentRight", "parentRight", "parentRight");

        graph.addParentWithChild(parentLeft, sharedLeft);
        graph.addParentWithChild(parentRight, sharedRight);

        graph.addChildrenToRoot(parentLeft, parentRight);

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "dumb", "dumbVer");
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("dumb", "dumbVer", id, graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.billOfMaterials.id = "uuid:123";

        boolean found = false;
        for (final BdioComponent component : simpleBdioDocument.components) {
            if (component.name == "shared") {
                assertEquals(1, component.relationships.size());
            } else if (component.name == "one") {
                found = true;
            }
        }
        assertEquals(true, found);
    }

    @Test
    public void testTransformingBrokenTreeLeftEmpty() throws URISyntaxException, IOException, JSONException {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        final Dependency childOne = node("one", "one", "one");

        final Dependency sharedLeft = node("shared", "shared", "shared");
        final Dependency sharedRight = node("shared", "shared", "shared");

        graph.addParentWithChild(sharedRight, childOne);

        final Dependency parentLeft = node("parentLeft", "parentLeft", "parentLeft");
        final Dependency parentRight = node("parentRight", "parentRight", "parentRight");

        graph.addParentWithChild(parentLeft, sharedLeft);
        graph.addParentWithChild(parentRight, sharedRight);

        graph.addChildrenToRoot(parentLeft, parentRight);

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "dumb", "dumbVer");
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("dumb", "dumbVer", id, graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.billOfMaterials.id = "uuid:123";

        boolean found = false;
        for (final BdioComponent component : simpleBdioDocument.components) {
            if (component.name == "shared") {
                assertEquals(1, component.relationships.size());
            } else if (component.name == "one") {
                found = true;
            }
        }
        assertEquals(true, found);
    }

    @Test
    public void testCyclic() throws URISyntaxException, IOException, JSONException {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        final Dependency one = node("one", "one", "one");
        final Dependency two = node("two", "two", "two");
        final Dependency three = node("three", "three", "three");
        final Dependency project = node("project", "project", "project");

        graph.addParentWithChild(one, three);
        graph.addParentWithChild(two, one);
        graph.addParentWithChild(three, two);

        graph.addChildrenToRoot(one);

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        final SimpleBdioDocument simpleBdioDocumentRecursive = simpleBdioFactory.createSimpleBdioDocument(project.name, project.version, project.externalId, graph);

        assertEquals(simpleBdioDocumentRecursive.components.size(), 3);
    }

    @Test
    public void testProjectAsChild() throws URISyntaxException, IOException, JSONException {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        final Dependency one = node("one", "one", "one");
        final Dependency two = node("two", "two", "two");
        final Dependency project = node("project", "project", "project");

        graph.addParentWithChild(one, two);
        graph.addParentWithChild(project, two);

        graph.addChildrenToRoot(one);
        graph.addChildrenToRoot(project);

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        final SimpleBdioDocument simpleBdioDocumentRecursive = simpleBdioFactory.createSimpleBdioDocument(project.name, project.version, project.externalId, graph);

        assertEquals(simpleBdioDocumentRecursive.components.size(), 2);
    }

}
