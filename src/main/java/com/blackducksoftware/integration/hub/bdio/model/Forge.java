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

    //@formatter:off
    public static final Forge ALPINE          = new Forge(SLASH, SLASH, "alpine");
    public static final Forge ANACONDA        = new Forge(EQUAL, SLASH, "anaconda");
    public static final Forge ANDROID         = new Forge(COLON, COLON, "android");
    public static final Forge APACHE_SOFTWARE = new Forge(SLASH, SLASH, "apache_software");
    public static final Forge BITBUCKET       = new Forge(SLASH, SLASH, "bitbucket");
    public static final Forge BOWER           = new Forge(NUMBR, SLASH, "bower");
    public static final Forge BUSYBOX         = new Forge(SLASH, SLASH, "busybox");
    public static final Forge CENTOS          = new Forge(SLASH, SLASH,"centos");
    public static final Forge COCOAPODS       = new Forge(COLON, COLON, "cocoapods");
    public static final Forge CODEPLEX        = new Forge(SLASH, SLASH, "codeplex");
    public static final Forge CODEPLEX_GROUP  = new Forge(SLASH, SLASH, "codeplex_group");
    public static final Forge CPAN            = new Forge(HYFEN, SLASH, "cpan");
    public static final Forge CPE             = new Forge(COLON, COLON, "cpe");
    public static final Forge CRAN            = new Forge(SLASH, SLASH, "cran");
    public static final Forge DEBIAN          = new Forge(SLASH, SLASH, "debian");
    public static final Forge FEDORA          = new Forge(SLASH, SLASH, "fedora");
    public static final Forge FREEDESKTOP_ORG = new Forge(SLASH, SLASH, "freedesktop_org");
    public static final Forge GITCAFE         = new Forge(SLASH, SLASH, "gitcafe");
    public static final Forge GITHUB          = new Forge(COLON, COLON, "github");
    public static final Forge GITLAB          = new Forge(SLASH, SLASH, "gitlab");
    public static final Forge GITORIOUS       = new Forge(SLASH, SLASH, "gitorious");
    public static final Forge GNU             = new Forge(SLASH, SLASH, "gnu");
    public static final Forge GOGET           = new Forge(EMPTY, SLASH, "goget");
    public static final Forge GOLANG          = new Forge(COLON, COLON, "golang");
    public static final Forge GOOGLECODE      = new Forge(SLASH, SLASH, "googlecode");
    public static final Forge HEX             = new Forge(SLASH, SLASH, "hex");
    public static final Forge JAVA_NET        = new Forge(SLASH, SLASH, "java_net");
    public static final Forge KDE_ORG         = new Forge(SLASH, SLASH, "kde_org");
    public static final Forge LAUNCHPAD       = new Forge(SLASH, SLASH, "launchpad");
    public static final Forge LONG_TAIL       = new Forge(SLASH, SLASH, "long_tail");
    public static final Forge MAVEN           = new Forge(COLON, COLON, "maven");
    public static final Forge NPM             = new Forge(ATSGN, SLASH, "npm");
    public static final Forge NUGET           = new Forge(SLASH, SLASH, "nuget");
    public static final Forge PACKAGIST       = new Forge(COLON, COLON, "packagist");
    public static final Forge PEAR            = new Forge(SLASH, SLASH, "pear");
    public static final Forge PYPI            = new Forge(SLASH, SLASH, "pypi");
    public static final Forge REDHAT          = new Forge(SLASH, SLASH, "redhat");
    public static final Forge RUBYFORGE       = new Forge(SLASH, SLASH, "rubyforge");
    public static final Forge RUBYGEMS        = new Forge(EQUAL, SLASH, "rubygems");
    public static final Forge SOURCEFORGE     = new Forge(SLASH, SLASH, "sourceforge");
    public static final Forge SOURCEFORGE_JP  = new Forge(SLASH, SLASH, "sourceforge_jp");
    public static final Forge UBUNTU          = new Forge(SLASH, SLASH, "ubuntu");
    //@formatter:on

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
