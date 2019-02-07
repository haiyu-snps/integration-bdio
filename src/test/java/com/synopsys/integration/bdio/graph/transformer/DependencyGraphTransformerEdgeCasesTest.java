package com.synopsys.integration.bdio.graph.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyGraphTransformerEdgeCasesTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    private Dependency node(String name, String version, String group) {
        ExternalId projectExternalId = externalIdFactory.createMavenExternalId(group, name, version);
        Dependency root = new Dependency(name, version, projectExternalId);
        return root;
    }

    @Test
    public void testTransformingExpensiveRecursiveTree() throws URISyntaxException, IOException, JSONException {
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

        MutableDependencyGraph graph = new MutableMapDependencyGraph();
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
                graph.addChildToRoot(next);
            }
        }

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "dumb", "dumbVer");
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("dumb", "dumbVer", id, graph);
        simpleBdioDocument.billOfMaterials.id = BdioId.createFromUUID("123");

        int found = 0;
        for (BdioComponent component : simpleBdioDocument.components) {
            Dependency node = null;
            for (Dependency candidate : generated) {
                if (component.name == candidate.name) {
                    assertEquals(null, node);
                    node = candidate;
                    found++;
                }
            }

            assertEquals(counts.get(node).intValue(), component.relationships.size());
        }
        assertEquals(generated.size(), found);
    }

    @Test
    public void testTransformingBrokenTree() throws URISyntaxException, IOException, JSONException {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

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
        ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "dumb", "dumbVer");
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("dumb", "dumbVer", id, graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.billOfMaterials.id = BdioId.createFromUUID("123");

        for (BdioComponent component : simpleBdioDocument.components) {
            if (component.name == "shared") {
                assertEquals(component.relationships.size(), 2);
            }
        }

    }

    @Test
    public void testTransformingBrokenTreeLeftHasNodeRightEmpty() throws URISyntaxException, IOException, JSONException {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

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
        ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "dumb", "dumbVer");
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("dumb", "dumbVer", id, graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.billOfMaterials.id = BdioId.createFromUUID("123");

        boolean found = false;
        for (BdioComponent component : simpleBdioDocument.components) {
            if (component.name == "shared") {
                assertEquals(1, component.relationships.size());
            } else if (component.name == "one") {
                found = true;
            }
        }
        assertEquals(true, found);
    }

    @Test
    public void testTransformingBrokenTreeLeftEmpty() throws URISyntaxException, IOException, JSONException {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

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
        ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.ANACONDA, "dumb", "dumbVer");
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("dumb", "dumbVer", id, graph);

        // we are overriding the default value of a new uuid just to pass the json comparison
        simpleBdioDocument.billOfMaterials.id = BdioId.createFromUUID("123");

        boolean found = false;
        for (BdioComponent component : simpleBdioDocument.components) {
            if (component.name == "shared") {
                assertEquals(1, component.relationships.size());
            } else if (component.name == "one") {
                found = true;
            }
        }
        assertEquals(true, found);
    }

    @Test
    public void testCyclic() throws URISyntaxException, IOException, JSONException {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        Dependency one = node("one", "one", "one");
        Dependency two = node("two", "two", "two");
        Dependency three = node("three", "three", "three");
        Dependency project = node("project", "project", "project");

        graph.addParentWithChild(one, three);
        graph.addParentWithChild(two, one);
        graph.addParentWithChild(three, two);

        graph.addChildrenToRoot(one);

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocumentRecursive = simpleBdioFactory.createSimpleBdioDocument(project.name, project.version, project.externalId, graph);

        assertEquals(simpleBdioDocumentRecursive.components.size(), 3);
    }

    @Test
    public void testProjectAsChild() throws URISyntaxException, IOException, JSONException {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        Dependency one = node("one", "one", "one");
        Dependency two = node("two", "two", "two");
        Dependency project = node("project", "project", "project");

        graph.addParentWithChild(one, two);
        graph.addParentWithChild(project, two);

        graph.addChildrenToRoot(one);
        graph.addChildrenToRoot(project);

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocumentRecursive = simpleBdioFactory.createSimpleBdioDocument(project.name, project.version, project.externalId, graph);

        assertEquals(simpleBdioDocumentRecursive.components.size(), 2);
    }

}
