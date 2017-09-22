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
package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public class ExternalIdFactory {
    public ExternalId createNameVersionExternalId(final Forge forge, final String name, final String version) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.name = name;
        externalId.version = version;
        return externalId;
    }

    public ExternalId createMavenExternalId(final String group, final String name, final String version) {
        final ExternalId externalId = createNameVersionExternalId(Forge.MAVEN, name, version);
        externalId.group = group;
        return externalId;
    }

    public ExternalId createArchitectureExternalId(final Forge forge, final String name, final String version, final String architecture) {
        final ExternalId externalId = createNameVersionExternalId(forge, name, version);
        externalId.architecture = architecture;
        return externalId;
    }

    public ExternalId createModuleNamesExternalId(final Forge forge, final String... moduleNames) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.moduleNames = moduleNames;
        return externalId;
    }

    public ExternalId createPathExternalId(final Forge forge, final String path) {
        final ExternalId externalId = new ExternalId(forge);
        externalId.path = path;
        return externalId;
    }

}
