package com.blackducksoftware.integration.hub.bdio.simple.model.externalid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;

public abstract class ExternalId {
    public static final String DATA_ID_SEPARATOR = "/";

    public final Forge forge;

    public ExternalId(final Forge forge) {
        this.forge = forge;
    }

    public abstract String[] getExternalIdPieces();

    public String createDataId() {
        final List<String> dataIdPieces = new ArrayList<>();
        dataIdPieces.add(forge.toString());
        dataIdPieces.addAll(Arrays.asList(getExternalIdPieces()));

        return "data:" + StringUtils.join(dataIdPieces, DATA_ID_SEPARATOR);
    }

    public String createExternalId() {
        return StringUtils.join(getExternalIdPieces(), forge.separator);
    }

}
