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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Forge {
    public static final Forge ALPINE = slashForge("alpine");
    public static final Forge ANACONDA = slashForge("anaconda");
    public static final Forge APACHE_SOFTWARE = slashForge("apache_software");
    public static final Forge BITBUCKET = slashForge("bitbucket");
    public static final Forge BOWER = slashForge("bower");
    public static final Forge BUSYBOX = slashForge("busybox");
    public static final Forge CENTOS = slashForge("centos");
    public static final Forge CODEPLEX = slashForge("codeplex");
    public static final Forge CODEPLEX_GROUP = slashForge("codeplex_group");
    public static final Forge CPAN = slashForge("cpan");
    public static final Forge CRAN = slashForge("cran");
    public static final Forge DEBIAN = slashForge("debian");
    public static final Forge FEDORA = slashForge("fedora");
    public static final Forge FREEDESKTOP_ORG = slashForge("freedesktop_org");
    public static final Forge GITCAFE = slashForge("gitcafe");
    public static final Forge GITLAB = slashForge("gitlab");
    public static final Forge GITORIOUS = slashForge("gitorious");
    public static final Forge GOGET = slashForge("goget");
    public static final Forge GNU = slashForge("gnu");
    public static final Forge GOOGLECODE = slashForge("googlecode");
    public static final Forge HEX = slashForge("hex");
    public static final Forge JAVA_NET = slashForge("java_net");
    public static final Forge KDE_ORG = slashForge("kde_org");
    public static final Forge LAUNCHPAD = slashForge("launchpad");
    public static final Forge LONG_TAIL = slashForge("long_tail");
    public static final Forge NUGET = slashForge("nuget");
    public static final Forge NPM = slashForge("npm");
    public static final Forge PEAR = slashForge("pear");
    public static final Forge PYPI = slashForge("pypi");
    public static final Forge REDHAT = slashForge("redhat");
    public static final Forge RUBYFORGE = slashForge("rubyforge");
    public static final Forge RUBYGEMS = slashForge("rubygems");
    public static final Forge SOURCEFORGE = slashForge("sourceforge");
    public static final Forge SOURCEFORGE_JP = slashForge("sourceforge_jp");
    public static final Forge UBUNTU = slashForge("ubuntu");
    public static final Forge YOCTO = slashForge("yocto");

    public static final Forge ANDROID = colonForge("android");
    public static final Forge COCOAPODS = colonForge("cocoapods");
    public static final Forge CPE = colonForge("cpe");
    public static final Forge GITHUB = colonForge("github");
    public static final Forge GOLANG = colonForge("golang");
    public static final Forge MAVEN = colonForge("maven");
    public static final Forge PACKAGIST = colonForge("packagist");

    private final String name;
    private final String separator;
    private Boolean usePreferredNamespaceAlias;

    public Forge(final String separator, final String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("A non-blank name is required.");
        }

        this.name = name;
        this.separator = separator;
    }

    public Forge(final String separator, final String name, final boolean usePreferredNamespaceAlias) {
        this(separator, name);
        this.usePreferredNamespaceAlias = usePreferredNamespaceAlias;
    }

    /**
     * The various separators here are to support three systems 1) bdio, 2) Black Duck, 3) Black Duck Knowledge Base.
     *
     * To support matching in various Black Duck versions, the separators need to conform to the separators defined in the source of the bdio project here:
     * https://github.com/blackducksoftware/bdio/blob/2.1.1/bdio/src/main/java/com/blackducksoftware/bdio/model/ExternalIdentifierBuilder.java
     *
     * However, as of Black Duck 4.3.2, those separators are internally transformed to the KB separators, which are (currently) defined here: https://confluence.dc1.lan/display/KB/KB+Details+Service#KBDetailsService-ForgeNames
     *
     * As of 2017-11-29, only forward slashes and colons are used for KB separators.
     */

    private static Forge slashForge(final String forgeName) {
        return new Forge("/", forgeName);
    }

    private static Forge colonForge(final String forgeName) {
        return new Forge(":", forgeName);
    }

    public static Map<String, Forge> getKnownForges() {
        final Map<String, Forge> knownForges = new HashMap<>();

        final List<Field> knownStaticFinalForgeFields = Arrays
                                                            .stream(Forge.class.getFields())
                                                            .filter(f -> Modifier.isStatic(f.getModifiers()))
                                                            .filter(f -> Modifier.isFinal(f.getModifiers()))
                                                            .filter(f -> f.getType().isAssignableFrom(Forge.class))
                                                            .collect(Collectors.toList());

        for (final Field field : knownStaticFinalForgeFields) {
            try {
                final Forge forge = (Forge) field.get(null);
                knownForges.put(forge.getName(), forge);
            } catch (final IllegalAccessException ignored) {
                // okay to ignore, the filtering above took care of this
            }
        }

        return knownForges;
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
        return name;
    }

    public String getName() {
        final String formatString = usePreferredNamespaceAlias != null && usePreferredNamespaceAlias ? "@%s" : "%s";
        return String.format(formatString, name.toLowerCase());
    }

    public String getSeparator() {
        return separator;
    }

}
