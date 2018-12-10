package com.synopsys.integration.bdio.graph.summary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.utility.DependencyTestUtil;

public class DependencyGraphSummarizerTest {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    Dependency parent1 = DependencyTestUtil.newMavenDependency("parent1", "1.0", "parents");
    Dependency parent2 = DependencyTestUtil.newMavenDependency("parent2", "1.0", "parents");
    Dependency parent3 = DependencyTestUtil.newMavenDependency("parent3", "1.0", "parents");
    Dependency parent4 = DependencyTestUtil.newMavenDependency("parent4", "1.0", "parents");

    Dependency child1 = DependencyTestUtil.newMavenDependency("child1", "1.0", "children");
    Dependency child2 = DependencyTestUtil.newMavenDependency("child2", "1.0", "children");
    Dependency child3 = DependencyTestUtil.newMavenDependency("child3", "1.0", "children");
    Dependency child4 = DependencyTestUtil.newMavenDependency("child4", "1.0", "children");

    Dependency grandchild1 = DependencyTestUtil.newMavenDependency("grandchild1", "1.0", "grandchildren");
    Dependency grandchild2 = DependencyTestUtil.newMavenDependency("grandchild2", "1.0", "grandchildren");
    Dependency grandchild3 = DependencyTestUtil.newMavenDependency("grandchild3", "1.0", "grandchildren");
    Dependency grandchild4 = DependencyTestUtil.newMavenDependency("grandchild4", "1.0", "grandchildren");

    @Test
    public void testGraphSummarized() throws IOException, JSONException {
        final DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(gson);

        final MutableDependencyGraph actualGraph = new MutableMapDependencyGraph();
        actualGraph.addParentWithChild(parent1, child1);
        actualGraph.addParentWithChildren(child1, DependencyTestUtil.asSet(grandchild1, grandchild2));
        actualGraph.addParentWithChildren(parent2, DependencyTestUtil.asList(child2, child3));
        actualGraph.addChildrenToRoot(parent1, parent2);
        final String actualGraphSummaryJson = summarizer.toJson(actualGraph);

        final String expectedJson = IOUtils.toString(getClass().getResourceAsStream("/summary.json"), StandardCharsets.UTF_8);
        final GraphSummary expectedGraphSummary = summarizer.fromJson(expectedJson);
        final String expectedGraphSummaryJson = summarizer.toJson(expectedGraphSummary);

        JSONAssert.assertEquals(expectedGraphSummaryJson, actualGraphSummaryJson, false);
    }

}
