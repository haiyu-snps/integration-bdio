package com.synopsys.integration.bdio.graph;

import static com.synopsys.integration.bdio.utility.DependencyTestUtil.newMavenDependency;
import static com.synopsys.integration.bdio.utility.DependencyTestUtil.newProjectDependency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

class MutableMapDependencyGraphTest {
    private final ProjectDependency rootDependency = newProjectDependency(Forge.MAVEN, "root", "1.2.3");
    private final ProjectDependency subProjectDependency = newProjectDependency(Forge.MAVEN, "sub-project", "x.y.z");
    private final ProjectDependency anotherSubProjectDependency = newProjectDependency(Forge.MAVEN, "another-sub-project", "a.b.c");

    private final Dependency parent1 = newMavenDependency("parents:parent1:1.0");
    private final Dependency parent2 = newMavenDependency("parents:parent2:1.0");
    private final Dependency parent3 = newMavenDependency("parents:parent3:1.0");

    private final Dependency child1 = newMavenDependency("children:child1:1.0");
    private final Dependency child2 = newMavenDependency("children:child2:1.0");
    private final Dependency child3 = newMavenDependency("children:child3:1.0");
    private final Dependency child4 = newMavenDependency("children:child4:1.0");

    private final Dependency grandchild1 = newMavenDependency("grandchildren:grandchild1:1.0");
    private final Dependency grandchild2 = newMavenDependency("grandchildren:grandchild2:1.0");

    @Test
    void testProjectNodes() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph(rootDependency);
        graph.addChildToRoot(child1);

        MutableDependencyGraph subProjectGraph = new MutableMapDependencyGraph(subProjectDependency);
        subProjectGraph.addChildToRoot(grandchild1);
        graph.addGraphAsChildrenToRoot(subProjectGraph);

        MutableDependencyGraph anotherSubProjectGraph = new MutableMapDependencyGraph(anotherSubProjectDependency);
        anotherSubProjectGraph.addChildToRoot(grandchild2);
        graph.addGraphAsChildrenToParent(subProjectDependency, anotherSubProjectGraph);

        DependencyGraphTestUtil.assertGraphRootChildren(graph, child1, subProjectDependency);
        DependencyGraphTestUtil.assertGraphChildren(graph, subProjectDependency, grandchild1, anotherSubProjectDependency);
        DependencyGraphTestUtil.assertGraphChildren(graph, anotherSubProjectDependency, grandchild2);
    }

    @Test
    void testAddChildWithParents() {
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
    void testRootAdd() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        graph.addChildToRoot(parent1);
        graph.addChildrenToRoot(parent2, parent3);
        graph.addChildrenToRoot(DependencyTestUtil.asSet(child1, child2));
        graph.addChildrenToRoot(DependencyTestUtil.asList(child3, child4));

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1, parent2, parent3, child1, child2, child3, child4);
    }

    @Test
    void testAddParentsWithChildren() {
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
    void testAddToSameParent() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addParentWithChildren(parent1, child2);
        graph.addChildrenToRoot(parent1);

        DependencyGraphTestUtil.assertGraphChildren(graph, parent1, child1, child2);
        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1);
    }

    @Test
    void testHas() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addChildrenToRoot(parent1);

        DependencyGraphTestUtil.assertGraphHas(graph, child1, parent1);
    }

    @Test
    void testDoesNotHas() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addChildrenToRoot(parent1);

        assertNull(graph.getDependency(parent2.getExternalId()));
        assertFalse(graph.hasDependency(parent2));

        assertNull(graph.getDependency(child2.getExternalId()));
        assertFalse(graph.hasDependency(child2));
    }

    @Test
    void testGraphWithProvidedRoot() {
        MutableDependencyGraph graph = new MutableMapDependencyGraph(new ProjectDependency(parent1));
        graph.addParentWithChild(parent1, child1);
        graph.addParentWithChild(parent1, child2);
        graph.addParentWithChild(child2, child3);
        graph.addParentWithChild(child2, child4);

        Set<Dependency> rootDependencies = graph.getRootDependencies();
        assertEquals(2, rootDependencies.size());

        assertEquals(0, graph.getChildrenForParent(parent1).size());
        assertEquals(0, graph.getChildrenForParent(child1).size());
        assertEquals(2, graph.getChildrenForParent(child2).size());
        assertEquals(0, graph.getChildrenForParent(child3).size());
        assertEquals(0, graph.getChildrenForParent(child4).size());

        DependencyGraphTestUtil.assertGraphRootChildren(graph, child1, child2);
        DependencyGraphTestUtil.assertGraphChildren(graph, child2, child3, child4);
    }

}
