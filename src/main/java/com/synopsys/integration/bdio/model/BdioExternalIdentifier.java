/*
 * integration-bdio
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class BdioExternalIdentifier {
    @SerializedName("externalSystemTypeId")
    public String forge;

    @SerializedName("externalId")
    public String externalId;

    @SerializedName("externalIdMetaData")
    // this horrible name exists because 'externalId' is reserved by the bdio specification
    public ExternalId externalIdMetaData;
}
