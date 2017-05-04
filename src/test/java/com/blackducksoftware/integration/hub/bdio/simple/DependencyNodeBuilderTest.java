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
package com.blackducksoftware.integration.hub.bdio.simple;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;

public class DependencyNodeBuilderTest {
    DependencyNode root = new DependencyNode("root", "1.0", new MavenExternalId(Forge.maven, "testRoot", "root", "1.0"));

    DependencyNode firstChild = new DependencyNode("first", "1.0", new MavenExternalId(Forge.maven, "children", "first", "1.0"));

    DependencyNode secondChild = new DependencyNode("second", "2.0", new MavenExternalId(Forge.maven, "children", "second", "2.0"));

    DependencyNode thirdChild = new DependencyNode("third", "3.0", new MavenExternalId(Forge.maven, "children", "third", "3.0"));

    DependencyNode fourthChild = new DependencyNode("fourth", "4.0", new MavenExternalId(Forge.maven, "children", "fourth", "4.0"));

    DependencyNode subFirstChild = new DependencyNode("first", "1.0", new MavenExternalId(Forge.maven, "subChild", "first", "1.0"));

    DependencyNode subSecondChild = new DependencyNode("second", "2.0", new MavenExternalId(Forge.maven, "subChild", "second", "2.0"));

    DependencyNode subThirdChild = new DependencyNode("third", "3.0", new MavenExternalId(Forge.maven, "subChild", "third", "3.0"));

    private DependencyNode getRootNodeToCompareWith() {
        // Constructing the root node in a specific structure
        final Set<DependencyNode> children = new HashSet<>();
        firstChild.children.addAll(Arrays.asList(subFirstChild, subSecondChild));
        secondChild.children.add(subThirdChild);
        fourthChild.children.addAll(Arrays.asList(subFirstChild, subSecondChild, subThirdChild));

        children.add(firstChild);
        children.add(secondChild);
        children.add(thirdChild);
        children.add(fourthChild);

        root.children = children;

        return root;
    }

    @Test
    public void testDependencyNodeBuilder() {
        final DependencyNode rootToCompareTo = getRootNodeToCompareWith();

        // Adding the relationships randomly
        final DependencyNodeBuilder builder = new DependencyNodeBuilder(root);
        builder.addParentNodeWithChildren(secondChild, Arrays.asList(subThirdChild));
        builder.addChildNodeWithParents(subSecondChild, Arrays.asList(fourthChild, firstChild));
        builder.addChildNodeWithParents(subFirstChild, Arrays.asList(fourthChild, firstChild));
        builder.addParentNodeWithChildren(root, Arrays.asList(firstChild, secondChild, thirdChild, fourthChild));
        builder.addChildNodeWithParents(subThirdChild, Arrays.asList(fourthChild));

        compareNode(rootToCompareTo, root);
    }

    private void compareNode(final DependencyNode expected, final DependencyNode actual) {
        Assert.assertEquals(expected.name, actual.name);
        Assert.assertEquals(expected.version, actual.version);
        Assert.assertEquals(expected.externalId.createDataId(), actual.externalId.createDataId());
        Assert.assertEquals(expected.children.size(), actual.children.size());

        for (final DependencyNode expectedChild : expected.children) {
            final String expectedDataId = expectedChild.externalId.createDataId();
            boolean foundMatch = false;
            for (final DependencyNode actualChild : actual.children) {
                if (expectedDataId.equals(actualChild.externalId.createDataId())) {
                    foundMatch = true;
                    compareNode(expectedChild, actualChild);
                }
            }
            Assert.assertTrue(foundMatch);
        }
    }
}
