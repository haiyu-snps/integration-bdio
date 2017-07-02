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

public class MavenExternalId extends NameVersionExternalId {
    public String group;

    public MavenExternalId(final Forge forge, final String group, final String artifact, final String version) {
        super(forge, artifact, version);
        this.group = group;
    }

    /**
     * A MavenExternalId should always use the maven Forge.
     */
    public MavenExternalId(final String group, final String artifact, final String version) {
        this(Forge.MAVEN, group, artifact, version);
    }

    @Override
    public String[] getExternalIdPieces() {
        return new String[] { group, name, version };
    }

}