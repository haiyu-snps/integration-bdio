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
package com.blackducksoftware.integration.hub.bdio.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.utility.DependencyGraphTestUtil;
import com.blackducksoftware.integration.hub.bdio.utility.DependencyTestUtil;

public class DependencyGraphBuilderTest {

    Dependency parent1 = DependencyTestUtil.newMavenDependency("parent1", "1.0", "parents");
    Dependency parent2 = DependencyTestUtil.newMavenDependency("parent2", "1.0", "parents");
    Dependency parent3 = DependencyTestUtil.newMavenDependency("parent3", "1.0", "parents");
    Dependency parent4 = DependencyTestUtil.newMavenDependency("parent4", "1.0", "parents");

    Dependency child1 = DependencyTestUtil.newMavenDependency("child1", "1.0", "children");
    Dependency child2 = DependencyTestUtil.newMavenDependency("child2", "1.0", "children");
    Dependency child3 = DependencyTestUtil.newMavenDependency("child3", "1.0", "children");
    Dependency child4 = DependencyTestUtil.newMavenDependency("child4", "1.0", "children");

    Dependency grandchild1 = DependencyTestUtil.newMavenDependency("grandchild1", "1.0", "grandchildren");
    Dependency grandchild2 = DependencyTestUtil.newMavenDependency("grandchild2", "1.0", "grandchildren");
    Dependency grandchild3 = DependencyTestUtil.newMavenDependency("grandchild3", "1.0", "grandchildren");
    Dependency grandchild4 = DependencyTestUtil.newMavenDependency("grandchild4", "1.0", "grandchildren");

    @Test
    public void testAddChildWithParents() {

        final DependencyGraphBuilder builder = new DependencyGraphBuilder();
        builder.addChildWithParent(child1, parent1);
        builder.addChildWithParents(grandchild2, parent1, child1);
        builder.addChildWithParents(child2, DependencyTestUtil.asSet(parent2, child1));
        builder.addChildWithParents(child3, DependencyTestUtil.asList(parent3));
        builder.addChildrenToRoot(parent1, parent2, parent3);

        final DependencyGraph graph = builder.build();

        assertBuilderChildren(builder, graph, parent1, child1, grandchild2);

        assertBuilderChildren(builder, graph, child1, child2, grandchild2);
        assertBuilderChildren(builder, graph, parent3, child3);
        assertBuilderChildren(builder, graph, parent2, child2);

        assertBuilderParents(builder, graph, child1, parent1);
        assertBuilderParents(builder, graph, grandchild2, parent1, child1);
        assertBuilderParents(builder, graph, child2, parent2, child1);
        assertBuilderParents(builder, graph, child3, parent3);

        assertBuilderRootChildren(builder, graph, parent1, parent2, parent3);
    }

    @Test
    public void testRootAdd() {
        final DependencyGraphBuilder builder = new DependencyGraphBuilder();

        builder.addChildToRoot(parent1);
        builder.addChildrenToRoot(parent2, parent3);
        builder.addChildrenToRoot(DependencyTestUtil.asSet(child1, child2));
        builder.addChildrenToRoot(DependencyTestUtil.asList(child3, child4));

        final DependencyGraph graph = builder.build();

        assertBuilderRootChildren(builder, graph, parent1, parent2, parent3, child1, child2, child3, child4);
    }

    @Test
    public void testAddParentsWithChildren() {

        final DependencyGraphBuilder builder = new DependencyGraphBuilder();
        builder.addParentWithChild(parent1, child1);
        builder.addParentWithChildren(child1, DependencyTestUtil.asSet(grandchild1, grandchild2));
        builder.addParentWithChildren(parent2, DependencyTestUtil.asList(child2, child3));
        builder.addChildrenToRoot(parent1, parent2);

        final DependencyGraph graph = builder.build();

        assertBuilderChildren(builder, graph, parent1, child1);
        assertBuilderChildren(builder, graph, parent2, child2, child3);
        assertBuilderChildren(builder, graph, child1, grandchild1, grandchild2);

        assertBuilderRootChildren(builder, graph, parent1, parent2);
    }

    @Test
    public void testAddToSameParent() {

        final DependencyGraphBuilder builder = new DependencyGraphBuilder();
        builder.addParentWithChildren(parent1, child1);
        builder.addParentWithChildren(parent1, child2);
        builder.addChildrenToRoot(parent1);
        final DependencyGraph graph = builder.build();

        assertBuilderChildren(builder, graph, parent1, child1, child2);
        assertBuilderRootChildren(builder, graph, parent1);

    }

    @Test
    public void testHas() {

        final DependencyGraphBuilder builder = new DependencyGraphBuilder();
        builder.addParentWithChildren(parent1, child1);
        builder.addChildrenToRoot(parent1);

        final Set<Dependency> dependencies = new HashSet<>();
        dependencies.add(parent1);
        dependencies.add(child1);
        for (final Dependency dependency : dependencies) {
            assertTrue(builder.hasDependency(dependency));
            assertTrue(builder.hasDependency(dependency.externalId));
            assertEquals(builder.getDependency(dependency.externalId), dependency);
        }

        final DependencyGraph graph = builder.build();

        DependencyGraphTestUtil.assertGraphHas(graph, child1, parent1);
    }

    @Test
    public void testDoesNotHas() {

        final DependencyGraphBuilder builder = new DependencyGraphBuilder();
        builder.addParentWithChildren(parent1, child1);
        builder.addChildrenToRoot(parent1);

        assertNull(builder.getDependency(parent2.externalId));
        assertFalse(builder.hasDependency(parent2));

        assertNull(builder.getDependency(child2.externalId));
        assertFalse(builder.hasDependency(child2));

        final DependencyGraph graph = builder.build();

        assertNull(graph.getDependency(parent2.externalId));
        assertFalse(graph.hasDependency(parent2));

        assertNull(graph.getDependency(child2.externalId));
        assertFalse(graph.hasDependency(child2));

    }

    public static void assertBuilderRootChildren(final DependencyGraphBuilder builder, final DependencyGraph graph, final Dependency... dependencies) {
        DependencyGraphTestUtil.assertGraphRootChildren(graph, dependencies);
        DependencyGraphTestUtil.assertDependencySet(builder.getRootDependencies(), dependencies);
    }

    public static void assertBuilderChildren(final DependencyGraphBuilder builder, final DependencyGraph graph, final Dependency node, final Dependency... dependencies) {
        DependencyGraphTestUtil.assertGraphChildren(graph, node, dependencies);
        final Set<Dependency> actualChildren = new HashSet<>(builder.getChildrenForParent(node));
        DependencyGraphTestUtil.assertDependencySet(actualChildren, dependencies);
        DependencyGraphTestUtil.assertExternalIdSet(builder.getChildrenExternalIdsForParent(node.externalId), DependencyGraphTestUtil.extractExternalIds(dependencies));
        DependencyGraphTestUtil.assertExternalIdSet(builder.getChildrenExternalIdsForParent(node), DependencyGraphTestUtil.extractExternalIds(dependencies));
    }

    public static void assertBuilderParents(final DependencyGraphBuilder builder, final DependencyGraph graph, final Dependency node, final Dependency... dependencies) {
        DependencyGraphTestUtil.assertGraphParents(graph, node, dependencies);
        final Set<Dependency> actualParents = new HashSet<>(builder.getParentsForChild(node));
        DependencyGraphTestUtil.assertDependencySet(actualParents, dependencies);
        DependencyGraphTestUtil.assertExternalIdSet(builder.getParentExternalIdsForChild(node.externalId), DependencyGraphTestUtil.extractExternalIds(dependencies));
        DependencyGraphTestUtil.assertExternalIdSet(builder.getParentExternalIdsForChild(node), DependencyGraphTestUtil.extractExternalIds(dependencies));
    }

}
