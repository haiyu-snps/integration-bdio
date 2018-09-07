package com.synopsys.integration.hub.bdio.graph.summary;

import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.hub.bdio.graph.DependencyGraph;

public class DependencyGraphComparer {
    private final DependencyGraphSummarizer dependencyGraphSummarizer;

    public DependencyGraphComparer(final DependencyGraphSummarizer dependencyGraphSummarizer) {
        this.dependencyGraphSummarizer = dependencyGraphSummarizer;
    }

    public boolean areEqual(final DependencyGraph left, final DependencyGraph right) {
        final GraphSummary leftSummary = dependencyGraphSummarizer.fromGraph(left);
        final GraphSummary rightSummary = dependencyGraphSummarizer.fromGraph(right);
        return areEqual(leftSummary, rightSummary);
    }

    public boolean areEqual(final GraphSummary left, final GraphSummary right) {
        boolean isEqual = true;

        isEqual = isEqual && left.rootExternalDataIds.equals(right.rootExternalDataIds);
        isEqual = isEqual && left.dependencySummaries.keySet().equals(right.dependencySummaries.keySet());

        final Set<String> leftRelationshipIds = left.externalDataIdRelationships.keySet();
        final Set<String> leftExistingRelationshipsIds = leftRelationshipIds.stream().filter(key -> left.externalDataIdRelationships.get(key) != null && left.externalDataIdRelationships.get(key).size() > 0).collect(Collectors.toSet());

        final Set<String> rightRelationshipIds = right.externalDataIdRelationships.keySet();
        final Set<String> rightExistingRelationshipsIds = rightRelationshipIds.stream().filter(key -> right.externalDataIdRelationships.get(key) != null && right.externalDataIdRelationships.get(key).size() > 0).collect(Collectors.toSet());

        isEqual = isEqual && leftExistingRelationshipsIds.equals(rightExistingRelationshipsIds);

        for (final String key : left.dependencySummaries.keySet()) {
            isEqual = isEqual && left.dependencySummaries.get(key).getName().equals(right.dependencySummaries.get(key).getName());
            isEqual = isEqual && left.dependencySummaries.get(key).getVersion().equals(right.dependencySummaries.get(key).getVersion());
        }
        for (final String key : leftExistingRelationshipsIds) {
            isEqual = isEqual && left.externalDataIdRelationships.get(key).equals(right.externalDataIdRelationships.get(key));
        }

        return isEqual;
    }

}
