package com.synopsys.integration.bdio.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyTestUtil {
    public static ExternalIdFactory factory = new ExternalIdFactory();

    public static Dependency newMavenDependency(final String name, final String version, final String org) {
        return new Dependency(name, version, factory.createMavenExternalId(org, name, version));
    }

    public static Set<Dependency> asSet(final Dependency... dependencies) {
        final Set<Dependency> set = new HashSet<>();
        for (final Dependency dependency : dependencies) {
            set.add(dependency);
        }
        return set;
    }

    public static List<Dependency> asList(final Dependency... dependencies) {
        final List<Dependency> list = new ArrayList<>();
        for (final Dependency dependency : dependencies) {
            list.add(dependency);
        }
        return list;
    }

}
