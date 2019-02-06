package com.synopsys.integration.bdio.model;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.util.IntegrationEscapeUtil;

public class BdioId {
    public static final String BDIO_ID_SEPARATOR = "/";

    private static final IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil();

    private final String id;

    /**
     * Avoid using this constructor - bdio id's have uniqueness and formatting constraints that the other constructors handle better.
     */
    public BdioId(String id) {
        this.id = id;
    }

    public BdioId(String... idPieces) {
        this(Arrays.asList(idPieces));
    }

    public BdioId(List<String> idPieces) {
        id = "http:" + StringUtils.join(BdioId.integrationEscapeUtil.escapePiecesForUri(idPieces), BdioId.BDIO_ID_SEPARATOR);
    }

    @Override
    public String toString() {
        return id;
    }

}
