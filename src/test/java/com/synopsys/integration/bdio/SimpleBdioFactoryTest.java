package com.synopsys.integration.bdio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.bdio.utility.JsonTestUtils;

public class SimpleBdioFactoryTest {
    @Test
    public void testConstructor() {
        BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        assertNotNull(simpleBdioFactory);
        assertNotNull(simpleBdioFactory.getBdioPropertyHelper());
        assertNotNull(simpleBdioFactory.getBdioNodeFactory());
        assertNotNull(simpleBdioFactory.getDependencyGraphTransformer());
        assertNotNull(simpleBdioFactory.getExternalIdFactory());

        assertNotSame(bdioPropertyHelper, simpleBdioFactory.getBdioPropertyHelper());
        assertNotSame(bdioNodeFactory, simpleBdioFactory.getBdioNodeFactory());
        assertNotSame(dependencyGraphTransformer, simpleBdioFactory.getDependencyGraphTransformer());
        assertNotSame(externalIdFactory, simpleBdioFactory.getExternalIdFactory());
    }

    @Test
    public void testDependencyInjectionConstructor() {
        BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        DependencyGraphTransformer dependencyGraphTransformer = new DependencyGraphTransformer(bdioPropertyHelper, bdioNodeFactory);
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory(bdioPropertyHelper, bdioNodeFactory, dependencyGraphTransformer, externalIdFactory, dependencyFactory, gson);

        assertNotNull(simpleBdioFactory);

        assertSame(bdioPropertyHelper, simpleBdioFactory.getBdioPropertyHelper());
        assertSame(bdioNodeFactory, simpleBdioFactory.getBdioNodeFactory());
        assertSame(dependencyGraphTransformer, simpleBdioFactory.getDependencyGraphTransformer());
        assertSame(externalIdFactory, simpleBdioFactory.getExternalIdFactory());
        assertSame(dependencyFactory, simpleBdioFactory.getDependencyFactory());
    }

    @Test
    public void testConstructingBdioWriters() throws IOException {
        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        Writer writer = new StringWriter();
        OutputStream outputStream = new ByteArrayOutputStream();

        BdioWriter writerBdioWriter = simpleBdioFactory.createBdioWriter(writer);
        assertNotNull(writerBdioWriter);

        BdioWriter outputStreamBdioWriter = simpleBdioFactory.createBdioWriter(outputStream);
        assertNotNull(outputStreamBdioWriter);
    }

    @Test
    public void testTryFinally() throws IOException {
        SimpleBdioFactory simpleBdioFactory = Mockito.spy(new SimpleBdioFactory());
        Mockito.doThrow(RuntimeException.class).when(simpleBdioFactory).createBdioWriter(Mockito.any(OutputStream.class));
        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertThrows(RuntimeException.class, () -> simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument));
    }

    @Test
    public void testTryFinally2() throws IOException {
        SimpleBdioFactory simpleBdioFactory = Mockito.spy(new SimpleBdioFactory());
        Mockito.doThrow(RuntimeException.class).when(simpleBdioFactory).writeSimpleBdioDocument(Mockito.any(BdioWriter.class), Mockito.any(SimpleBdioDocument.class));
        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertThrows(RuntimeException.class, () -> simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument));
    }

    @Test
    public void testWritingBdioToFile() throws IOException, URISyntaxException, JSONException {
        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertEquals(0, bdioFile.length());

        // overriding default UUID so the expected value matches the actual value
        simpleBdioDocument.getBillOfMaterials().id = BdioId.createFromUUID("static-uuid-for-testing");
        simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument);

        assertNotEquals(0, bdioFile.length());

        JsonTestUtils jsonTestUtils = new JsonTestUtils();
        String expectedJson = jsonTestUtils.getExpectedJson("simple-bdio-factory-integration-test-output.jsonld");
        String actualJson = IOUtils.toString(new FileInputStream(bdioFile), StandardCharsets.UTF_8);

        jsonTestUtils.verifyJsonArraysEqual(expectedJson, actualJson, false);
    }

    @Test
    public void testCreatingProjectWithOnlyNameAndVersion() {
        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("code location name", "project name", "project version name");

        assertEquals("code location name", simpleBdioDocument.getBillOfMaterials().spdxName);
        assertEquals("project name", simpleBdioDocument.getProject().name);
        assertEquals("project version name", simpleBdioDocument.getProject().version);
        assertEquals(new BdioId("http:project+name/project+version+name"), simpleBdioDocument.getProject().id);
        assertTrue(simpleBdioDocument.getProject().relationships.isEmpty());
        assertNull(simpleBdioDocument.getProject().bdioExternalIdentifier);
    }

    private SimpleBdioDocument createSimpleBdioDocument(SimpleBdioFactory simpleBdioFactory) {
        MutableDependencyGraph mutableDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();
        ExternalIdFactory externalIdFactory = simpleBdioFactory.getExternalIdFactory();
        DependencyFactory dependencyFactory = simpleBdioFactory.getDependencyFactory();

        Dependency bdioTestDependency = dependencyFactory.createMavenDependency("com.blackducksoftware.integration", "bdio-test", "1.1.2");
        Dependency bdioReaderDependency = dependencyFactory.createMavenDependency("com.blackducksoftware.integration", "bdio-reader", "1.2.0");
        Dependency commonsLangDependency = dependencyFactory.createMavenDependency("org.apache.commons", "commons-lang3", "3.6");

        mutableDependencyGraph.addChildrenToRoot(bdioTestDependency);
        mutableDependencyGraph.addChildrenToRoot(bdioReaderDependency);
        mutableDependencyGraph.addChildWithParent(commonsLangDependency, bdioReaderDependency);

        ExternalId projectExternalId = externalIdFactory.createMavenExternalId("com.blackducksoftware.integration", "integration-bdio", "0.0.1");
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument("test code location", "integration-bdio", "0.0.1", projectExternalId, mutableDependencyGraph);

        return simpleBdioDocument;
    }

}
