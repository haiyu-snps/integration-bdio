/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.synopsys.integration.bdio.model.BdioBillOfMaterials;
import com.synopsys.integration.bdio.model.BdioComponent;
import com.synopsys.integration.bdio.model.BdioProject;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;

public class BdioReader implements Closeable {
    private final Gson gson;
    private final JsonReader jsonReader;

    public BdioReader(final Gson gson, final Reader reader) throws IOException {
        this.gson = gson;
        jsonReader = new JsonReader(reader);
        jsonReader.beginArray();
    }

    public BdioReader(final Gson gson, final InputStream inputStream) throws IOException {
        this.gson = gson;
        jsonReader = new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        jsonReader.beginArray();
    }

    public SimpleBdioDocument readSimpleBdioDocument() throws IOException {
        final SimpleBdioDocument document = new SimpleBdioDocument();
        document.setBillOfMaterials(readBillOfMaterials());
        document.setProject(readProject());
        while (jsonReader.hasNext()) {
            document.getComponents().add(readComponent());
        }
        return document;
    }

    public BdioBillOfMaterials readBillOfMaterials() {
        return gson.fromJson(jsonReader, BdioBillOfMaterials.class);
    }

    public BdioProject readProject() {
        return gson.fromJson(jsonReader, BdioProject.class);
    }

    public BdioComponent readComponent() {
        return gson.fromJson(jsonReader, BdioComponent.class);
    }

    @Override
    public void close() throws IOException {
        jsonReader.endArray();
        jsonReader.close();
    }

}
