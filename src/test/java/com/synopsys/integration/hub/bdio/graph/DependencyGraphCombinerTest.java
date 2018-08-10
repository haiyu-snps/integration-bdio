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
package com.synopsys.integration.hub.bdio.graph;

import org.junit.Test;

import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.utility.DependencyGraphTestUtil;
import com.synopsys.integration.hub.bdio.utility.DependencyTestUtil;

public class DependencyGraphCombinerTest {
    Dependency dep1 = DependencyTestUtil.newMavenDependency("first", "1.0", "children");
    Dependency dep2 = DependencyTestUtil.newMavenDependency("second", "2.0", "children");
    Dependency dep3 = DependencyTestUtil.newMavenDependency("third", "3.0", "children");
    Dependency dep4 = DependencyTestUtil.newMavenDependency("fourth", "4.0", "children");
    Dependency dep5 = DependencyTestUtil.newMavenDependency("first", "1.0", "subChild");
    Dependency dep6 = DependencyTestUtil.newMavenDependency("second", "2.0", "subChild");
    Dependency dep7 = DependencyTestUtil.newMavenDependency("third", "3.0", "subChild");

    @Test
    public void testAddChildWithParents() {
        final MutableDependencyGraph first = new MutableMapDependencyGraph();
        final MutableDependencyGraph second = new MutableMapDependencyGraph();
        final MutableDependencyGraph combined = new MutableMapDependencyGraph();

        first.addChildToRoot(dep1);
        first.addChildWithParent(dep2, dep1);
        first.addChildWithParent(dep3, dep2);
        DependencyGraphTestUtil.assertGraphChildren(first, dep1, dep2);

        second.addChildToRoot(dep4);
        second.addParentWithChild(dep4, dep5);
        second.addParentWithChild(dep5, dep6);
        second.addParentWithChild(dep5, dep7);

        combined.addGraphAsChildrenToRoot(first);
        combined.addGraphAsChildrenToParent(dep2, second);

        DependencyGraphTestUtil.assertGraphRootChildren(combined, dep1);

        DependencyGraphTestUtil.assertGraphChildren(combined, dep1, dep2);
        DependencyGraphTestUtil.assertGraphChildren(combined, dep2, dep3, dep4);
        DependencyGraphTestUtil.assertGraphChildren(combined, dep4, dep5);
        DependencyGraphTestUtil.assertGraphChildren(combined, dep5, dep6, dep7);
    }

}
