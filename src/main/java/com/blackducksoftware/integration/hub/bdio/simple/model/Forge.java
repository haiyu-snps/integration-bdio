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
package com.blackducksoftware.integration.hub.bdio.simple.model;

import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ArchitectureExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ModuleNamesExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId;

public enum Forge {
    anaconda("=", NameVersionExternalId.class),
    bower("#", NameVersionExternalId.class),
    centos("/", ArchitectureExternalId.class),
    cocoapods(":", NameVersionExternalId.class),
    cpan("::", ModuleNamesExternalId.class),
    goget("", PathExternalId.class),
    maven(":", MavenExternalId.class),
    npm("@", NameVersionExternalId.class),
    nuget("/", NameVersionExternalId.class),
    pypi("/", NameVersionExternalId.class),
    rubygems("=", NameVersionExternalId.class);

    public final String separator;

    public final Class<? extends ExternalId> externalIdClass;

    private Forge(final String separator, final Class<? extends ExternalId> externalIdClass) {
        this.separator = separator;
        this.externalIdClass = externalIdClass;
    }

}
