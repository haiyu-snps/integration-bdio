package com.synopsys.integration.bdio.graph;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

class DependencyGraphCombinerTest {
    private final Dependency dep1 = DependencyTestUtil.newMavenDependency("children:first:1.0");
    private final Dependency dep2 = DependencyTestUtil.newMavenDependency("children:second:2.0");
    private final Dependency dep3 = DependencyTestUtil.newMavenDependency("children:third:3.0");
    private final Dependency dep4 = DependencyTestUtil.newMavenDependency("children:fourth:4.0");
    private final Dependency dep5 = DependencyTestUtil.newMavenDependency("subChild:first:1.0");
    private final Dependency dep6 = DependencyTestUtil.newMavenDependency("subChild:second:2.0");
    private final Dependency dep7 = DependencyTestUtil.newMavenDependency("subChild:third:3.0");

    private final ProjectDependency parentProject = DependencyTestUtil.newProjectDependency(Forge.MAVEN, "parent-project", "1");
    private final ProjectDependency childProject1 = DependencyTestUtil.newProjectDependency(Forge.MAVEN, "child-project1", "1");

    @Test
    void testSubProjects() {
        MutableDependencyGraph parent = new MutableMapDependencyGraph(parentProject);
        MutableDependencyGraph child1 = new MutableMapDependencyGraph(childProject1);
        MutableDependencyGraph noProjectGraph = new MutableMapDependencyGraph();

        noProjectGraph.addChildToRoot(dep3);

        child1.addChildToRoot(dep2);
        child1.addGraphAsChildrenToRoot(noProjectGraph);

        parent.addChildToRoot(dep1);
        parent.addGraphAsChildrenToRoot(child1);

        DependencyGraphTestUtil.assertGraphRootChildren(parent, childProject1, dep1);
        DependencyGraphTestUtil.assertGraphRootChildren(child1, dep2, dep3);
    }

    @Test
    void testAddChildWithParents() {
        MutableDependencyGraph first = new MutableMapDependencyGraph();
        MutableDependencyGraph second = new MutableMapDependencyGraph();
        MutableDependencyGraph combined = new MutableMapDependencyGraph();

        first.addChildToRoot(dep1);
        first.addChildWithParent(dep2, dep1);
        first.addChildWithParent(dep3, dep2);
        DependencyGraphTestUtil.assertGraphChildren(first, dep1, dep2);

        second.addChildToRoot(dep4);
        second.addParentWithChild(dep4, dep5);
        second.addParentWithChild(dep5, dep6);
        second.addParentWithChild(dep5, dep7);

        combined.addGraphAsChildrenToRoot(first);
        combined.addGraphAsChildrenToParent(dep2, second);

        DependencyGraphTestUtil.assertGraphRootChildren(combined, dep1, dep4);
        // From first graph
        DependencyGraphTestUtil.assertGraphChildren(combined, dep1, dep2);
        DependencyGraphTestUtil.assertGraphChildren(combined, dep2, dep3);
        // From second graph
        DependencyGraphTestUtil.assertGraphChildren(combined, dep4, dep5);
        DependencyGraphTestUtil.assertGraphChildren(combined, dep5, dep6, dep7);
    }

}
