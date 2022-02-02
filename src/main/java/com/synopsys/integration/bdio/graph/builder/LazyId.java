/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.graph.builder;

import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.Stringable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.synopsys.integration.bdio.graph.builder.LazyIdSource.*;

/**
 * An id for a Dependency that has fewer requirements than an ExternalId. While
 * building a graph you may have a name and a version, or maybe just a name, or
 * perhaps just a plain string to identify the Dependency.
 */
public class LazyId extends Stringable {
    public static LazyId fromName(String name) {
        return new LazyId(NAME, Arrays.asList(name));
    }

    public static LazyId fromNameAndVersion(String name, String version) {
        return new LazyId(NAME_VERSION, Arrays.asList(name, version));
    }

    public static LazyId fromNameVersion(NameVersion nameVersion) {
        return new LazyId(NAME_VERSION, Arrays.asList(nameVersion.getName(), nameVersion.getVersion()));
    }

    public static LazyId fromString(String s) {
        return new LazyId(STRING, Arrays.asList(s));
    }

    private final List<String> pieces = new LinkedList<>();

    public LazyId(LazyIdSource source, List<String> pieces) {
        this.pieces.add(source.name());
        this.pieces.addAll(pieces);
    }

}
