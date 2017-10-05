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
package com.blackducksoftware.integration.hub.bdio.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;

public class DependencyGraphTestUtil {
    public static void assertGraphRootChildren(final DependencyGraph graph, final Dependency... dependencies) {
        assertDependencySet(graph.getRootDependencies(), dependencies);
    }

    public static void assertGraphChildren(final DependencyGraph graph, final Dependency node, final Dependency... dependencies) {
        final Set<Dependency> actualChildren = new HashSet<>(graph.getChildrenForParent(node));
        assertDependencySet(actualChildren, dependencies);
        assertExternalIdSet(graph.getChildrenExternalIdsForParent(node.externalId), extractExternalIds(dependencies));
        assertExternalIdSet(graph.getChildrenExternalIdsForParent(node), extractExternalIds(dependencies));
    }

    public static void assertGraphParents(final DependencyGraph graph, final Dependency node, final Dependency... dependencies) {
        final Set<Dependency> actualParents = new HashSet<>(graph.getParentsForChild(node));
        assertDependencySet(actualParents, dependencies);
        assertExternalIdSet(graph.getParentExternalIdsForChild(node.externalId), extractExternalIds(dependencies));
        assertExternalIdSet(graph.getParentExternalIdsForChild(node), extractExternalIds(dependencies));
    }

    public static Set<ExternalId> extractExternalIds(final Set<Dependency> dependencies) {
        final Set<ExternalId> ids = new HashSet<>();
        for (final Dependency dependency : dependencies) {
            ids.add(dependency.externalId);
        }
        return ids;
    }

    public static Set<ExternalId> extractExternalIds(final Dependency... dependencies) {
        final Set<ExternalId> ids = new HashSet<>();
        for (final Dependency dependency : dependencies) {
            ids.add(dependency.externalId);
        }
        return ids;
    }

    public static void assertGraphHas(final DependencyGraph graph, final Dependency... dependencies) {
        for (final Dependency dependency : dependencies) {
            assertTrue(graph.hasDependency(dependency));
            assertTrue(graph.hasDependency(dependency.externalId));
            assertEquals(graph.getDependency(dependency.externalId), dependency);
        }
    }

    public static void assertDependencySet(final Set<Dependency> actualDependencies, final Dependency... dependencies) {
        final Set<Dependency> expectedDependencies = new HashSet<>();
        for (final Dependency dependency : dependencies) {
            expectedDependencies.add(dependency);
        }
        assertDependencySet(actualDependencies, expectedDependencies);
    }

    public static void assertDependencySet(final Set<Dependency> actualDependencies, final Set<Dependency> expectedDependencies) {
        assertEquals("Expected graph children size to equal given children size.", actualDependencies.size(), expectedDependencies.size());

        final Set<Dependency> missingExpected = new HashSet<>(expectedDependencies);
        final Set<Dependency> extraActual = new HashSet<>(actualDependencies);
        missingExpected.removeAll(actualDependencies);
        extraActual.removeAll(expectedDependencies);

        assertEquals("Expected graph not to have extra dependencies.", missingExpected.size(), 0);
        assertEquals("Expected graph not to be missing dependencies.", extraActual.size(), 0);
    }

    public static void assertExternalIdSet(final Set<ExternalId> actualDependencies, final Set<ExternalId> expectedDependencies) {
        assertEquals("Expected graph children size to equal given children size.", actualDependencies.size(), expectedDependencies.size());

        final Set<ExternalId> missingExpected = new HashSet<>(expectedDependencies);
        final Set<ExternalId> extraActual = new HashSet<>(actualDependencies);
        missingExpected.removeAll(actualDependencies);
        extraActual.removeAll(expectedDependencies);

        assertEquals("Expected graph not to have extra dependencies.", missingExpected.size(), 0);
        assertEquals("Expected graph not to be missing dependencies.", extraActual.size(), 0);
    }
}
