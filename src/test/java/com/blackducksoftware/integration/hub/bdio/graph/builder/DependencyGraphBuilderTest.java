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
package com.blackducksoftware.integration.hub.bdio.graph.builder;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class DependencyGraphBuilderTest {
    // Dependency root = new Dependency("root", "1.0", externalIdFactory.createMavenExternalId("testRoot", "root", "1.0"));
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    Dependency firstChild = new Dependency("first", "1.0", externalIdFactory.createMavenExternalId("children", "first", "1.0"));

    Dependency secondChild = new Dependency("second", "2.0", externalIdFactory.createMavenExternalId("children", "second", "2.0"));

    Dependency thirdChild = new Dependency("third", "3.0", externalIdFactory.createMavenExternalId("children", "third", "3.0"));

    Dependency fourthChild = new Dependency("fourth", "4.0", externalIdFactory.createMavenExternalId("children", "fourth", "4.0"));

    Dependency subFirstChild = new Dependency("first", "1.0", externalIdFactory.createMavenExternalId("subChild", "first", "1.0"));

    Dependency subSecondChild = new Dependency("second", "2.0", externalIdFactory.createMavenExternalId("subChild", "second", "2.0"));

    Dependency subThirdChild = new Dependency("third", "3.0", externalIdFactory.createMavenExternalId("subChild", "third", "3.0"));

    private List<ExpectedDependencyTree> getExpected() {
        final List<ExpectedDependencyTree> trees = new ArrayList<>();

        final ExpectedDependencyTree firstChildTree = new ExpectedDependencyTree(firstChild.externalId);
        final ExpectedDependencyTree secondChildTree = new ExpectedDependencyTree(secondChild.externalId);
        final ExpectedDependencyTree thirdChildTree = new ExpectedDependencyTree(thirdChild.externalId);
        final ExpectedDependencyTree fourthChildTree = new ExpectedDependencyTree(fourthChild.externalId);
        final ExpectedDependencyTree subFirstChildTree = new ExpectedDependencyTree(subFirstChild.externalId);
        final ExpectedDependencyTree subSecondChildTree = new ExpectedDependencyTree(subSecondChild.externalId);
        final ExpectedDependencyTree subThirdChildTree = new ExpectedDependencyTree(subThirdChild.externalId);

        firstChildTree.addChildren(subFirstChildTree, subSecondChildTree);
        secondChildTree.addChildren(subThirdChildTree);
        fourthChildTree.addChildren(subFirstChildTree, subSecondChildTree, subThirdChildTree);

        trees.add(firstChildTree);
        trees.add(secondChildTree);
        trees.add(thirdChildTree);
        trees.add(fourthChildTree);

        return trees;
    }

    @Test
    public void testDependencyNodeBuilder() {

        // Adding the relationships randomly
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(secondChild, subThirdChild);
        // builder.addParentNodeWithChildren(secondChild, Arrays.asList(subThirdChild));
        graph.addChildWithParents(subSecondChild, fourthChild, firstChild);
        // builder.addChildNodeWithParents(subSecondChild, Arrays.asList(fourthChild, firstChild));
        graph.addChildWithParents(subFirstChild, fourthChild, firstChild);
        // builder.addChildNodeWithParents(subFirstChild, Arrays.asList(fourthChild, firstChild));
        graph.addChildrenToRoot(firstChild, secondChild, thirdChild, fourthChild);
        // builder.addParentNodeWithChildren(root, Arrays.asList(firstChild, secondChild, thirdChild, fourthChild));
        graph.addChildWithParents(subThirdChild, fourthChild);
        // builder.addChildNodeWithParents(subThirdChild, Arrays.asList(fourthChild));

        assertGraphMatches(graph, getExpected());
    }

    Dependency makeNode(final String org, final String mod, final String rev) {
        final ExternalId id = externalIdFactory.createMavenExternalId(org, mod, rev);
        final Dependency node = new Dependency(mod, rev, id);
        return node;
    }

    public class ExpectedDependencyTree {
        public ExternalId id;
        public List<ExpectedDependencyTree> children = new ArrayList<>();

        public ExpectedDependencyTree(final ExternalId id) {
            this.id = id;
        }

        public void addChildren(final ExpectedDependencyTree... children) {
            for (final ExpectedDependencyTree child : children) {
                this.children.add(child);
            }
        }
    }

    public void assertGraphMatches(final DependencyGraph graph, final ExpectedDependencyTree tree) {
        // assertEquals(tree.id, graph.getRoot().externalId);
        assertGraphHas(graph, tree);
    }

    public void assertGraphMatches(final DependencyGraph graph, final List<ExpectedDependencyTree> trees) {
        assertEquals(graph.getRootDependencies().size(), trees.size());
        for (final ExpectedDependencyTree tree : trees) {
            assertGraphHas(graph, tree);
        }
    }

    public void assertGraphHas(final DependencyGraph graph, final ExpectedDependencyTree tree) {

        assertEquals(tree.id, graph.getDependency(tree.id).externalId);

        final List<ExternalId> children = new ArrayList<>();
        children.addAll(graph.getChildrenExternalIdsForParent(tree.id));

        tree.children.sort(new ExpectedDependencyTreeComparator());
        children.sort(new ExternalIdComparator());

        assertEquals(tree.children.size(), children.size());
        for (int i = 0; i < tree.children.size(); i++) {
            assertEquals(tree.children.get(i).id, children.get(i));
            assertGraphHas(graph, tree.children.get(i));
        }

    }

    public static int sortExternalId(final ExternalId id1, final ExternalId id2) {
        return id1.createDataId().compareTo(id2.createDataId());
    }

    public class ExternalIdComparator implements Comparator<ExternalId> {
        @Override
        public int compare(final ExternalId id1, final ExternalId id2) {

            return id1.createDataId().compareTo(id2.createDataId());
        }
    }

    public class ExpectedDependencyTreeComparator implements Comparator<ExpectedDependencyTree> {
        @Override
        public int compare(final ExpectedDependencyTree id1, final ExpectedDependencyTree id2) {

            return id1.id.createDataId().compareTo(id2.id.createDataId());
        }
    }
}
