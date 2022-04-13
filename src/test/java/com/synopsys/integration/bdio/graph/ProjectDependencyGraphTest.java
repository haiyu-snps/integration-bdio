package com.synopsys.integration.bdio.graph;

import static com.synopsys.integration.bdio.utility.DependencyTestUtil.newMavenDependency;
import static com.synopsys.integration.bdio.utility.DependencyTestUtil.newProjectDependency;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;

class ProjectDependencyGraphTest {
    private final ProjectDependency rootDependency = newProjectDependency(Forge.MAVEN, "root", "1.2.3");
    private final ProjectDependency subProjectDependency = newProjectDependency(Forge.MAVEN, "sub-project", "x.y.z");
    private final ProjectDependency anotherSubProjectDependency = newProjectDependency(Forge.MAVEN, "another-sub-project", "a.b.c");

    private final Dependency parent1 = newMavenDependency("parents:parent1:1.0");

    private final Dependency child1 = newMavenDependency("children:child1:1.0");
    private final Dependency child2 = newMavenDependency("children:child2:1.0");
    private final Dependency child3 = newMavenDependency("children:child3:1.0");
    private final Dependency child4 = newMavenDependency("children:child4:1.0");

    private final Dependency grandchild1 = newMavenDependency("grandchildren:grandchild1:1.0");
    private final Dependency grandchild2 = newMavenDependency("grandchildren:grandchild2:1.0");

    @Test
    void testProjectNodes() {
        ProjectDependencyGraph graph = new ProjectDependencyGraph(rootDependency);
        graph.addDirectDependency(child1);

        ProjectDependencyGraph subProjectGraph = new ProjectDependencyGraph(subProjectDependency);
        subProjectGraph.addDirectDependency(grandchild1);

        ProjectDependencyGraph anotherSubProjectGraph = new ProjectDependencyGraph(anotherSubProjectDependency);
        anotherSubProjectGraph.addDirectDependency(grandchild2);

        // The order in which the subprojects are copied matters!
        subProjectGraph.copyGraphToRoot(anotherSubProjectGraph);
        graph.copyGraphToRoot(subProjectGraph);

        DependencyGraphTestUtil.assertGraphRootChildren(graph, child1, subProjectDependency);
        DependencyGraphTestUtil.assertGraphChildren(graph, subProjectDependency, grandchild1, anotherSubProjectDependency);
        DependencyGraphTestUtil.assertGraphChildren(graph, anotherSubProjectDependency, grandchild2);
    }

    @Test
    void testGraphWithProvidedRoot() {
        ProjectDependencyGraph graph = new ProjectDependencyGraph(new ProjectDependency(parent1));
        graph.addParentWithChild(parent1, child1);
        graph.addParentWithChild(parent1, child2);
        graph.addParentWithChild(child2, child3);
        graph.addParentWithChild(child2, child4);

        Set<Dependency> rootDependencies = graph.getDirectDependencies();
        assertEquals(2, rootDependencies.size());

        assertEquals(2, graph.getChildrenForParent(parent1).size());
        assertEquals(0, graph.getChildrenForParent(child1).size());
        assertEquals(2, graph.getChildrenForParent(child2).size());
        assertEquals(0, graph.getChildrenForParent(child3).size());
        assertEquals(0, graph.getChildrenForParent(child4).size());

        DependencyGraphTestUtil.assertGraphRootChildren(graph, child1, child2);
        DependencyGraphTestUtil.assertGraphChildren(graph, child2, child3, child4);
    }
}