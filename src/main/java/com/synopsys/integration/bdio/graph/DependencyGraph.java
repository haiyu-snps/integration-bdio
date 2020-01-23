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
package com.synopsys.integration.bdio.graph;

import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public interface DependencyGraph {
    Set<Dependency> getRootDependencies();

    Set<ExternalId> getRootDependencyExternalIds();

    boolean hasDependency(Dependency dependency);

    boolean hasDependency(ExternalId dependency);

    Dependency getDependency(ExternalId dependency);

    Set<Dependency> getChildrenForParent(Dependency parent);

    Set<ExternalId> getChildrenExternalIdsForParent(Dependency parent);

    Set<Dependency> getChildrenForParent(ExternalId parent);

    Set<ExternalId> getChildrenExternalIdsForParent(ExternalId parent);

    Set<ExternalId> getParentExternalIdsForChild(Dependency child);

    Set<Dependency> getParentsForChild(ExternalId child);

    Set<Dependency> getParentsForChild(Dependency child);

    Set<ExternalId> getParentExternalIdsForChild(ExternalId child);

}
