package com.synopsys.integration.bdio;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.BdioBillOfMaterials;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioCreationInfo;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.SpdxCreator;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.bdio.utility.JsonTestUtils;

public class BdioNodeFactoryTest {
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    public void testWriterOutput() throws FileNotFoundException, IOException, URISyntaxException, JSONException {
        String expectedJson = jsonTestUtils.getExpectedJson("sample.jsonld");

        // we simply write the final structure out through a StringWriter so we can compare what is generated to a stock
        // file
        Writer writer = new StringWriter();
        try (BdioWriter bdioWriter = new BdioWriter(new Gson(), writer)) {
            bdioWriter.writeSimpleBdioDocument(getSimpleBdioDocument());
        }

        String actualJson = writer.toString();
        jsonTestUtils.verifyJsonArraysEqual(expectedJson, actualJson);
    }

    @Test
    public void testOutputStreamOutput() throws FileNotFoundException, IOException, URISyntaxException, JSONException {
        String expectedJson = jsonTestUtils.getExpectedJson("sample.jsonld");

        // we simply write the final structure out through a ByteArrayOutputStream so we can compare what is generated
        // to a stock file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BdioWriter bdioWriter = new BdioWriter(new Gson(), outputStream)) {
            bdioWriter.writeSimpleBdioDocument(getSimpleBdioDocument());
        }

        String actualJson = outputStream.toString(StandardCharsets.UTF_8.name());
        jsonTestUtils.verifyJsonArraysEqual(expectedJson, actualJson);
    }

    @Test
    public void testCodeLocationOverride() {
        BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials("name", "version");
        assertEquals("name/version Black Duck I/O Export", bdioBillOfMaterials.spdxName);

        bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials("override", "name", "version");
        assertEquals("override", bdioBillOfMaterials.spdxName);
    }

    @Test
    public void testCreateMinimumProject() {
        BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);

        BdioProject project = bdioNodeFactory.createProject("projectName", "projectVersionName");
        assertEquals("Project", project.type);
        assertEquals("projectName", project.name);
        assertEquals("projectVersionName", project.version);
        assertEquals(new BdioId("http:projectName/projectVersionName"), project.id);
        assertEquals(0, project.relationships.size());
        assertNull(project.bdioExternalIdentifier);
    }

    @Test
    public void testVersionFileFound() throws Exception {
        BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);
        BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials("name", "version");
        assertEquals("name/version Black Duck I/O Export", bdioBillOfMaterials.spdxName);
        for (String spdxCreator : bdioBillOfMaterials.creationInfo.getCreator()) {
            assertFalse(spdxCreator.contains(BdioNodeFactory.UNKNOWN_LIBRARY_VERSION));
        }
    }

    private SimpleBdioDocument getSimpleBdioDocument() {
        SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument();

        BdioPropertyHelper bdioPropertyHelper = new BdioPropertyHelper();
        BdioNodeFactory bdioNodeFactory = new BdioNodeFactory(bdioPropertyHelper);

        String projectGroup = "com.blackducksoftware.gradle.test";
        String projectName = "gradleTestProject";
        String projectVersion = "99.5-SNAPSHOT";
        ExternalId mavenExternalId = externalIdFactory.createMavenExternalId(projectGroup, projectName, projectVersion);
        BdioId projectBdioId = mavenExternalId.createBdioId();

        BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials(projectName, projectVersion);
        // we are overriding the default value of a new creation info just to pass the json comparison
        bdioBillOfMaterials.creationInfo = new BdioCreationInfo();
        bdioBillOfMaterials.creationInfo.addSpdxCreator(SpdxCreator.createToolSpdxCreator("integration-bdio-test", "0.0.1-SNAPSHOT"));
        // we are overriding the default value of a new uuid just to pass the json comparison
        bdioBillOfMaterials.id = BdioId.createFromUUID("45772d33-5353-44f1-8681-3d8a15540646");

        BdioProject bdioProject = bdioNodeFactory.createProject(projectName, projectVersion, projectBdioId, mavenExternalId);

        ExternalId cxfBundleExternalId = externalIdFactory.createMavenExternalId("org.apache.cxf", "cxf-bundle", "2.7.7");
        BdioComponent cxfBundle = bdioNodeFactory.createComponent("cxf-bundle", "2.7.7", cxfBundleExternalId);

        ExternalId velocityExternalId = externalIdFactory.createMavenExternalId("org.apache.velocity", "velocity", "1.7");
        BdioComponent velocity = bdioNodeFactory.createComponent("velocity", "1.7", velocityExternalId);

        ExternalId commonsCollectionsExternalId = externalIdFactory.createMavenExternalId("commons-collections", "commons-collections", "3.2.1");
        BdioComponent commonsCollections = bdioNodeFactory.createComponent("commons-collections", "3.2.1", commonsCollectionsExternalId);

        ExternalId commonsLangExternalId = externalIdFactory.createMavenExternalId("commons-lang", "commons-lang", "2.6");
        BdioComponent commonsLang = bdioNodeFactory.createComponent("commons-lang", "2.6", commonsLangExternalId);

        // we will now relate the constructed bdio nodes

        // first, add the cxfBundle component as a child of the project - this project has a single direct dependency
        bdioPropertyHelper.addRelationship(bdioProject, cxfBundle);

        // now, the cxfBundle component itself has two dependencies, which will appear in the final BOM as they are
        // transitive dependencies of the project
        bdioPropertyHelper.addRelationships(cxfBundle, Arrays.asList(velocity, commonsLang));

        // and the velocity component also has two dependencies - it will only add one additional entry to our final BOM
        // as the commonsLang component was already included from the cxfBundle component above
        bdioPropertyHelper.addRelationships(velocity, Arrays.asList(commonsCollections, commonsLang));

        List<BdioComponent> bdioComponents = new ArrayList<>();
        bdioComponents.add(cxfBundle);
        bdioComponents.add(velocity);
        bdioComponents.add(commonsCollections);
        bdioComponents.add(commonsLang);

        simpleBdioDocument.billOfMaterials = bdioBillOfMaterials;
        simpleBdioDocument.project = bdioProject;
        simpleBdioDocument.components = bdioComponents;

        return simpleBdioDocument;
    }

}
