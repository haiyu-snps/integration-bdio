package com.synopsys.integration.bdio.utility;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;

public class JsonTestUtils {
    public void verifyJsonArraysEqual(final String expectedJson, final String actualJson) throws JSONException {
        verifyJsonArraysEqual(expectedJson, actualJson, true);
    }

    public void verifyJsonArraysEqual(final String expectedJson, final String actualJson, final boolean strict) throws JSONException {
        final JSONArray expected = (JSONArray) JSONParser.parseJSON(expectedJson);
        final JSONArray actual = (JSONArray) JSONParser.parseJSON(actualJson);
        assertEquals(expected.length(), actual.length());
        JSONAssert.assertEquals(expected, actual, strict);
    }

    public String getExpectedJson(final String filename) throws URISyntaxException, IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        final File file = new File(url.toURI().getPath());
        final String expectedJson = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        return expectedJson;
    }

}
