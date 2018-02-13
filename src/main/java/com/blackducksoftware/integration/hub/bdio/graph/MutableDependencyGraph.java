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
package com.blackducksoftware.integration.hub.bdio.graph;

import java.util.List;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;

public interface MutableDependencyGraph extends DependencyGraph {
    public void addGraphAsChildrenToRoot(DependencyGraph sourceGraph);

    public void addGraphAsChildrenToParent(Dependency parent, DependencyGraph sourceGraph);

    public void addParentWithChild(final Dependency parent, final Dependency child);

    public void addParentWithChildren(final Dependency parent, final List<Dependency> children);

    public void addParentWithChildren(final Dependency parent, final Set<Dependency> children);

    public void addParentWithChildren(final Dependency parent, final Dependency... children);

    public void addChildWithParent(final Dependency child, final Dependency parent);

    public void addChildWithParents(final Dependency child, final List<Dependency> parents);

    public void addChildWithParents(final Dependency child, final Set<Dependency> parents);

    public void addChildWithParents(final Dependency child, final Dependency... parents);

    public void addChildToRoot(final Dependency child);

    public void addChildrenToRoot(final List<Dependency> children);

    public void addChildrenToRoot(final Set<Dependency> children);

    public void addChildrenToRoot(final Dependency... children);

}
