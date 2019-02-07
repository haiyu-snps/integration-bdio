/**
 * integration-bdio
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.bdio.graph.summary;

import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.BdioId;

public class DependencyGraphSummaryComparer {
    private final DependencyGraphSummarizer dependencyGraphSummarizer;

    public DependencyGraphSummaryComparer(DependencyGraphSummarizer dependencyGraphSummarizer) {
        this.dependencyGraphSummarizer = dependencyGraphSummarizer;
    }

    public boolean areEqual(DependencyGraph left, DependencyGraph right) {
        GraphSummary leftSummary = dependencyGraphSummarizer.fromGraph(left);
        GraphSummary rightSummary = dependencyGraphSummarizer.fromGraph(right);
        return areEqual(leftSummary, rightSummary);
    }

    public boolean areEqual(GraphSummary left, GraphSummary right) {
        boolean isEqual = true;

        isEqual = isEqual && left.rootExternalDataIds.equals(right.rootExternalDataIds);
        isEqual = isEqual && left.dependencySummaries.keySet().equals(right.dependencySummaries.keySet());

        Set<BdioId> leftRelationshipIds = getRelationships(left);
        Set<BdioId> leftExistingRelationshipsIds = getExistingRelationships(left, leftRelationshipIds);

        Set<BdioId> rightRelationshipIds = getRelationships(right);
        Set<BdioId> rightExistingRelationshipsIds = getExistingRelationships(right, rightRelationshipIds);

        isEqual = isEqual && leftExistingRelationshipsIds.equals(rightExistingRelationshipsIds);

        for (BdioId key : left.dependencySummaries.keySet()) {
            isEqual = isEqual && left.dependencySummaries.get(key).getName().equals(right.dependencySummaries.get(key).getName());
            isEqual = isEqual && left.dependencySummaries.get(key).getVersion().equals(right.dependencySummaries.get(key).getVersion());
        }
        for (BdioId key : leftExistingRelationshipsIds) {
            isEqual = isEqual && left.externalDataIdRelationships.get(key).equals(right.externalDataIdRelationships.get(key));
        }

        return isEqual;
    }

    private Set<BdioId> getRelationships(GraphSummary graphSummary) {
        return graphSummary.externalDataIdRelationships.keySet();
    }

    private Set<BdioId> getExistingRelationships(GraphSummary graphSummary, Set<BdioId> relationships) {
        return relationships.stream().filter(key -> graphSummary.externalDataIdRelationships.get(key) != null && graphSummary.externalDataIdRelationships.get(key).size() > 0).collect(Collectors.toSet());
    }

}
