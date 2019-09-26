package com.synopsys.integration.bdio.graph;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

public class MutableMapDependencyGraphTest {

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

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addChildWithParent(child1, parent1);
        graph.addChildWithParents(grandchild2, parent1, child1);
        graph.addChildWithParents(child2, DependencyTestUtil.asSet(parent2, child1));
        graph.addChildWithParents(child3, DependencyTestUtil.asList(parent3));
        graph.addChildrenToRoot(parent1, parent2, parent3);

        DependencyGraphTestUtil.assertGraphChildren(graph, parent1, child1, grandchild2);

        DependencyGraphTestUtil.assertGraphChildren(graph, child1, child2, grandchild2);
        DependencyGraphTestUtil.assertGraphChildren(graph, parent3, child3);
        DependencyGraphTestUtil.assertGraphChildren(graph, parent2, child2);

        DependencyGraphTestUtil.assertGraphParents(graph, child1, parent1);
        DependencyGraphTestUtil.assertGraphParents(graph, grandchild2, parent1, child1);
        DependencyGraphTestUtil.assertGraphParents(graph, child2, parent2, child1);
        DependencyGraphTestUtil.assertGraphParents(graph, child3, parent3);

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1, parent2, parent3);
    }

    @Test
    public void testRootAdd() {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        graph.addChildToRoot(parent1);
        graph.addChildrenToRoot(parent2, parent3);
        graph.addChildrenToRoot(DependencyTestUtil.asSet(child1, child2));
        graph.addChildrenToRoot(DependencyTestUtil.asList(child3, child4));

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1, parent2, parent3, child1, child2, child3, child4);
    }

    @Test
    public void testAddParentsWithChildren() {

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChild(parent1, child1);
        graph.addParentWithChildren(child1, DependencyTestUtil.asSet(grandchild1, grandchild2));
        graph.addParentWithChildren(parent2, DependencyTestUtil.asList(child2, child3));
        graph.addChildrenToRoot(parent1, parent2);

        DependencyGraphTestUtil.assertGraphChildren(graph, parent1, child1);
        DependencyGraphTestUtil.assertGraphChildren(graph, parent2, child2, child3);
        DependencyGraphTestUtil.assertGraphChildren(graph, child1, grandchild1, grandchild2);

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1, parent2);
    }

    @Test
    public void testAddToSameParent() {

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addParentWithChildren(parent1, child2);
        graph.addChildrenToRoot(parent1);

        DependencyGraphTestUtil.assertGraphChildren(graph, parent1, child1, child2);
        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1);
    }

    @Test
    public void testHas() {

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addChildrenToRoot(parent1);

        DependencyGraphTestUtil.assertGraphHas(graph, child1, parent1);
    }

    @Test
    public void testDoesNotHas() {

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addChildrenToRoot(parent1);

        assertNull(graph.getDependency(parent2.getExternalId()));
        assertFalse(graph.hasDependency(parent2));

        assertNull(graph.getDependency(child2.getExternalId()));
        assertFalse(graph.hasDependency(child2));

    }

}
