package com.synopsys.integration.bdio.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BdioIdEscaper {
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
        return s.replaceAll("[^A-Za-z0-9]", "_");
    }

}
