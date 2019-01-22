package com.synopsys.integration.bdio;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.DependencyGraphTransformer;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.bdio.utility.JsonTestUtils;

public class SimpleBdioFactoryTest {
    @Test
    public void testConstructor() {
        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        assertNotNull(simpleBdioFactory);
        assertNotNull(simpleBdioFactory.getBdioPropertyHelper());
        assertNotNull(simpleBdioFactory.getBdioNodeFactory());
        assertNotNull(simpleBdioFactory.getDependencyGraphTransformer());
        assertNotNull(simpleBdioFactory.getExternalIdFactory());

        assertFalse(bdioPropertyHelper == simpleBdioFactory.getBdioPropertyHelper());
        assertFalse(bdioNodeFactory == simpleBdioFactory.getBdioNodeFactory());
        assertFalse(dependencyGraphTransformer == simpleBdioFactory.getDependencyGraphTransformer());
        assertFalse(externalIdFactory == simpleBdioFactory.getExternalIdFactory());
    }

    @Test
    public void testDependencyInjectionConstructor() {
        final BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        final BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        final DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory(bdioPropertyHelper, bdioNodeFactory, dependencyGraphTransformer, externalIdFactory, gson);

        assertNotNull(simpleBdioFactory);
        assertNotNull(simpleBdioFactory.getBdioPropertyHelper());
        assertNotNull(simpleBdioFactory.getBdioNodeFactory());
        assertNotNull(simpleBdioFactory.getDependencyGraphTransformer());

        assertTrue(bdioPropertyHelper == simpleBdioFactory.getBdioPropertyHelper());
        assertTrue(bdioNodeFactory == simpleBdioFactory.getBdioNodeFactory());
        assertTrue(dependencyGraphTransformer == simpleBdioFactory.getDependencyGraphTransformer());
        assertTrue(externalIdFactory == simpleBdioFactory.getExternalIdFactory());
    }

    @Test
    public void testConstructingBdioWriters() throws IOException {
        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        final Writer writer = new StringWriter();
        final OutputStream outputStream = new ByteArrayOutputStream();

        final BdioWriter writerBdioWriter = simpleBdioFactory.createBdioWriter(writer);
        assertNotNull(writerBdioWriter);

        final BdioWriter outputStreamBdioWriter = simpleBdioFactory.createBdioWriter(outputStream);
        assertNotNull(outputStreamBdioWriter);
    }

    @Test
    public void testTryFinally() throws IOException {
        final SimpleBdioFactory simpleBdioFactory = Mockito.spy(new SimpleBdioFactory());
        Mockito.doThrow(RuntimeException.class).when(simpleBdioFactory).createBdioWriter(Mockito.any(OutputStream.class));
        final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        final File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertThrows(RuntimeException.class, () -> simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument));
    }

    @Test
    public void testTryFinally2() throws IOException {
        final SimpleBdioFactory simpleBdioFactory = Mockito.spy(new SimpleBdioFactory());
        Mockito.doThrow(RuntimeException.class).when(simpleBdioFactory).writeSimpleBdioDocument(Mockito.any(BdioWriter.class), Mockito.any(SimpleBdioDocument.class));
        final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        final File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertThrows(RuntimeException.class, () -> simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument));
    }

    @Test
    public void testWritingBdioToFile() throws IOException, URISyntaxException, JSONException {
        final SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        final File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertEquals(0, bdioFile.length());

        // overriding default UUID so the expected value matches the actual value
        simpleBdioDocument.billOfMaterials.id = "uuid:static-uuid-for-testing";
        simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument);

        assertNotEquals(0, bdioFile.length());

        final JsonTestUtils jsonTestUtils = new JsonTestUtils();
        final String expectedJson = jsonTestUtils.getExpectedJson("simple-bdio-factory-integration-test-output.jsonld");
        final String actualJson = IOUtils.toString(new FileInputStream(bdioFile), StandardCharsets.UTF_8);

        jsonTestUtils.verifyJsonArraysEqual(expectedJson, actualJson, false);
    }

    private SimpleBdioDocument createSimpleBdioDocument(final SimpleBdioFactory simpleBdioFactory) {
        final MutableDependencyGraph mutableDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        final Dependency bdioTestDependency = simpleBdioFactory.createDependency("bdio-test", "1.1.2", simpleBdioFactory.getExternalIdFactory().createMavenExternalId("com.blackducksoftware.integration", "bdio-test", "1.1.2"));
        final Dependency bdioReaderDependency = simpleBdioFactory.createDependency("bdio-reader", "1.2.0", simpleBdioFactory.getExternalIdFactory().createMavenExternalId("com.blackducksoftware.integration", "bdio-reader", "1.2.0"));
        final Dependency commonsLangDependency = simpleBdioFactory.createDependency("commons-lang3", "3.6", simpleBdioFactory.getExternalIdFactory().createMavenExternalId("org.apache.commons", "commons-lang3", "3.6"));

        mutableDependencyGraph.addChildrenToRoot(bdioTestDependency);
        mutableDependencyGraph.addChildrenToRoot(bdioReaderDependency);
        mutableDependencyGraph.addChildWithParent(commonsLangDependency, bdioReaderDependency);

        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("test code location", "integration-bdio", "0.0.1",
                simpleBdioFactory.createMavenExternalId("com.blackducksoftware.integration", "integration-bdio", "0.0.1"), mutableDependencyGraph);
        return simpleBdioDocument;
    }

}
