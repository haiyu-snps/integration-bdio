/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model;

public class SpdxCreator {
    private String type;
    private String identifier;

    public SpdxCreator(final String type, final String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public String getData() {
        return String.format("%s: %s", type, identifier);
    }

    public static SpdxCreator createToolSpdxCreator(final String name, final String version) {
        final String identifier = String.format("%s-%s", name, version);
        final SpdxCreator spdxCreator = new SpdxCreator("Tool", identifier);
        return spdxCreator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
