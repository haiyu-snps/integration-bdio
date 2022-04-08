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
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.bdio.utility.JsonTestUtils;

class SimpleBdioFactoryTest {
    @Test
    void testConstructor() {
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
    void testDependencyInjectionConstructor() {
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
    void testConstructingBdioWriters() throws IOException {
        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        Writer writer = new StringWriter();
        OutputStream outputStream = new ByteArrayOutputStream();

        BdioWriter writerBdioWriter = simpleBdioFactory.createBdioWriter(writer);
        assertNotNull(writerBdioWriter);

        BdioWriter outputStreamBdioWriter = simpleBdioFactory.createBdioWriter(outputStream);
        assertNotNull(outputStreamBdioWriter);
    }

    @Test
    void testTryFinally() throws IOException {
        SimpleBdioFactory simpleBdioFactory = Mockito.spy(new SimpleBdioFactory());
        Mockito.doThrow(RuntimeException.class).when(simpleBdioFactory).createBdioWriter(Mockito.any(OutputStream.class));
        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertThrows(RuntimeException.class, () -> simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument));
    }

    @Test
    void testTryFinally2() throws IOException {
        SimpleBdioFactory simpleBdioFactory = Mockito.spy(new SimpleBdioFactory());
        Mockito.doThrow(RuntimeException.class).when(simpleBdioFactory).writeSimpleBdioDocument(Mockito.any(BdioWriter.class), Mockito.any(SimpleBdioDocument.class));
        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertThrows(RuntimeException.class, () -> simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument));
    }

    @Test
    void testWritingBdioToFile() throws IOException, URISyntaxException, JSONException {
        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();

        SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(simpleBdioFactory);

        File bdioFile = File.createTempFile("bdio", "jsonld");
        bdioFile.deleteOnExit();

        assertEquals(0L, bdioFile.length());

        // overriding default UUID so the expected value matches the actual value
        simpleBdioDocument.getBillOfMaterials().id = BdioId.createFromUUID("static-uuid-for-testing");
        simpleBdioFactory.writeSimpleBdioDocumentToFile(bdioFile, simpleBdioDocument);

        assertNotEquals(0L, bdioFile.length());

        JsonTestUtils jsonTestUtils = new JsonTestUtils();
        String expectedJson = jsonTestUtils.getExpectedJson("simple-bdio-factory-integration-test-output.jsonld");
        String actualJson = IOUtils.toString(new FileInputStream(bdioFile), StandardCharsets.UTF_8);

        jsonTestUtils.verifyJsonArraysEqual(expectedJson, actualJson, false);
    }

    @Test
    void testCreatingProjectWithOnlyNameAndVersion() {
        SimpleBdioFactory simpleBdioFactory = new SimpleBdioFactory();
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createEmptyBdioDocument("code location name", "project name", "project version name");

        assertEquals("code location name", simpleBdioDocument.getBillOfMaterials().spdxName);
        assertEquals("project name", simpleBdioDocument.getProject().name);
        assertEquals("project version name", simpleBdioDocument.getProject().version);
        assertEquals(new BdioId("http:project+name/project+version+name"), simpleBdioDocument.getProject().id);
        assertTrue(simpleBdioDocument.getProject().relationships.isEmpty());
        assertNull(simpleBdioDocument.getProject().bdioExternalIdentifier);
    }

    private SimpleBdioDocument createSimpleBdioDocument(SimpleBdioFactory simpleBdioFactory) {
        Dependency bdioTestDependency = Dependency.FACTORY.createMavenDependency("com.blackducksoftware.integration", "bdio-test", "1.1.2");
        Dependency bdioReaderDependency = Dependency.FACTORY.createMavenDependency("com.blackducksoftware.integration", "bdio-reader", "1.2.0");
        Dependency commonsLangDependency = Dependency.FACTORY.createMavenDependency("org.apache.commons", "commons-lang3", "3.6");

        //        ExternalId projectExternalId = externalIdFactory.createMavenExternalId();
        ProjectDependencyGraph projectDependencyGraph = new ProjectDependencyGraph(Dependency.FACTORY.createMavenDependency(
            "com.blackducksoftware.integration",
            "integration-bdio",
            "0.0.1"
        ));

        projectDependencyGraph.addChildrenToRoot(bdioTestDependency);
        projectDependencyGraph.addChildrenToRoot(bdioReaderDependency);
        projectDependencyGraph.addChildWithParent(commonsLangDependency, bdioReaderDependency);

        return simpleBdioFactory.createPopulatedBdioDocument("test code location", projectDependencyGraph);
    }

}
