/**
 * Hub Bdio Simple
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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BdioNode {
    @SerializedName("@id")
    private String id;

    @SerializedName("@type")
    private String type;

    @SerializedName("name")
    private String name;

    @SerializedName("externalIdentifier")
    private BdioExternalIdentifier bdioExternalIdentifier;

    @SerializedName("relationship")
    List<BdioRelationship> relationship;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BdioExternalIdentifier getBdioExternalIdentifier() {
        return bdioExternalIdentifier;
    }

    public void setBdioExternalIdentifier(final BdioExternalIdentifier bdioExternalIdentifier) {
        this.bdioExternalIdentifier = bdioExternalIdentifier;
    }

    public List<BdioRelationship> getRelationship() {
        return relationship;
    }

    public void setRelationship(final List<BdioRelationship> relationship) {
        this.relationship = relationship;
    }

    public void addRelationship(final BdioRelationship singleRelationship) {
        if (relationship == null) {
            relationship = new ArrayList<>();
        }
        relationship.add(singleRelationship);
    }

}
