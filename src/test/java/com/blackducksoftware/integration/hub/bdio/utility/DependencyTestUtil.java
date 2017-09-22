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
package com.blackducksoftware.integration.hub.bdio.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.MavenExternalId;

public class DependencyTestUtil {

    public static Dependency newMavenDependency(final String name, final String version, final String org) {
        return new Dependency(name, version, new MavenExternalId(Forge.MAVEN, org, name, version));
    }

    public static Set<Dependency> asSet(final Dependency... dependencies) {
        final Set<Dependency> set = new HashSet<>();
        for (final Dependency dependency : dependencies) {
            set.add(dependency);
        }
        return set;
    }

    public static List<Dependency> asList(final Dependency... dependencies) {
        final List<Dependency> list = new ArrayList<>();
        for (final Dependency dependency : dependencies) {
            list.add(dependency);
        }
        return list;
    }

}
