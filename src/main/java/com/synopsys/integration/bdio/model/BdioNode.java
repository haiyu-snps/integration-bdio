/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BdioNode {
    @SerializedName("@id")
    public BdioId id;

    @SerializedName("@type")
    public String type;

    @SerializedName("externalIdentifier")
    public BdioExternalIdentifier bdioExternalIdentifier;

    @SerializedName("relationship")
    public List<BdioRelationship> relationships = new ArrayList<>();

}
