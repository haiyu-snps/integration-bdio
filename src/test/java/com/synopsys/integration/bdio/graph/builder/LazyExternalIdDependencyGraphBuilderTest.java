package com.synopsys.integration.bdio.graph.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameVersionDependencyId;
import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.bdio.utility.DependencyIdTestUtil;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

public class LazyExternalIdDependencyGraphBuilderTest {
    DependencyId stringId = new StringDependencyId("id1");
    Dependency stringDep = DependencyTestUtil.newMavenDependency("test1", "test2", "org");
    DependencyId aliasId = new StringDependencyId("alias1");

    Dependency parent1 = DependencyTestUtil.newMavenDependency("parent1", "1.0", "parents");
    Dependency parent2 = DependencyTestUtil.newMavenDependency("parent2", "1.0", "parents");
    Dependency parent3 = DependencyTestUtil.newMavenDependency("parent3", "1.0", "parents");
    Dependency parent4 = DependencyTestUtil.newMavenDependency("parent4", "1.0", "parents");

    Dependency child1 = DependencyTestUtil.newMavenDependency("child1", "1.0", "childs");
    Dependency child2 = DependencyTestUtil.newMavenDependency("child2", "1.0", "childs");
    Dependency child3 = DependencyTestUtil.newMavenDependency("child3", "1.0", "childs");
    Dependency child4 = DependencyTestUtil.newMavenDependency("child4", "1.0", "childs");

    DependencyId parentId1 = new NameDependencyId("parent1");
    DependencyId parentId2 = new NameVersionDependencyId("parent2", "1.0");
    DependencyId parentId3 = new StringDependencyId("parent3");
    DependencyId parentId4 = new StringDependencyId("parent4");

    DependencyId childId1 = new NameDependencyId("child1");
    DependencyId childId2 = new NameDependencyId("child2");
    DependencyId childId3 = new NameDependencyId("child3");
    DependencyId childId4 = new NameDependencyId("child4");

    @Test
    public void testSetInfo() throws MissingExternalIdException {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(stringId);
        builder.setDependencyInfo(stringId, "test1", "test2", stringDep.getExternalId());

        final DependencyGraph graph = builder.build();

        final Dependency dep = graph.getDependency(stringDep.getExternalId());

        assertEquals("test1", dep.getName());
        assertEquals("test2", dep.getVersion());

        DependencyGraphTestUtil.assertGraphRootChildren(graph, stringDep);

    }

    @Test
    public void testAlias() throws MissingExternalIdException {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(aliasId);
        builder.setDependencyInfo(stringId, "test1", "test2", stringDep.getExternalId());
        builder.setDependencyAsAlias(stringId, aliasId);

        final DependencyGraph graph = builder.build();

        final Dependency dep = graph.getDependency(stringDep.getExternalId());

        assertEquals("test1", dep.getName());
        assertEquals("test2", dep.getVersion());

        DependencyGraphTestUtil.assertGraphRootChildren(graph, stringDep);

    }

    @Test
    public void testNoExternalId() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(parentId1);
        builder.setDependencyName(stringId, "test1");
        builder.setDependencyAsAlias(aliasId, stringId);

        final String expectedMessage = "A dependency ({\"name\":\"parent1\"}) in a relationship in the graph never had it's external id set.";
        assertThrows(MissingExternalIdException.class, builder::build, expectedMessage);
    }

    @Test
    public void testNoExternalIdForChild() {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(parentId1);
        builder.addChildWithParent(childId1, parentId1);
        builder.addChildWithParent(childId2, parentId1);

        builder.setDependencyInfo(parentId1, parent1.getName(), parent1.getVersion(), parent1.getExternalId());
        builder.setDependencyInfo(childId1, child1.getName(), child1.getVersion(), child1.getExternalId());

        final String expectedMessage = "A dependency ({\"name\":\"child2\"}) in a relationship in the graph never had it's external id set.";
        assertThrows(MissingExternalIdException.class, builder::build, expectedMessage);
    }

    @Test
    public void testSetPieces() throws MissingExternalIdException {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(stringId);
        builder.setDependencyName(stringId, "test1");
        builder.setDependencyVersion(stringId, "test2");
        builder.setDependencyExternalId(stringId, stringDep.getExternalId());

        final DependencyGraph graph = builder.build();

        final Dependency dep = graph.getDependency(stringDep.getExternalId());

        assertEquals("test1", dep.getName());
        assertEquals("test2", dep.getVersion());

        DependencyGraphTestUtil.assertGraphRootChildren(graph, stringDep);
    }

    @Test
    public void testAddChild() throws MissingExternalIdException {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(parentId1);
        builder.addChildWithParent(childId1, parentId1);
        builder.addChildWithParents(childId2, parentId1);
        builder.addChildWithParents(childId3, DependencyIdTestUtil.asList(parentId1));
        builder.addChildWithParents(childId4, DependencyIdTestUtil.asSet(parentId1));

        builder.setDependencyInfo(parentId1, parent1.getName(), parent1.getVersion(), parent1.getExternalId());
        builder.setDependencyInfo(childId1, child1.getName(), child1.getVersion(), child1.getExternalId());
        builder.setDependencyInfo(childId2, child2.getName(), child2.getVersion(), child2.getExternalId());
        builder.setDependencyInfo(childId3, child3.getName(), child3.getVersion(), child3.getExternalId());
        builder.setDependencyInfo(childId4, child4.getName(), child4.getVersion(), child4.getExternalId());
        final DependencyGraph graph = builder.build();

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1);
        DependencyGraphTestUtil.assertGraphChildren(graph, parent1, child1, child2, child3, child4);
    }

    @Test
    public void testAddParent() throws MissingExternalIdException {
        final LazyExternalIdDependencyGraphBuilder builder = new LazyExternalIdDependencyGraphBuilder();

        builder.addChildToRoot(parentId1);
        builder.addChildrenToRoot(parentId2);
        builder.addChildrenToRoot(DependencyIdTestUtil.asList(parentId3));
        builder.addChildrenToRoot(DependencyIdTestUtil.asSet(parentId4));

        builder.addParentWithChild(parentId1, childId1);
        builder.addParentWithChildren(parentId2, childId1);
        builder.addParentWithChildren(parentId3, DependencyIdTestUtil.asList(childId1));
        builder.addParentWithChildren(parentId4, DependencyIdTestUtil.asSet(childId1));

        builder.setDependencyInfo(parentId1, parent1.getName(), parent1.getVersion(), parent1.getExternalId());
        builder.setDependencyInfo(parentId2, parent2.getName(), parent2.getVersion(), parent2.getExternalId());
        builder.setDependencyInfo(parentId3, parent3.getName(), parent3.getVersion(), parent3.getExternalId());
        builder.setDependencyInfo(parentId4, parent4.getName(), parent4.getVersion(), parent4.getExternalId());
        builder.setDependencyInfo(childId1, child1.getName(), child1.getVersion(), child1.getExternalId());
        final DependencyGraph graph = builder.build();

        DependencyGraphTestUtil.assertGraphRootChildren(graph, parent1, parent2, parent3, parent4);
        DependencyGraphTestUtil.assertGraphParents(graph, child1, parent1, parent2, parent3, parent4);
    }

}
