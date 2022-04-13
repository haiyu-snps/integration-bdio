package com.synopsys.integration.bdio.graph.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

class BasicDependencyGraphTransformerEdgeCasesTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    private Dependency node(String name, String version, String group) {
        ExternalId projectExternalId = externalIdFactory.createMavenExternalId(group, name, version);
        return new Dependency(name, version, projectExternalId);
    }

    @Test
    void testTransformingExpensiveRecursiveTree() {
        // Here we generate a broad tree - for each new node, it becomes a child of all previous nodes
        // lets do it for [A,B,C,D]
        // after B we have A->B
        // after C we have A->C and A->B and B->C
        // after D we have A->D and A->C and C->D and B->D and A->B
        // so the final tree is
        // ..........A
        // ......../.|.\
        // .......D..C..B __
        // ..........|...\..\
        // ..........D....C..D
        // ...............|
        // ...............D
        // And it only gets wider from there.

        HashSet<Dependency> generated = new HashSet<>();
        HashMap<Dependency, Integer> counts = new HashMap<>();

        Dependency projectDependency = Dependency.FACTORY.createNameVersionDependency(Forge.ANACONDA, "dumb", "dumbVer");
        ProjectDependencyGraph graph = new ProjectDependencyGraph(new ProjectDependency(projectDependency));
        for (int i = 0; i < 200; i++) {
            String name = "node" + i;
            Dependency next = node(name, name, name);
            for (Dependency parent : generated) {
                counts.put(parent, counts.get(parent) + 1);
                graph.addParentWithChild(parent, next);
            }
            generated.add(next);
            counts.put(next, 0);
            if (i == 0) {
                graph.addDirectDependency(next);
            }
        }

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createPopulatedBdioDocument(graph);
        simpleBdioDocument.getBillOfMaterials().id = BdioId.createFromUUID("123");

        int found = 0;
        for (BdioComponent component : simpleBdioDocument.getComponents()) {
            Dependency node = null;
            for (Dependency candidate : generated) {
                if (component.name.equals(candidate.getName())) {
                    assertNull(node);
                    node = candidate;
                    found++;
                }
            }

            assertEquals(counts.get(node).intValue(), component.relationships.size());
        }
        assertEquals(generated.size(), found);
    }

    @Test
    void testTransformingBrokenTree() {
        Dependency projectDependency = Dependency.FACTORY.createNameVersionDependency(Forge.ANACONDA, "dumb", "dumbVer");
        ProjectDependencyGraph graph = new ProjectDependencyGraph(new ProjectDependency(projectDependency));

        Dependency childOne = node("one", "one", "one");
        Dependency childTwo = node("two", "two", "two");

        Dependency sharedLeft = node("shared", "shared", "shared");
        Dependency sharedRight = node("shared", "shared", "shared");

        graph.addParentWithChild(sharedLeft, childOne);
        graph.addParentWithChild(sharedRight, childTwo);

        Dependency parentLeft = node("parentLeft", "parentLeft", "parentLeft");
        Dependency parentRight = node("parentRight", "parentRight", "parentRight");

        graph.addParentWithChild(parentLeft, sharedLeft);
        graph.addParentWithChild(parentRight, sharedRight);

        graph.addChildrenToRoot(parentLeft, parentRight);

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createPopulatedBdioDocument(graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.getBillOfMaterials().id = BdioId.createFromUUID("123");

        for (BdioComponent component : simpleBdioDocument.getComponents()) {
            if (component.name.equals("shared")) {
                assertEquals(2, component.relationships.size());
            }
        }

    }

    @Test
    void testTransformingBrokenTreeLeftHasNodeRightEmpty() {
        Dependency projectDependency = Dependency.FACTORY.createNameVersionDependency(Forge.ANACONDA, "dumb", "dumbVer");
        ProjectDependencyGraph graph = new ProjectDependencyGraph(new ProjectDependency(projectDependency));

        Dependency childOne = node("one", "one", "one");

        Dependency sharedLeft = node("shared", "shared", "shared");
        Dependency sharedRight = node("shared", "shared", "shared");

        graph.addParentWithChild(sharedLeft, childOne);

        Dependency parentLeft = node("parentLeft", "parentLeft", "parentLeft");
        Dependency parentRight = node("parentRight", "parentRight", "parentRight");

        graph.addParentWithChild(parentLeft, sharedLeft);
        graph.addParentWithChild(parentRight, sharedRight);

        graph.addChildrenToRoot(parentLeft, parentRight);

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createPopulatedBdioDocument(graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.getBillOfMaterials().id = BdioId.createFromUUID("123");

        boolean found = false;
        for (BdioComponent component : simpleBdioDocument.getComponents()) {
            if ("shared".equals(component.name)) {
                assertEquals(1, component.relationships.size());
            } else if ("one".equals(component.name)) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    void testTransformingBrokenTreeLeftEmpty() {
        Dependency projectDependency = Dependency.FACTORY.createNameVersionDependency(Forge.ANACONDA, "dumb", "dumbVer");
        ProjectDependencyGraph graph = new ProjectDependencyGraph(new ProjectDependency(projectDependency));

        Dependency childOne = node("one", "one", "one");

        Dependency sharedLeft = node("shared", "shared", "shared");
        Dependency sharedRight = node("shared", "shared", "shared");

        graph.addParentWithChild(sharedRight, childOne);

        Dependency parentLeft = node("parentLeft", "parentLeft", "parentLeft");
        Dependency parentRight = node("parentRight", "parentRight", "parentRight");

        graph.addParentWithChild(parentLeft, sharedLeft);
        graph.addParentWithChild(parentRight, sharedRight);

        graph.addChildrenToRoot(parentLeft, parentRight);

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createPopulatedBdioDocument(graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.getBillOfMaterials().id = BdioId.createFromUUID("123");

        boolean found = false;
        for (BdioComponent component : simpleBdioDocument.getComponents()) {
            if ("shared".equals(component.name)) {
                assertEquals(1, component.relationships.size());
            } else if ("one".equals(component.name)) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    void testCyclic() {
        ProjectDependency project = new ProjectDependency(node("project", "project", "project"));
        ProjectDependencyGraph graph = new ProjectDependencyGraph(new ProjectDependency(project));

        Dependency one = node("one", "one", "one");
        Dependency two = node("two", "two", "two");
        Dependency three = node("three", "three", "three");

        graph.addParentWithChild(one, three);
        graph.addParentWithChild(two, one);
        graph.addParentWithChild(three, two);

        graph.addChildrenToRoot(one);

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocumentRecursive = simpleBdioFactory.createPopulatedBdioDocument(graph);

        assertEquals(3, simpleBdioDocumentRecursive.getComponents().size());
    }

    @Test
    void testProjectAsChild() {
        ProjectDependency project = new ProjectDependency(node("project", "project", "project"));
        ProjectDependencyGraph graph = new ProjectDependencyGraph(new ProjectDependency(project));

        Dependency one = node("one", "one", "one");
        Dependency two = node("two", "two", "two");

        graph.addParentWithChild(one, two);
        graph.addParentWithChild(project, two);

        graph.addChildrenToRoot(one);
        graph.addChildrenToRoot(project);

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocumentRecursive = simpleBdioFactory.createPopulatedBdioDocument(graph);

        assertEquals(2, simpleBdioDocumentRecursive.getComponents().size());
    }

}
