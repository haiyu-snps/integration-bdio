package com.synopsys.integration.bdio.model;

import com.synopsys.integration.util.IntegrationEscapeUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BdioIdEscaperTest {
    @Test
    public void testEscapingForUri() {
        final BdioIdEscaper bdioIdEscaper = new BdioIdEscaper();
        assertEquals(null, bdioIdEscaper.escapeForUri(null));
        assertEquals("", bdioIdEscaper.escapeForUri(""));
        assertEquals("%21a%24b%3Ac%401+2--+3", bdioIdEscaper.escapeForUri("!a$b:c@1 2-- 3"));

        final List<String> messyStrings = Arrays.asList(new String[] { "#A(B)C++=", "def", "~\tgh1<>23*i.." });
        final List<String> cleanStrings = Arrays.asList(new String[] { "%23A%28B%29C%2B%2B%3D", "def", "%7E%09gh1%3C%3E23*i.." });
        assertEquals(cleanStrings, bdioIdEscaper.escapePiecesForUri(messyStrings));
    }

    @Test
    public void testDifferentSpecialCharactersStillDistinct() {
        final BdioIdEscaper bdioIdEscaper = new BdioIdEscaper();
        String nameWithAnUnderscore = "a-b-c";
        String nameWithAPeriod = "a.b.c";

        assertNotEquals(bdioIdEscaper.escapeForUri(nameWithAnUnderscore), bdioIdEscaper.escapeForUri(nameWithAPeriod));
    }

    @Test
    public void testDifferentSpecialCharactersNotDistinctWithPoorEscaping() {
        final IntegrationEscapeUtil bdioIdEscaper = new IntegrationEscapeUtil();
        String nameWithAnUnderscore = "a-b-c";
        String nameWithAPeriod = "a.b.c";

        assertEquals(bdioIdEscaper.escapeForUri(nameWithAnUnderscore), bdioIdEscaper.escapeForUri(nameWithAPeriod));
    }

}
