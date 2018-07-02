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
package com.blackducksoftware.integration.hub.bdio.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BdioCreationInfo {
    @SerializedName("spdx:creator")
    private final List<String> creator = new ArrayList<>();

    @SerializedName("spdx:created")
    public String created;

    // ekerwin 2018-06-11
    // The Hub only supports a single creator and if there are multiple creators, the Hub will use only the first one.
    public void setPrimarySpdxCreator(final SpdxCreator spdxCreator) {
        creator.add(0, spdxCreator.getData());
    }

    public void addSpdxCreator(final SpdxCreator spdxCreator) {
        creator.add(spdxCreator.getData());
    }

    public List<String> getCreator() {
        return Collections.unmodifiableList(creator);
    }

}
