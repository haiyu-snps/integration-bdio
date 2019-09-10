/**
 * integration-bdio
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.bdio.model.externalid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.util.Stringable;

public class ExternalId extends Stringable {
    public final Forge forge;
    public String layer;
    public String group;
    public String name;
    public String version;
    public String architecture;
    public String[] moduleNames;
    public String path;

    public ExternalId(Forge forge) {
        this.forge = forge;
    }

    /**
     * A forge is always required. The other fields to populate depend on what
     * external id type you need. The currently supported types are:
     * "name/version": populate name and version (if version is blank, only name is included)
     * "architecture": populate name, version, and architecture (if version is blank, only name is included)
     * "layer": populate name, version, and layer (if version is blank, only name is included)
     * "maven": populate name, version, and group (if version is blank, group and name are included)
     * "module names": populate moduleNames
     * "path": populate path
     */
    public String[] getExternalIdPieces() {
        if (StringUtils.isNotBlank(path)) {
            return new String[] { path };
        } else if (moduleNames != null && moduleNames.length > 0) {
            return moduleNames;
        } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(version)) {
            if (StringUtils.isNotBlank(group)) {
                return new String[] { group, name, version };
            } else if (StringUtils.isNotBlank(architecture)) {
                return new String[] { name, version, architecture };
            } else if (StringUtils.isNotBlank(layer)) {
                return new String[] { layer, name, version };
            } else {
                return new String[] { name, version };
            }
        } else if (StringUtils.isNotBlank(name)) {
            //Black Duck now (2019.6.0) supports version-less components
            if (StringUtils.isNotBlank(group)) {
                return new String[] { group, name };
            } else if (StringUtils.isNotBlank(layer)) {
                return new String[] { layer, name };
            } else {
                return new String[] { name };
            }
        }

        // if we can't be positive about what kind of external id we are, just give everything we have in a reasonable order
        List<String> bestGuessPieces = new ArrayList<>();

        final List<String> bestGuessCandidates = Arrays.asList(layer, group, name, version, architecture);
        for (String candidate : bestGuessCandidates) {
            if (StringUtils.isNotBlank(candidate)) {
                bestGuessPieces.add(candidate);
            }
        }

        return bestGuessPieces.toArray(new String[0]);
    }

    public BdioId createBdioId() {
        List<String> bdioIdPieces = new ArrayList<>();
        bdioIdPieces.add(forge.toString());
        bdioIdPieces.addAll((Arrays.asList(getExternalIdPieces())));

        return BdioId.createFromPieces(bdioIdPieces);
    }

    public String createExternalId() {
        return StringUtils.join(getExternalIdPieces(), forge.getSeparator());
    }

}
