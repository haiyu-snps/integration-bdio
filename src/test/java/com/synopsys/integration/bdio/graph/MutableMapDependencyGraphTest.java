package com.synopsys.integration.bdio.graph;

import static com.synopsys.integration.bdio.utility.DependencyTestUtil.addMavenRelationship;
import static com.synopsys.integration.bdio.utility.DependencyTestUtil.newMavenDependency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

public class MutableMapDependencyGraphTest {
    private final Dependency parent1 = newMavenDependency("parent1", "1.0", "parents");
    private final Dependency parent2 = newMavenDependency("parent2", "1.0", "parents");
    private final Dependency parent3 = newMavenDependency("parent3", "1.0", "parents");
    private final Dependency parent4 = newMavenDependency("parent4", "1.0", "parents");

    private final Dependency child1 = newMavenDependency("child1", "1.0", "children");
    private final Dependency child2 = newMavenDependency("child2", "1.0", "children");
    private final Dependency child3 = newMavenDependency("child3", "1.0", "children");
    private final Dependency child4 = newMavenDependency("child4", "1.0", "children");

    private final Dependency grandchild1 = newMavenDependency("grandchild1", "1.0", "grandchildren");
    private final Dependency grandchild2 = newMavenDependency("grandchild2", "1.0", "grandchildren");
    private final Dependency grandchild3 = newMavenDependency("grandchild3", "1.0", "grandchildren");
    private final Dependency grandchild4 = newMavenDependency("grandchild4", "1.0", "grandchildren");

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
    public void testGraphWithoutExplicitRoot() {
        Dependency project = DependencyTestUtil.newMavenDependency("org.foundweekends:sbt-bintray:HEAD+20210303-1347");
        MutableDependencyGraph graph = new MutableMapDependencyGraph(project);

        addMavenRelationship(graph, "org.foundweekends:sbt-bintray:HEAD+20210303-1347", "org.slf4j:slf4j-nop:1.7.7");
        addMavenRelationship(graph, "org.foundweekends:sbt-bintray:HEAD+20210303-1347", "org.foundweekends:bintry_2.10:0.5.1");
        addMavenRelationship(graph, "org.foundweekends:bintry_2.10:0.5.1", "net.databinder.dispatch:dispatch-json4s-native_2.10:0.11.2");
        addMavenRelationship(graph, "org.slf4j:slf4j-nop:1.7.7", "org.slf4j:slf4j-api:1.7.7");
        addMavenRelationship(graph, "com.ning:async-http-client:1.8.10", "org.slf4j:slf4j-api:1.7.5");
        addMavenRelationship(graph, "org.json4s:json4s-core_2.10:3.2.10", "org.scala-lang:scalap:2.10.6");
        addMavenRelationship(graph, "org.scala-lang:scalap:2.10.6", "org.scala-lang:scala-compiler:2.10.6");
        addMavenRelationship(graph, "org.scala-lang:scala-compiler:2.10.6", "org.scala-lang:scala-reflect:2.10.6");
        addMavenRelationship(graph, "net.databinder.dispatch:dispatch-json4s-native_2.10:0.11.2", "org.json4s:json4s-native_2.10:3.2.10");
        addMavenRelationship(graph, "org.json4s:json4s-native_2.10:3.2.10", "org.json4s:json4s-core_2.10:3.2.10");
        addMavenRelationship(graph, "net.databinder.dispatch:dispatch-json4s-native_2.10:0.11.2", "org.json4s:json4s-core_2.10:3.2.10");
        addMavenRelationship(graph, "org.json4s:json4s-core_2.10:3.2.10", "com.thoughtworks.paranamer:paranamer:2.6");
        addMavenRelationship(graph, "org.json4s:json4s-core_2.10:3.2.10", "org.json4s:json4s-ast_2.10:3.2.10");
        addMavenRelationship(graph, "net.databinder.dispatch:dispatch-json4s-native_2.10:0.11.2", "net.databinder.dispatch:dispatch-core_2.10:0.11.2");
        addMavenRelationship(graph, "net.databinder.dispatch:dispatch-core_2.10:0.11.2", "com.ning:async-http-client:1.8.10");
        addMavenRelationship(graph, "com.ning:async-http-client:1.8.10", "io.netty:netty:3.9.2.Final");
        addMavenRelationship(graph, "org.slf4j:slf4j-api:1.7.5", "org.slf4j:slf4j-api:1.7.7");

        DependencyGraph completeGraph = graph;
        Set<Dependency> rootDependencies = completeGraph.getRootDependencies();

        assertEquals(2, rootDependencies.size());
        assertTrue(rootDependencies.contains(DependencyTestUtil.newMavenDependency("org.slf4j:slf4j-nop:1.7.7")));
        assertTrue(rootDependencies.contains(DependencyTestUtil.newMavenDependency("org.foundweekends:bintry_2.10:0.5.1")));
    }

}
