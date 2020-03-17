/**
 * integration-bdio
 *
 * Copyright (c) 2020 Synopsys, Inc.
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

import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.Stringable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A forge is always required. The other fields to populate depend on what external id type you need.
 * <p>
 * The supported types are:
 * - a path
 * - a list of moduleNames
 * - a name
 * - a name/version
 * - a name/version/architecture
 * - a name/architecture
 * - a group/name/version
 * - a group/name
 * - a layer/name/version
 * - a layer/name
 */
public class ExternalId extends Stringable {
    private static final int NAME_POSITION = 0;
    private static final int VERSION_POSITION = 1;

    private final Forge forge;
    private List<String> pieces = new ArrayList<>();
    private String prefix;
    private String suffix;

    public static ExternalId createFromExternalId(Forge forge, String fullExternalId, String name, String version) {
        List<String> unknownPieces = Arrays.stream(StringUtils.split(fullExternalId, forge.getSeparator()))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        ExternalId externalId = new ExternalId(forge);
        if (unknownPieces.isEmpty()) {
            return externalId;
        }

        if (unknownPieces.size() == 1) {
            // the external id is either a path, or it only includes the name
            String firstPiece = unknownPieces.get(0);
            if (firstPiece.equals(name)) {
                externalId.setName(firstPiece);
            } else {
                externalId.setPath(firstPiece);
            }

            return externalId;
        }

        if (!unknownPieces.contains(name)) {
            // without the name, the best we can do moduleNames
            externalId.setModuleNames(unknownPieces.toArray(new String[0]));

            return externalId;
        }

        // we know that name is involved now
        int indexToConsider = 0;
        if (unknownPieces.get(indexToConsider).equals(name)) {
            externalId.setName(name);
            indexToConsider++;
        } else {
            externalId.prefix = unknownPieces.get(indexToConsider++);
            externalId.setName(unknownPieces.get(indexToConsider++));
        }

        indexToConsider = considerVersion(version, externalId, unknownPieces, indexToConsider);

        if (unknownPieces.size() >= indexToConsider + 1) {
            externalId.suffix = unknownPieces.get(indexToConsider++);
        }

        // we may still have a case where we must use moduleNames - if there is content in unknown pieces
        // we haven't addressed, just reset and use all the pieces as moduleNames
        if (unknownPieces.size() >= indexToConsider + 1) {
            externalId.setModuleNames(unknownPieces.toArray(new String[0]));
        }

        return externalId;
    }

    private static int considerVersion(String version, ExternalId externalId, List<String> unknownPieces, int indexToConsider) {
        if (unknownPieces.size() >= indexToConsider + 1 && unknownPieces.get(indexToConsider).equals(version)) {
            externalId.setVersion(version);
            indexToConsider++;
        }

        return indexToConsider;
    }

    public ExternalId(Forge forge) {
        this.forge = forge;
        pieces.add(NAME_POSITION, null);
        pieces.add(VERSION_POSITION, null);
    }

    /**
     * The currently supported types are:
     * "name/version": populate name and version (if version is blank, only name is included)
     * "architecture": populate name, version, and architecture (if version is blank, only name is included)
     * "layer": populate name, version, and layer (if version is blank, only name is included)
     * "maven": populate name, version, and group (if version is blank, group and name are included)
     * "module names": populate moduleNames
     * "path": populate path
     */
    public String[] getExternalIdPieces() {
        List<String> externalIdPieces = new ArrayList<>();
        if (StringUtils.isNotBlank(prefix)) {
            externalIdPieces.add(prefix);
        }

        pieces
                .stream()
                .filter(StringUtils::isNotBlank)
                .forEach(externalIdPieces::add);

        if (StringUtils.isNotBlank(suffix)) {
            externalIdPieces.add(suffix);
        }

        return externalIdPieces.toArray(new String[0]);
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

    public Forge getForge() {
        return forge;
    }

    public String getLayer() {
        return getPrefix();
    }

    public void setLayer(String layer) {
        this.prefix = layer;
    }

    public String getGroup() {
        return getPrefix();
    }

    public void setGroup(String group) {
        this.prefix = group;
    }

    public String getName() {
        return pieces.get(NAME_POSITION);
    }

    public void setName(String name) {
        pieces.set(NAME_POSITION, name);
    }

    public String getVersion() {
        return pieces.get(VERSION_POSITION);
    }

    public void setVersion(String version) {
        pieces.set(VERSION_POSITION, version);
    }

    public String getArchitecture() {
        return getSuffix();
    }

    public void setArchitecture(String architecture) {
        this.suffix = architecture;
    }

    public String[] getModuleNames() {
        return pieces.toArray(new String[0]);
    }

    public void setModuleNames(String[] moduleNames) {
        prefix = null;
        suffix = null;
        pieces.set(NAME_POSITION, null);
        pieces.set(VERSION_POSITION, null);

        Arrays
                .stream(moduleNames)
                .filter(StringUtils::isNotBlank)
                .forEach(pieces::add);
    }

    public String getPath() {
        return pieces.get(NAME_POSITION);
    }

    public void setPath(String path) {
        prefix = null;
        suffix = null;
        pieces.set(NAME_POSITION, path);
        pieces.set(VERSION_POSITION, null);
    }

    // these methods should not really be considered part of this class's public API - they are meant to serve as common accessors for the other methods that ARE part of the public API
    protected String getPrefix() {
        return prefix;
    }

    protected String getSuffix() {
        return suffix;
    }

}
