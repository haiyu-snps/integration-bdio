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
package com.blackducksoftware.integration.hub.bdio.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.model.BdioComponent;
import com.blackducksoftware.integration.hub.bdio.model.BdioRelationship;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;

public class BdioTransformer {
    private final Map<String, Forge> forgeMap;

    public BdioTransformer(final Map<String, Forge> forgeMap) {
        this.forgeMap = forgeMap;
    }

    // returns a dependency node that is the project
    public Dependency transformToDependencyNode(final SimpleBdioDocument document) {
        final List<BdioComponent> components = new ArrayList<>(document.components);
        components.add(document.project);

        final Map<String, Dependency> map = componentsToNodes(components);

        return map.get(document.project.id);

    }

    public Map<String, Dependency> componentsToNodes(final List<BdioComponent> components) {
        final Map<String, Dependency> map = new HashMap<>();

        for (final BdioComponent component : components) {
            final Forge forge = forgeMap.get(component.bdioExternalIdentifier.forge);
            final ExternalId id = uncreateExternalId(forge, component.bdioExternalIdentifier.externalId, component.name, component.version);
            final Dependency dependency = new Dependency(component.name, component.version, id);
            map.put(component.id, dependency);
        }

        for (final BdioComponent component : components) {
            final Dependency dependency = map.get(component.id);
            for (final BdioRelationship relation : component.relationships) {
                dependency.children.add(map.get(relation.related));
            }
        }

        return map;
    }

    public ExternalId uncreateExternalId(final Forge forge, final String fullExternalId, final String name, final String revision) {
        final String[] pieces = StringUtils.split(fullExternalId, forge.getSeparator());
        final ExternalId id = new ExternalId(forge);

        if (pieces.length == 1) {
            // assume path? could be a 1 length module id?
            id.path = pieces[0];
        } else if (pieces.length == 2 || pieces.length == 3) {
            if (pieces[0].equals(name)) {
                id.name = pieces[0];
                id.version = pieces[1];
                if (pieces.length > 2) {
                    id.architecture = pieces[2];
                }
            } else if (pieces[1].equals(name) && pieces[2].equals(revision)) {
                id.group = pieces[0];
                id.name = pieces[1];
                id.version = pieces[2];
            } else {
                id.moduleNames = pieces;
            }
        } else {
            id.moduleNames = pieces;
        }

        return id;
    }

}
