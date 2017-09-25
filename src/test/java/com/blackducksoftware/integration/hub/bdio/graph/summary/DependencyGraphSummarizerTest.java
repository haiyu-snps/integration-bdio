/**
 * Integration Bdio
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.bdio.graph.summary;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.utility.DependencyTestUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    public void testGraphSummarized() throws IOException {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addParentWithChild(parent1, child1);
        graph.addParentWithChildren(child1, DependencyTestUtil.asSet(grandchild1, grandchild2));
        graph.addParentWithChildren(parent2, DependencyTestUtil.asList(child2, child3));
        graph.addChildrenToRoot(parent1, parent2);

        final DependencyGraphSummarizer summarizer = new DependencyGraphSummarizer(gson);

        final String json = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("summary.json"));

        final GraphSummary summaryJson = summarizer.fromJson(json);

        assertEquals(summarizer.toJson(graph), summarizer.toJson(summaryJson));

    }

}
