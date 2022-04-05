package com.synopsys.integration.bdio.graph;

import java.util.Set;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;

public class ProjectDependencyGraph extends DependencyGraph {
    private final ProjectDependency rootDependency;

    public ProjectDependencyGraph(Dependency rootDependency) {
        this.rootDependency = new ProjectDependency(rootDependency);
    }

    public ProjectDependencyGraph(ProjectDependency rootDependency) {
        this.rootDependency = rootDependency;
    }

    public ProjectDependency getRootDependency() {
        return rootDependency;
    }

    public void copyGraphToRoot(BasicDependencyGraph sourceGraph) {
        DependencyGraphCombiner.copyRootDependencies(this, sourceGraph);
    }

    public void copyGraphToRoot(ProjectDependencyGraph sourceGraph) {
        ProjectDependency sourceRootDependency = sourceGraph.getRootDependency();
        addChildToRoot(sourceRootDependency);
        DependencyGraphCombiner.copyRootDependenciesToParent(this, sourceRootDependency, sourceGraph);
    }

    @Override
    public void addChildToRoot(Dependency child) {
        addParentWithChild(rootDependency, child);
    }

    @Override
    public Set<Dependency> getRootDependencies() {
        return getChildrenForParent(getRootDependency());
    }
}
