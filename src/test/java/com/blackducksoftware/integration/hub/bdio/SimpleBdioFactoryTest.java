package com.blackducksoftware.integration.hub.bdio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer;

public class SimpleBdioFactoryTest {
    @Test
    public void testConstructor() {
        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        assertNotNull(simpleBdioFactory);
        assertNotNull(simpleBdioFactory.getBdioPropertyHelper());
        assertNotNull(simpleBdioFactory.getBdioNodeFactory());
        assertNotNull(simpleBdioFactory.getDependencyGraphTransformer());

        assertFalse(bdioPropertyHelper == simpleBdioFactory.getBdioPropertyHelper());
        assertFalse(bdioNodeFactory == simpleBdioFactory.getBdioNodeFactory());
        assertFalse(dependencyGraphTransformer == simpleBdioFactory.getDependencyGraphTransformer());
    }

    @Test
    public void testDependencyInjectionConstructor() {
        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory(bdioPropertyHelper, bdioNodeFactory, dependencyGraphTransformer);

        assertNotNull(simpleBdioFactory);
        assertNotNull(simpleBdioFactory.getBdioPropertyHelper());
        assertNotNull(simpleBdioFactory.getBdioNodeFactory());
        assertNotNull(simpleBdioFactory.getDependencyGraphTransformer());

        assertTrue(bdioPropertyHelper == simpleBdioFactory.getBdioPropertyHelper());
        assertTrue(bdioNodeFactory == simpleBdioFactory.getBdioNodeFactory());
        assertTrue(dependencyGraphTransformer == simpleBdioFactory.getDependencyGraphTransformer());
    }

}
