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
package com.synopsys.integration.bdio.graph;

import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public interface DependencyGraph {
    public Set<Dependency> getRootDependencies();

    public Set<ExternalId> getRootDependencyExternalIds();

    public boolean hasDependency(Dependency dependency);

    public boolean hasDependency(ExternalId dependency);

    public Dependency getDependency(ExternalId dependency);

    public Set<Dependency> getChildrenForParent(Dependency parent);

    public Set<ExternalId> getChildrenExternalIdsForParent(Dependency parent);

    public Set<Dependency> getChildrenForParent(ExternalId parent);

    public Set<ExternalId> getChildrenExternalIdsForParent(ExternalId parent);

    public Set<ExternalId> getParentExternalIdsForChild(Dependency child);

    public Set<Dependency> getParentsForChild(ExternalId child);

    public Set<Dependency> getParentsForChild(Dependency child);

    public Set<ExternalId> getParentExternalIdsForChild(ExternalId child);

}
