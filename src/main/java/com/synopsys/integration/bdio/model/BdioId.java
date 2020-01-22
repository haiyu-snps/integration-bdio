/**
 * integration-bdio
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.bdio.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(BdioIdAdapter.class)
public class BdioId {
    public static final String BDIO_ID_SEPARATOR = "/";

    private static final BdioIdEscaper bdioIdEscaper = new BdioIdEscaper();

    private final String id;

    public static BdioId createFromPieces(List<String> pieces) {
        return new BdioId("http:" + StringUtils.join(BdioId.bdioIdEscaper.escapePiecesForUri(pieces), BdioId.BDIO_ID_SEPARATOR));
    }

    public static BdioId createFromPieces(String... pieces) {
        return BdioId.createFromPieces(Arrays.asList(pieces));
    }

    public static BdioId createFromUUID(String uuid) {
        return new BdioId(String.format("uuid:%s", uuid));
    }

    /**
     * Avoid using this constructor - bdio id's have uniqueness and formatting constraints that the static builders handle better.
     * This is mainly for use internally within the library.
     */
    public BdioId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BdioId bdioId = (BdioId) o;
        return Objects.equals(id, bdioId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

}
