/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class BdioIdAdapter extends TypeAdapter<BdioId> {
    @Override
    public void write(JsonWriter writer, BdioId value) throws IOException {
        writer.value(value.toString());
    }

    @Override
    public BdioId read(JsonReader in) throws IOException {
        return new BdioId(in.nextString());
    }

}
