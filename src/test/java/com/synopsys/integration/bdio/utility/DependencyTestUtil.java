package com.synopsys.integration.bdio.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependencyTestUtil {
    public static ExternalIdFactory factory = new ExternalIdFactory();

    public static Dependency newMavenDependency(String gav) {
        String[] pieces = gav.split(":");
        return new Dependency(pieces[1], pieces[2], factory.createMavenExternalId(pieces[0], pieces[1], pieces[2]));
    }

    public static ProjectDependency newProjectDependency(Forge forge, String name, String version) {
        return new ProjectDependency(name, version, factory.createNameVersionExternalId(forge, name, version));
    }

    public static Set<Dependency> asSet(Dependency... dependencies) {
        return new HashSet<>(Arrays.asList(dependencies));
    }

    public static List<Dependency> asList(Dependency... dependencies) {
        return new ArrayList<>(Arrays.asList(dependencies));
    }

}
