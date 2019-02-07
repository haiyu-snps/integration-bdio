package com.synopsys.integration.bdio.graph.summary;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyGraphSummaryComparerTest {
    @Test
    public void testGraphySummaryEqual() {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        DependencyGraphSummarizer dependencyGraphSummarizer = new DependencyGraphSummarizer(gson);
        DependencyGraphSummaryComparer dependencyGraphSummaryComparer = new DependencyGraphSummaryComparer(dependencyGraphSummarizer);

        MutableMapDependencyGraph leftGraph = new MutableMapDependencyGraph();
        MutableMapDependencyGraph rightGraph = new MutableMapDependencyGraph();

        assertTrue(dependencyGraphSummaryComparer.areEqual(leftGraph, rightGraph));

        leftGraph.addChildToRoot(new Dependency(externalIdFactory.createMavenExternalId("group1", "name1", "version1")));

        assertFalse(dependencyGraphSummaryComparer.areEqual(leftGraph, rightGraph));

        rightGraph.addChildToRoot(new Dependency(externalIdFactory.createMavenExternalId("group1", "name1", "version1")));

        assertTrue(dependencyGraphSummaryComparer.areEqual(leftGraph, rightGraph));
    }

}
