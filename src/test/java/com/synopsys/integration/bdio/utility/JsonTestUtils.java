/**
 * Integration Bdio
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
