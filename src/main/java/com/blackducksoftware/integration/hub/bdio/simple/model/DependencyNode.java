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

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;

/**
 * The externalId is required, but name, version, and children are all optional.
 */
public class DependencyNode {
    public String name;

    public String version;

    public ExternalId externalId;

    public List<DependencyNode> children = new ArrayList<>();

    public DependencyNode(final String name, final String version, final ExternalId externalId, final List<DependencyNode> children) {
        this.name = name;
        this.version = version;
        this.externalId = externalId;
        this.children = children;
    }

    public DependencyNode(final String name, final String version, final ExternalId externalId) {
        this(name, version, externalId, new ArrayList<DependencyNode>());
    }

    public DependencyNode(final String name, final ExternalId externalId, final List<DependencyNode> children) {
        this(name, null, externalId, children);
    }

    public DependencyNode(final ExternalId externalId, final List<DependencyNode> children) {
        this(null, externalId, children);
    }

    public DependencyNode(final ExternalId externalId) {
        this(externalId, new ArrayList<DependencyNode>());
    }

}
