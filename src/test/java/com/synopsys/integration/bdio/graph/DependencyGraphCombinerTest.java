package com.synopsys.integration.bdio.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

public class DependencyGraphCombinerTest {
    Dependency dep1 = DependencyTestUtil.newMavenDependency("first", "1.0", "children");
    Dependency dep2 = DependencyTestUtil.newMavenDependency("second", "2.0", "children");
    Dependency dep3 = DependencyTestUtil.newMavenDependency("third", "3.0", "children");
    Dependency dep4 = DependencyTestUtil.newMavenDependency("fourth", "4.0", "children");
    Dependency dep5 = DependencyTestUtil.newMavenDependency("first", "1.0", "subChild");
    Dependency dep6 = DependencyTestUtil.newMavenDependency("second", "2.0", "subChild");
    Dependency dep7 = DependencyTestUtil.newMavenDependency("third", "3.0", "subChild");

    @Test
    public void testAddChildWithParents() {
        final MutableDependencyGraph first = new MutableMapDependencyGraph();
        final MutableDependencyGraph second = new MutableMapDependencyGraph();
        final MutableDependencyGraph combined = new MutableMapDependencyGraph();

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

        DependencyGraphTestUtil.assertGraphRootChildren(combined, dep1);

        DependencyGraphTestUtil.assertGraphChildren(combined, dep1, dep2);
        DependencyGraphTestUtil.assertGraphChildren(combined, dep2, dep3, dep4);
        DependencyGraphTestUtil.assertGraphChildren(combined, dep4, dep5);
        DependencyGraphTestUtil.assertGraphChildren(combined, dep5, dep6, dep7);
    }

}
