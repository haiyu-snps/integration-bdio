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
package com.synopsys.integration.hub.bdio.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Forge {
    public static final Map<String, Forge> FORGE_NAME_TO_FORGE = new HashMap<>();

    /**
     * The various separators here are to support three systems 1) bdio, 2) Black Duck Hub, 3) Black Duck Knowledge Base.
     *
     * To support matching in various Hub versions, the separators need to conform to the separators defined in the source of the bdio project here:
     * https://github.com/blackducksoftware/bdio/blob/2.1.1/bdio/src/main/java/com/blackducksoftware/bdio/model/ExternalIdentifierBuilder.java
     *
     * However, as of Hub 4.3.2, those separators are internally transformed to the KB separators, which are (currently) defined here: https://confluence.dc1.lan/display/KB/KB+Details+Service#KBDetailsService-ForgeNames
     *
     * As of 2017-11-29, only forward slashes and colons are used for KB separators.
     */

    // five character variable names for the separators to maintain the column formatting below
    private static final String EMPTY = "";
    private static final String SLASH = "/";
    private static final String NUMBR = "#";
    private static final String COLON = ":";
    private static final String ATSGN = "@";
    private static final String EQUAL = "=";
    private static final String HYFEN = "-";

    private static final Forge createSlashSlashForge(final String forgeName) {
        return new Forge("/", "/", forgeName);
    }

    private static final Forge createColonColonForge(final String forgeName) {
        return new Forge(":", ":", forgeName);
    }

    private static final Forge createEqualSlashForge(final String forgeName) {
        return new Forge("=", "/", forgeName);
    }

    public static final Forge ALPINE = createSlashSlashForge("alpine");
    public static final Forge APACHE_SOFTWARE = createSlashSlashForge("apache_software");
    public static final Forge BITBUCKET = createSlashSlashForge("bitbucket");
    public static final Forge BUSYBOX = createSlashSlashForge("busybox");
    public static final Forge CENTOS = createSlashSlashForge("centos");
    public static final Forge CODEPLEX = createSlashSlashForge("codeplex");
    public static final Forge CODEPLEX_GROUP = createSlashSlashForge("codeplex_group");
    public static final Forge CRAN = createSlashSlashForge("cran");
    public static final Forge DEBIAN = createSlashSlashForge("debian");
    public static final Forge FEDORA = createSlashSlashForge("fedora");
    public static final Forge FREEDESKTOP_ORG = createSlashSlashForge("freedesktop_org");
    public static final Forge GITCAFE = createSlashSlashForge("gitcafe");
    public static final Forge GITLAB = createSlashSlashForge("gitlab");
    public static final Forge GITORIOUS = createSlashSlashForge("gitorious");
    public static final Forge GNU = createSlashSlashForge("gnu");
    public static final Forge GOOGLECODE = createSlashSlashForge("googlecode");
    public static final Forge HEX = createSlashSlashForge("hex");
    public static final Forge JAVA_NET = createSlashSlashForge("java_net");
    public static final Forge KDE_ORG = createSlashSlashForge("kde_org");
    public static final Forge LAUNCHPAD = createSlashSlashForge("launchpad");
    public static final Forge LONG_TAIL = createSlashSlashForge("long_tail");
    public static final Forge NUGET = createSlashSlashForge("nuget");
    public static final Forge PEAR = createSlashSlashForge("pear");
    public static final Forge PYPI = createSlashSlashForge("pypi");
    public static final Forge REDHAT = createSlashSlashForge("redhat");
    public static final Forge RUBYFORGE = createSlashSlashForge("rubyforge");
    public static final Forge SOURCEFORGE = createSlashSlashForge("sourceforge");
    public static final Forge SOURCEFORGE_JP = createSlashSlashForge("sourceforge_jp");
    public static final Forge UBUNTU = createSlashSlashForge("ubuntu");
    public static final Forge YOCTO = createSlashSlashForge("yocto");

    public static final Forge ANDROID = createColonColonForge("android");
    public static final Forge COCOAPODS = createColonColonForge("cocoapods");
    public static final Forge CPE = createColonColonForge("cpe");
    public static final Forge GITHUB = createColonColonForge("github");
    public static final Forge GOLANG = createColonColonForge("golang");
    public static final Forge MAVEN = createColonColonForge("maven");
    public static final Forge PACKAGIST = createColonColonForge("packagist");

    public static final Forge ANACONDA = createEqualSlashForge("anaconda");
    public static final Forge RUBYGEMS = createEqualSlashForge("rubygems");

    public static final Forge BOWER = new Forge("#", "/", "bower");
    public static final Forge CPAN = new Forge("-", "/", "cpan");
    public static final Forge GOGET = new Forge("", "/", "goget");
    public static final Forge NPM = new Forge("@", "/", "npm");

    private final String name;
    private final String separator;
    private final String kbSeparator;

    public Forge(final String separator, final String kbSeparator, final String name) {
        this.name = name;
        this.separator = separator;
        this.kbSeparator = kbSeparator;
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

    public String getKbSeparator() {
        return kbSeparator;
    }

}
