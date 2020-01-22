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
        return integrationEscapeUtil.escapeForUri(s);
    }

}
