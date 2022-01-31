/*
 * integration-bdio
 *
 * Copyright (c) 2022 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.bdio.model;

import com.google.gson.annotations.SerializedName;

public class BdioBillOfMaterials extends BdioNode {
    @SerializedName("specVersion")
    public String bdioSpecificationVersion;

    @SerializedName("spdx:name")
    public String spdxName;

    @SerializedName("creationInfo")
    public BdioCreationInfo creationInfo;

    public BdioBillOfMaterials() {
        type = "BillOfMaterials";
    }

}
