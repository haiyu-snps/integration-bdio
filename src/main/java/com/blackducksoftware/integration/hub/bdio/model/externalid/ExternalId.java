/**
 * integration-bdio
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.bdio.model.externalid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;

public class ExternalId {
    public static final String BDIO_ID_SEPARATOR = "/";

    public final Forge forge;
    public String group;
    public String name;
    public String version;
    public String architecture;
    public String[] moduleNames;
    public String path;

    private static final IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil();

    public ExternalId(final Forge forge) {
        this.forge = forge;
    }

    /**
     * @formatter:off
     * A forge is always required. The other fields to populate depend on what
     * external id type you need. The currently supported types are:
     *   "name/version": populate name and version
     *   "architecture": populate name, version, and architecture
     *   "maven": populate name, version, and group
     *   "module names": populate moduleNames
     *   "path": populate path
     * @formatter:on
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
            } else {
                return new String[] { name, version };
            }
        }

        // if we can't be positive about what kind of external id we are, just give everything we have in a reasonable order
        final List<String> bestGuessPieces = new ArrayList<>();

        final List<String> bestGuessCandidates = Arrays.asList(group, name, version, architecture);
        for (final String candidate : bestGuessCandidates) {
            if (StringUtils.isNotBlank(candidate)) {
                bestGuessPieces.add(candidate);
            }
        }

        return bestGuessPieces.toArray(new String[bestGuessPieces.size()]);
    }

    public String createBdioId() {
        final List<String> bdioIdPieces = new ArrayList<>();
        bdioIdPieces.add(forge.toString());
        bdioIdPieces.addAll(integrationEscapeUtil.escapePiecesForUri(Arrays.asList(getExternalIdPieces())));

        return "http:" + StringUtils.join(bdioIdPieces, BDIO_ID_SEPARATOR);
    }

    public String createExternalId() {
        return StringUtils.join(getExternalIdPieces(), forge.getSeparator());
    }

    public String createHubOriginId() {
        return StringUtils.join(getExternalIdPieces(), forge.getKbSeparator());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, RecursiveToStringStyle.JSON_STYLE);
    }

}
