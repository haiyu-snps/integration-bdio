package com.synopsys.integration.bdio.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class DependencyGraphTestUtil {

    /**
     * @deprecated (use assertGraphDirectDependencies instead)
     */
    @Deprecated
    public static void assertGraphRootChildren(DependencyGraph graph, Dependency... dependencies) {
        assertGraphDirectDependencies(graph, dependencies);
    }

    public static void assertGraphDirectDependencies(DependencyGraph graph, Dependency... dependencies) {
        assertDependencySet(graph.getDirectDependencies(), dependencies);
    }

    public static void assertGraphChildren(DependencyGraph graph, Dependency node, Dependency... dependencies) {
        Set<Dependency> actualChildren = new HashSet<>(graph.getChildrenForParent(node));
        assertDependencySet(actualChildren, dependencies);
        assertExternalIdSet(graph.getChildrenExternalIdsForParent(node.getExternalId()), extractExternalIds(dependencies));
        assertExternalIdSet(graph.getChildrenExternalIdsForParent(node), extractExternalIds(dependencies));
    }

    public static void assertGraphParents(DependencyGraph graph, Dependency node, Dependency... dependencies) {
        Set<Dependency> actualParents = new HashSet<>(graph.getParentsForChild(node));
        assertDependencySet(actualParents, dependencies);
        assertExternalIdSet(graph.getParentExternalIdsForChild(node.getExternalId()), extractExternalIds(dependencies));
        assertExternalIdSet(graph.getParentExternalIdsForChild(node), extractExternalIds(dependencies));
    }

    public static Set<ExternalId> extractExternalIds(Set<Dependency> dependencies) {
        Set<ExternalId> ids = new HashSet<>();
        for (Dependency dependency : dependencies) {
            ids.add(dependency.getExternalId());
        }
        return ids;
    }

    public static Set<ExternalId> extractExternalIds(Dependency... dependencies) {
        Set<ExternalId> ids = new HashSet<>();
        for (Dependency dependency : dependencies) {
            ids.add(dependency.getExternalId());
        }
        return ids;
    }

    public static void assertGraphHas(DependencyGraph graph, Dependency... dependencies) {
        for (Dependency dependency : dependencies) {
            assertTrue(graph.hasDependency(dependency));
            assertTrue(graph.hasDependency(dependency.getExternalId()));
            assertEquals(graph.getDependency(dependency.getExternalId()), dependency);
        }
    }

    public static void assertDependencySet(Set<Dependency> actualDependencies, Dependency... dependencies) {
        Set<Dependency> expectedDependencies = new HashSet<>(Arrays.asList(dependencies));
        assertDependencySet(actualDependencies, expectedDependencies);
    }

    public static void assertDependencySet(Set<Dependency> actualDependencies, Set<Dependency> expectedDependencies) {
        assertEquals(expectedDependencies, actualDependencies);
        assertEquals(actualDependencies.size(), expectedDependencies.size(), "Expected graph children size to equal given children size.");

        Set<Dependency> missingExpected = new HashSet<>(expectedDependencies);
        Set<Dependency> extraActual = new HashSet<>(actualDependencies);
        missingExpected.removeAll(actualDependencies);
        extraActual.removeAll(expectedDependencies);

        assertEquals(0, missingExpected.size(), "Expected graph not to have extra dependencies.");
        assertEquals(0, extraActual.size(), "Expected graph not to be missing dependencies.");
    }

    public static void assertExternalIdSet(Set<ExternalId> actualDependencies, Set<ExternalId> expectedDependencies) {
        assertEquals(actualDependencies.size(), expectedDependencies.size(), "Expected graph children size to equal given children size.");

        Set<ExternalId> missingExpected = new HashSet<>(expectedDependencies);
        Set<ExternalId> extraActual = new HashSet<>(actualDependencies);
        missingExpected.removeAll(actualDependencies);
        extraActual.removeAll(expectedDependencies);

        assertEquals(0, missingExpected.size(), "Expected graph not to have extra dependencies.");
        assertEquals(0, extraActual.size(), "Expected graph not to be missing dependencies.");
    }

}
