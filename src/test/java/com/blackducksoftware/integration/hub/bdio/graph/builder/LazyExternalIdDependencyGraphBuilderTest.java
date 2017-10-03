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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.DependencyId;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameDependencyId;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameVersionDependencyId;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.StringDependencyId;
import com.blackducksoftware.integration.hub.bdio.utility.DependencyGraphTestUtil;
import com.blackducksoftware.integration.hub.bdio.utility.DependencyIdTestUtil;
import com.blackducksoftware.integration.hub.bdio.utility.DependencyTestUtil;

public class LazyExternalIdDependencyGraphBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    DependencyId stringId = new StringDependencyId("id1");
    Dependency stringDep = DependencyTestUtil.newMavenDependency("test1", "test2", "org");
    DependencyId aliasId = new StringDependencyId("alias1");

    Dependency parent1 = DependencyTestUtil.newMavenDependency("parent1", "1.0", "parents");
    Dependency parent2 = DependencyTestUtil.newMavenDependency("parent2", "1.0", "parents");
    Dependency parent3 = DependencyTestUtil.newMavenDependency("parent3", "1.0", "parents");
    Dependency parent4 = DependencyTestUtil.newMavenDependency("parent4", "1.0", "parents");

    Dependency child1 = DependencyTestUtil.newMavenDependency("child1", "1.0", "childs");
    Dependency child2 = DependencyTestUtil.newMavenDependency("child2", "1.0", "childs");
    Dependency child3 = DependencyTestUtil.newMavenDependency("child3", "1.0", "childs");
    Dependency child4 = DependencyTestUtil.newMavenDependency("child4", "1.0", "childs");

    DependencyId parentId1 = new NameDependencyId("parent1");
    DependencyId parentId2 = new NameVersionDependencyId("parent2", "1.0");
    DependencyId parentId3 = new StringDependencyId("parent3");
    DependencyId parentId4 = new StringDependencyId("parent4");

    DependencyId childId1 = new NameDependencyId("child1");
    DependencyId childId2 = new NameDependencyId("child2");
    DependencyId childId3 = new NameDependencyId("child3");
    DependencyId childId4 = new NameDependencyId("child4");

    @Test
    public void testSetInfo() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(stringId);
        builder.setDependencyInfo(stringId, "test1", "test2", stringDep.externalId);

        final DependencyGraph graph = builder.build();

        final Dependency dep = graph.getDependency(stringDep.externalId);

        assertEquals("test1", dep.name);
        assertEquals("test2", dep.version);

        DependencyGraphTestUtil.assertGraphRootChildren(graph, stringDep);

    }

    @Test
    public void testAlias() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(aliasId);
        builder.setDependencyInfo(stringId, "test1", "test2", stringDep.externalId);
        builder.setDependencyAsAlias(stringId, aliasId);

        final DependencyGraph graph = builder.build();

        final Dependency dep = graph.getDependency(stringDep.externalId);

        assertEquals("test1", dep.name);
        assertEquals("test2", dep.version);

        DependencyGraphTestUtil.assertGraphRootChildren(graph, stringDep);

    }

    @Test
    public void testNoExternalId() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(aliasId);
        builder.setDependencyName(stringId, "test1");
        builder.setDependencyAsAlias(aliasId, stringId);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("A dependency in a relationship in the graph never had it's external id set.");

        builder.build();
    }

    @Test
    public void testNoExternalIdForChild() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(parentId1);
        builder.addChildWithParent(childId1, parentId1);
        builder.addChildWithParent(childId2, parentId1);

        builder.setDependencyInfo(parentId1, parent1.name, parent1.version, parent1.externalId);
        builder.setDependencyInfo(childId1, child1.name, child1.version, child1.externalId);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("A child dependency in a relationship in the graph never had it's external id set.");

        builder.build();
    }

    @Test
    public void testSetPieces() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(stringId);
        builder.setDependencyName(stringId, "test1");
        builder.setDependencyVersion(stringId, "test2");
        builder.setDependencyExternalId(stringId, stringDep.externalId);

        final DependencyGraph graph = builder.build();

        final Dependency dep = graph.getDependency(stringDep.externalId);

        assertEquals("test1", dep.name);
        assertEquals("test2", dep.version);

        DependencyGraphTestUtil.assertGraphRootChildren(graph, stringDep);
    }

    @Test
    public void testAddChild() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(parentId1);
        builder.addChildWithParent(childId1, parentId1);
        builder.addChildWithParents(childId2, parentId1);
        builder.addChildWithParents(childId3, DependencyIdTestUtil.asList(parentId1));
        builder.addChildWithParents(childId4, DependencyIdTestUtil.asSet(parentId1));

        builder.setDependencyInfo(parentId1, parent1.name, parent1.version, parent1.externalId);
        builder.setDependencyInfo(childId1, child1.name, child1.version, child1.externalId);
        builder.setDependencyInfo(childId2, child2.name, child2.version, child2.externalId);
        builder.setDependencyInfo(childId3, child3.name, child3.version, child3.externalId);
        builder.setDependencyInfo(childId4, child4.name, child4.version, child4.externalId);
        final DependencyGraph graph = builder.build();

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1);
        DependencyGraphTestUtil.assertGraphChildren(graph, parent1, child1, child2, child3, child4);
    }

    @Test
    public void testAddParent() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(parentId1);
        builder.addChildrenToRoot(parentId2);
        builder.addChildrenToRoot(DependencyIdTestUtil.asList(parentId3));
        builder.addChildrenToRoot(DependencyIdTestUtil.asSet(parentId4));

        builder.addParentWithChild(parentId1, childId1);
        builder.addParentWithChildren(parentId2, childId1);
        builder.addParentWithChildren(parentId3, DependencyIdTestUtil.asList(childId1));
        builder.addParentWithChildren(parentId4, DependencyIdTestUtil.asSet(childId1));

        builder.setDependencyInfo(parentId1, parent1.name, parent1.version, parent1.externalId);
        builder.setDependencyInfo(parentId2, parent2.name, parent2.version, parent2.externalId);
        builder.setDependencyInfo(parentId3, parent3.name, parent3.version, parent3.externalId);
        builder.setDependencyInfo(parentId4, parent4.name, parent4.version, parent4.externalId);
        builder.setDependencyInfo(childId1, child1.name, child1.version, child1.externalId);
        final DependencyGraph graph = builder.build();

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1, parent2, parent3, parent4);
        DependencyGraphTestUtil.assertGraphParents(graph, child1, parent1, parent2, parent3, parent4);
    }
}
