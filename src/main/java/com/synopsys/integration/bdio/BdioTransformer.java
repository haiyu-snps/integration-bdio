/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.BdioRelationship;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class BdioTransformer {
    private final Map<String, Forge> forgeMap;

    public BdioTransformer() {
        forgeMap = Forge.getKnownForges();
    }

    public BdioTransformer(Map<String, Forge> forgeMap) {
        this.forgeMap = forgeMap;
    }

    public DependencyGraph transformToDependencyGraph(BdioProject project, List<BdioComponent> components) {
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        Map<BdioId, Dependency> bdioIdToDependencyMap = new HashMap<>();

        for (BdioComponent component : components) {
            ExternalId externalId = component.bdioExternalIdentifier.externalIdMetaData;
            if (externalId == null) {
                // if the integration has not set the metadata, try our best to guess it
                Forge forge = forgeMap.get(component.bdioExternalIdentifier.forge);
                externalId = ExternalId.createFromExternalId(forge, component.bdioExternalIdentifier.externalId, component.name, component.version);
            }
            Dependency dependency = new Dependency(component.name, component.version, externalId);
            bdioIdToDependencyMap.put(component.id, dependency);
        }

        for (BdioRelationship relation : project.relationships) {
            dependencyGraph.addChildrenToRoot(bdioIdToDependencyMap.get(relation.related));
        }

        for (BdioComponent component : components) {
            Dependency dependency = bdioIdToDependencyMap.get(component.id);
            for (BdioRelationship relation : component.relationships) {
                dependencyGraph.addParentWithChild(dependency, bdioIdToDependencyMap.get(relation.related));
            }
        }

        return dependencyGraph;
    }

}
