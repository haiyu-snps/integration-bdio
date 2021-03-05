package com.synopsys.integration.bdio.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyTestUtil {
    public static ExternalIdFactory factory = new ExternalIdFactory();

    public static Dependency newMavenDependency(final String name, final String version, final String org) {
        return new Dependency(name, version, factory.createMavenExternalId(org, name, version));
    }

    public static Dependency newMavenDependency(String gav) {
        String[] pieces = gav.split(":");
        return newMavenDependency(pieces[0], pieces[1], pieces[2]);
    }

    public static void addMavenRelationship(MutableDependencyGraph graph, String parentGav, String childGav) {
        graph.addParentWithChild(newMavenDependency(parentGav), newMavenDependency(childGav));
    }

    public static Set<Dependency> asSet(final Dependency... dependencies) {
        final Set<Dependency> set = new HashSet<>(Arrays.asList(dependencies));
        return set;
    }

    public static List<Dependency> asList(final Dependency... dependencies) {
        final List<Dependency> list = new ArrayList<>(Arrays.asList(dependencies));
        return list;
    }

}
