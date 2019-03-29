/**
 * integration-bdio
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.bdio.model;

public class SpdxCreator {
    public String type;
    public String identifier;

    public SpdxCreator(final String type, final String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public String getData() {
        return String.format("%s: %s", type, identifier);
    }

    public static SpdxCreator createToolSpdxCreator(final String name, final String version) {
        final String identifier = String.format("%s-%s", name, version);
        final SpdxCreator spdxCreator = new SpdxCreator("Tool", identifier);
        return spdxCreator;
    }

}
