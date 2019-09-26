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

import com.synopsys.integration.util.Stringable;

public class Forge extends Stringable {
    // forges that use the slash as the separator
    public static final Forge ALPINE = new Forge("/", "alpine");
    public static final Forge ANACONDA = new Forge("/", "anaconda");
    public static final Forge APACHE_SOFTWARE = new Forge("/", "apache_software");
    public static final Forge BITBUCKET = new Forge("/", "bitbucket");
    public static final Forge BOWER = new Forge("/", "bower");
    public static final Forge BUSYBOX = new Forge("/", "busybox");
    public static final Forge CENTOS = new Forge("/", "centos");
    public static final Forge CODEPLEX = new Forge("/", "codeplex");
    public static final Forge CODEPLEX_GROUP = new Forge("/", "codeplex_group");
    public static final Forge CPAN = new Forge("/", "cpan");
    public static final Forge CRAN = new Forge("/", "cran");
    public static final Forge DEBIAN = new Forge("/", "debian");
    public static final Forge FEDORA = new Forge("/", "fedora");
    public static final Forge FREEDESKTOP_ORG = new Forge("/", "freedesktop_org");
    public static final Forge GITCAFE = new Forge("/", "gitcafe");
    public static final Forge GITLAB = new Forge("/", "gitlab");
    public static final Forge GITORIOUS = new Forge("/", "gitorious");
    public static final Forge GOGET = new Forge("/", "goget");
    public static final Forge GNU = new Forge("/", "gnu");
    public static final Forge GOOGLECODE = new Forge("/", "googlecode");
    public static final Forge HEX = new Forge("/", "hex");
    public static final Forge JAVA_NET = new Forge("/", "java_net");
    public static final Forge KDE_ORG = new Forge("/", "kde_org");
    public static final Forge LAUNCHPAD = new Forge("/", "launchpad");
    public static final Forge LONG_TAIL = new Forge("/", "long_tail");
    public static final Forge NUGET = new Forge("/", "nuget");
    public static final Forge NPMJS = new Forge("/", "npmjs");
    public static final Forge PEAR = new Forge("/", "pear");
    public static final Forge PYPI = new Forge("/", "pypi");
    public static final Forge REDHAT = new Forge("/", "redhat");
    public static final Forge RUBYFORGE = new Forge("/", "rubyforge");
    public static final Forge RUBYGEMS = new Forge("/", "rubygems");
    public static final Forge SOURCEFORGE = new Forge("/", "sourceforge");
    public static final Forge SOURCEFORGE_JP = new Forge("/", "sourceforge_jp");
    public static final Forge UBUNTU = new Forge("/", "ubuntu");
    public static final Forge YOCTO = new Forge("/", "yocto");

    // forges that use the colon as the separator
    public static final Forge ANDROID = new Forge(":", "android");
    public static final Forge COCOAPODS = new Forge(":", "cocoapods");
    public static final Forge CPE = new Forge(":", "cpe");
    public static final Forge GITHUB = new Forge(":", "github");
    public static final Forge GOLANG = new Forge(":", "golang");
    public static final Forge MAVEN = new Forge(":", "maven");
    public static final Forge PACKAGIST = new Forge(":", "packagist");

    private final String name;
    private final String separator;
    private Boolean usePreferredNamespaceAlias;

    public Forge(final String separator, final String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("A non-blank name is required.");
        }

        this.name = name.toLowerCase();
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
    public String toString() {
        return name;
    }

    public String getName() {
        final String formatString = usePreferredNamespaceAlias != null && usePreferredNamespaceAlias ? "@%s" : "%s";
        return String.format(formatString, name);
    }

    public String getSeparator() {
        return separator;
    }

}
