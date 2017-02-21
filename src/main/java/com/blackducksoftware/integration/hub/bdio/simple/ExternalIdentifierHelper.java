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

public class ExternalIdentifierHelper {
    private final BdioHelper bdioHelper;

    public ExternalIdentifierHelper(final BdioHelper bdioHelper) {
        this.bdioHelper = bdioHelper;
    }

    public String createBdioId(final String group, final String artifact, final String version) {
        return String.format("data:%s/%s/%s", group, artifact, version);
    }

    public String createBdioId(final String name, final String version) {
        return String.format("data:%s/%s", name, version);
    }

    public BdioExternalIdentifier createMavenExternalIdentifier(final String group, final String artifact, final String version) {
        return bdioHelper.createExternalIdentifier("maven", String.format("%s:%s:%s", group, artifact, version));
    }

    /**
     * Pypi is a forge for python
     */
    public BdioExternalIdentifier createPypiExternalIdentifier(final String name, final String version) {
        return bdioHelper.createExternalIdentifier("pypi", String.format("%s/%s", name, version));
    }

    public BdioExternalIdentifier createNugetExternalIdentifier(final String name, final String version) {
        return bdioHelper.createExternalIdentifier("nuget", String.format("%s/%s", name, version));
    }

    public BdioExternalIdentifier createNpmExternalIdentifier(final String name, final String version) {
        return bdioHelper.createExternalIdentifier("npm", String.format("%s@%s", name, version));
    }

    public BdioExternalIdentifier createRubygemsExternalIdentifier(final String name, final String version) {
        return bdioHelper.createExternalIdentifier("rubygems", String.format("%s=%s", name, version));
    }

}
