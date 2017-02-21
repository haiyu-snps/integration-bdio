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
package com.blackducksoftware.integration.hub.bdio.simple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class BdioWriter extends Writer {
    private final Gson gson;

    private final JsonWriter jsonWriter;

    public BdioWriter(final Gson gson, final Writer writer) throws IOException {
        this.gson = gson;
        this.jsonWriter = new JsonWriter(writer);
        jsonWriter.setIndent("  ");
        jsonWriter.beginArray();
    }

    public BdioWriter(final Gson gson, final OutputStream outputStream) throws IOException {
        this.gson = gson;
        this.jsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        jsonWriter.setIndent("  ");
        jsonWriter.beginArray();
    }

    public void writeBdioNodes(final List<BdioNode> bdioNodes) {
        for (final BdioNode bdioNode : bdioNodes) {
            gson.toJson(bdioNode, bdioNode.getClass(), jsonWriter);
        }
    }

    public void writeBdioNode(final BdioNode bdioNode) {
        gson.toJson(bdioNode, bdioNode.getClass(), jsonWriter);
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
    }

    @Override
    public void flush() throws IOException {
        jsonWriter.flush();
    }

    @Override
    public void close() throws IOException {
        jsonWriter.endArray();
        jsonWriter.close();
    }

}
