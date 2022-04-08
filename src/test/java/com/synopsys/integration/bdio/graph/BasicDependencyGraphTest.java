package com.synopsys.integration.bdio.graph;

import static com.synopsys.integration.bdio.utility.DependencyTestUtil.newMavenDependency;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

class BasicDependencyGraphTest {
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
    void testAddChildWithParents() {
        BasicDependencyGraph graph = new BasicDependencyGraph();
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
        BasicDependencyGraph graph = new BasicDependencyGraph();

        graph.addChildToRoot(parent1);
        graph.addChildrenToRoot(parent2, parent3);
        graph.addChildrenToRoot(DependencyTestUtil.asSet(child1, child2));
        graph.addChildrenToRoot(DependencyTestUtil.asList(child3, child4));

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1, parent2, parent3, child1, child2, child3, child4);
    }

    @Test
    void testAddParentsWithChildren() {
        BasicDependencyGraph graph = new BasicDependencyGraph();
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
        BasicDependencyGraph graph = new BasicDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addParentWithChildren(parent1, child2);
        graph.addChildrenToRoot(parent1);

        DependencyGraphTestUtil.assertGraphChildren(graph, parent1, child1, child2);
        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1);
    }

    @Test
    void testHas() {
        BasicDependencyGraph graph = new BasicDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addChildrenToRoot(parent1);

        DependencyGraphTestUtil.assertGraphHas(graph, child1, parent1);
    }

    @Test
    void testDoesNotHas() {
        BasicDependencyGraph graph = new BasicDependencyGraph();
        graph.addParentWithChildren(parent1, child1);
        graph.addChildrenToRoot(parent1);

        assertNull(graph.getDependency(parent2.getExternalId()));
        assertFalse(graph.hasDependency(parent2));

        assertNull(graph.getDependency(child2.getExternalId()));
        assertFalse(graph.hasDependency(child2));
    }

    @Test
    void testCopyingFromProjectDependencyGraph() {
        Dependency projectDependency = Dependency.FACTORY.createNameVersionDependency(Forge.GITHUB, "synopsys-detect", "1.0");
        ProjectDependencyGraph projectGraph = new ProjectDependencyGraph(projectDependency);
        projectGraph.addChildToRoot(child1);

        BasicDependencyGraph basicGraph = new BasicDependencyGraph();
        basicGraph.addChildToRoot(child2);
        basicGraph.copyGraphToRoot(projectGraph);

        DependencyGraphTestUtil.assertGraphRootChildren(basicGraph, child2, projectDependency);
        DependencyGraphTestUtil.assertGraphChildren(basicGraph, projectDependency, child1);
    }

}
