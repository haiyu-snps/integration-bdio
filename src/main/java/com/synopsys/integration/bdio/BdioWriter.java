/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.synopsys.integration.bdio.model.BdioNode;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;

public class BdioWriter implements Closeable {
    private final Gson gson;
    private final JsonWriter jsonWriter;

    public BdioWriter(final Gson gson, final Writer writer) throws IOException {
        this.gson = gson;
        jsonWriter = new JsonWriter(writer);
        jsonWriter.setIndent("  ");
        jsonWriter.beginArray();
    }

    public BdioWriter(final Gson gson, final OutputStream outputStream) throws IOException {
        this.gson = gson;
        jsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        jsonWriter.setIndent("  ");
        jsonWriter.beginArray();
    }

    public void writeSimpleBdioDocument(final SimpleBdioDocument simpleBdioDocument) {
        final List<BdioNode> bdioNodes = new ArrayList<>();
        bdioNodes.add(simpleBdioDocument.getBillOfMaterials());
        bdioNodes.add(simpleBdioDocument.getProject());
        if (simpleBdioDocument.getComponents() != null && !simpleBdioDocument.getComponents().isEmpty()) {
            bdioNodes.addAll(simpleBdioDocument.getComponents());
        }

        writeBdioNodes(bdioNodes);
    }

    public void writeBdioNodes(final List<? extends BdioNode> bdioNodes) {
        for (final BdioNode bdioNode : bdioNodes) {
            writeBdioNode(bdioNode);
        }
    }

    public void writeBdioNode(final BdioNode bdioNode) {
        gson.toJson(bdioNode, bdioNode.getClass(), jsonWriter);
    }

    @Override
    public void close() throws IOException {
        jsonWriter.endArray();
        jsonWriter.close();
    }

}
