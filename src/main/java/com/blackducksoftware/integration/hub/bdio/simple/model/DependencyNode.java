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

import java.util.LinkedHashSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;

/**
 * The externalId is required, but name, version, and children are all optional. However, most usage assumes the project name/version are set on the root DependencyNode.
 */
public class DependencyNode {
    public String name;

    public String version;

    public ExternalId externalId;

    // we are using the concrete class here because the ordering of children nodes is important
    public LinkedHashSet<DependencyNode> children = new LinkedHashSet<>();

    public DependencyNode(final String name, final String version, final ExternalId externalId, final LinkedHashSet<DependencyNode> children) {
        this.name = name;
        this.version = version;
        this.externalId = externalId;
        this.children = children;
    }

    public DependencyNode(final String name, final String version, final ExternalId externalId) {
        this(name, version, externalId, new LinkedHashSet<DependencyNode>());
    }

    public DependencyNode(final String name, final ExternalId externalId, final LinkedHashSet<DependencyNode> children) {
        this(name, null, externalId, children);
    }

    public DependencyNode(final ExternalId externalId, final LinkedHashSet<DependencyNode> children) {
        this(null, externalId, children);
    }

    public DependencyNode(final ExternalId externalId) {
        this(externalId, new LinkedHashSet<DependencyNode>());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "children");
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "children");
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, RecursiveToStringStyle.JSON_STYLE);
    }

}
