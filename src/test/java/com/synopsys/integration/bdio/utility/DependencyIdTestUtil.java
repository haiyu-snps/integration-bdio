package com.synopsys.integration.bdio.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.bdio.model.dependencyid.DependencyId;

public class DependencyIdTestUtil {
    public static Set<DependencyId> asSet(final DependencyId... dependencies) {
        final Set<DependencyId> set = new HashSet<>(Arrays.asList(dependencies));
        return set;
    }

    public static List<DependencyId> asList(final DependencyId... dependencies) {
        final List<DependencyId> list = new ArrayList<>(Arrays.asList(dependencies));
        return list;
    }

}
