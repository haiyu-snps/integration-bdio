package com.synopsys.integration.bdio.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class DependencyGraphTestUtil {
    public static void assertGraphRootChildren(final DependencyGraph graph, final Dependency... dependencies) {
        assertDependencySet(graph.getRootDependencies(), dependencies);
    }

    public static void assertGraphChildren(final DependencyGraph graph, final Dependency node, final Dependency... dependencies) {
        final Set<Dependency> actualChildren = new HashSet<>(graph.getChildrenForParent(node));
        assertDependencySet(actualChildren, dependencies);
        assertExternalIdSet(graph.getChildrenExternalIdsForParent(node.getExternalId()), extractExternalIds(dependencies));
        assertExternalIdSet(graph.getChildrenExternalIdsForParent(node), extractExternalIds(dependencies));
    }

    public static void assertGraphParents(final DependencyGraph graph, final Dependency node, final Dependency... dependencies) {
        final Set<Dependency> actualParents = new HashSet<>(graph.getParentsForChild(node));
        assertDependencySet(actualParents, dependencies);
        assertExternalIdSet(graph.getParentExternalIdsForChild(node.getExternalId()), extractExternalIds(dependencies));
        assertExternalIdSet(graph.getParentExternalIdsForChild(node), extractExternalIds(dependencies));
    }

    public static Set<ExternalId> extractExternalIds(final Set<Dependency> dependencies) {
        final Set<ExternalId> ids = new HashSet<>();
        for (final Dependency dependency : dependencies) {
            ids.add(dependency.getExternalId());
        }
        return ids;
    }

    public static Set<ExternalId> extractExternalIds(final Dependency... dependencies) {
        final Set<ExternalId> ids = new HashSet<>();
        for (final Dependency dependency : dependencies) {
            ids.add(dependency.getExternalId());
        }
        return ids;
    }

    public static void assertGraphHas(final DependencyGraph graph, final Dependency... dependencies) {
        for (final Dependency dependency : dependencies) {
            assertTrue(graph.hasDependency(dependency));
            assertTrue(graph.hasDependency(dependency.getExternalId()));
            assertEquals(graph.getDependency(dependency.getExternalId()), dependency);
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

        assertEquals("Expected graph not to have extra dependencies.", 0, missingExpected.size());
        assertEquals("Expected graph not to be missing dependencies.", 0, extraActual.size());
    }

    public static void assertExternalIdSet(final Set<ExternalId> actualDependencies, final Set<ExternalId> expectedDependencies) {
        assertEquals("Expected graph children size to equal given children size.", actualDependencies.size(), expectedDependencies.size());

        final Set<ExternalId> missingExpected = new HashSet<>(expectedDependencies);
        final Set<ExternalId> extraActual = new HashSet<>(actualDependencies);
        missingExpected.removeAll(actualDependencies);
        extraActual.removeAll(expectedDependencies);

        assertEquals("Expected graph not to have extra dependencies.", 0, missingExpected.size());
        assertEquals("Expected graph not to be missing dependencies.", 0, extraActual.size());
    }
}
