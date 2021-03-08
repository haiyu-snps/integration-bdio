package com.synopsys.integration.bdio.graph;

import static com.synopsys.integration.bdio.utility.DependencyTestUtil.newMavenDependency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

public class MutableMapDependencyGraphTest {
    private final Dependency parent1 = newMavenDependency("parents:parent1:1.0");
    private final Dependency parent2 = newMavenDependency("parents:parent2:1.0");
    private final Dependency parent3 = newMavenDependency("parents:parent3:1.0");
    private final Dependency parent4 = newMavenDependency("parents:parent4:1.0");

    private final Dependency child1 = newMavenDependency("children:child1:1.0");
    private final Dependency child2 = newMavenDependency("children:child2:1.0");
    private final Dependency child3 = newMavenDependency("children:child3:1.0");
    private final Dependency child4 = newMavenDependency("children:child4:1.0");

    private final Dependency grandchild1 = newMavenDependency("grandchildren:grandchild1:1.0");
    private final Dependency grandchild2 = newMavenDependency("grandchildren:grandchild2:1.0");
    private final Dependency grandchild3 = newMavenDependency("grandchildren:grandchild3:1.0");
    private final Dependency grandchild4 = newMavenDependency("grandchildren:grandchild4:1.0");

    @Test
    public void testAddChildWithParents() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
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
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        graph.addChildToRoot(parent1);
        graph.addChildrenToRoot(parent2, parent3);
        graph.addChildrenToRoot(DependencyTestUtil.asSet(child1, child2));
        graph.addChildrenToRoot(DependencyTestUtil.asList(child3, child4));

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1, parent2, parent3, child1, child2, child3, child4);
    }

    @Test
    public void testAddParentsWithChildren() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
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
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addParentWithChildren(parent1, child2);
        graph.addChildrenToRoot(parent1);

        DependencyGraphTestUtil.assertGraphChildren(graph, parent1, child1, child2);
        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1);
    }

    @Test
    public void testHas() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addChildrenToRoot(parent1);

        DependencyGraphTestUtil.assertGraphHas(graph, child1, parent1);
    }

    @Test
    public void testDoesNotHas() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addChildrenToRoot(parent1);

        assertNull(graph.getDependency(parent2.getExternalId()));
        assertFalse(graph.hasDependency(parent2));

        assertNull(graph.getDependency(child2.getExternalId()));
        assertFalse(graph.hasDependency(child2));
    }

    @Test
    public void testGraphWithProvidedRoot() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph(parent1);
        graph.addParentWithChild(parent1, child1);
        graph.addParentWithChild(parent1, child2);
        graph.addParentWithChild(child2, child3);
        graph.addParentWithChild(child2, child4);

        DependencyGraph completeGraph = graph;

        Set<Dependency> rootDependencies = completeGraph.getRootDependencies();
        assertEquals(2, rootDependencies.size());

        assertEquals(0, completeGraph.getChildrenForParent(parent1).size());
        assertEquals(0, completeGraph.getChildrenForParent(child1).size());
        assertEquals(2, completeGraph.getChildrenForParent(child2).size());
        assertEquals(0, completeGraph.getChildrenForParent(child3).size());
        assertEquals(0, completeGraph.getChildrenForParent(child4).size());

        DependencyGraphTestUtil.assertGraphRootChildren(graph, child1, child2);
        DependencyGraphTestUtil.assertGraphChildren(graph, child2, child3, child4);
    }

}
