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
package com.blackducksoftware.integration.hub.bdio.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Forge {
    public static final Map<String, Forge> FORGE_NAME_TO_FORGE = new HashMap<>();

    public static final Forge ALPINE = new Forge("alpine", "/");
    public static final Forge ANACONDA = new Forge("anaconda", "=");
    public static final Forge BOWER = new Forge("bower", "#");
    public static final Forge BUSYBOX = new Forge("busybox", "/");
    public static final Forge CENTOS = new Forge("centos", "/");
    public static final Forge COCOAPODS = new Forge("cocoapods", ":");
    public static final Forge CPAN = new Forge("cpan", "-");
    public static final Forge CRAN = new Forge("cran", "/");
    public static final Forge DEBIAN = new Forge("debian", "/");
    public static final Forge FEDORA = new Forge("fedora", "/");
    public static final Forge GOGET = new Forge("goget", "");
    public static final Forge GOLANG = new Forge("golang", ":");
    public static final Forge HEX = new Forge("hex", "-");
    public static final Forge MAVEN = new Forge("maven", ":");
    public static final Forge NPM = new Forge("npm", "@");
    public static final Forge NUGET = new Forge("nuget", "/");
    public static final Forge PACKAGIST = new Forge("packagist", ":");
    public static final Forge PEAR = new Forge("pear", "/");
    public static final Forge PYPI = new Forge("pypi", "/");
    public static final Forge REDHAT = new Forge("redhat", "/");
    public static final Forge RUBYGEMS = new Forge("rubygems", "=");
    public static final Forge UBUNTU = new Forge("ubuntu", "/");

    private final String name;
    private final String separator;

    public Forge(final String name, final String separator) {
        this.name = name;
        this.separator = separator;
        FORGE_NAME_TO_FORGE.put(name, this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public String getSeparator() {
        return separator;
    }

}
