/**
 * integration-bdio
 *
 * Copyright (c) 2020 Synopsys, Inc.
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

import com.synopsys.integration.util.IntegrationEscapeUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BdioIdEscaper {
    private IntegrationEscapeUtil integrationEscapeUtil;

    public BdioIdEscaper() {
        this.integrationEscapeUtil = new IntegrationEscapeUtil();
    }

    public BdioIdEscaper(IntegrationEscapeUtil integrationEscapeUtil) {
        this.integrationEscapeUtil = new IntegrationEscapeUtil();
    }

    public List<String> escapePiecesForUri(final List<String> pieces) {
        final List<String> escapedPieces = new ArrayList<>(pieces.size());
        for (final String piece : pieces) {
            final String escaped = escapeForUri(piece);
            escapedPieces.add(escaped);
        }

        return escapedPieces;
    }

    public String escapeForUri(final String s) {
        if (s == null) {
            return null;
        }

        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return poorManEscaping(s);
        }
    }

    private String poorManEscaping(String s) {
        return integrationEscapeUtil.replaceWithUnderscore(s);
    }

}
