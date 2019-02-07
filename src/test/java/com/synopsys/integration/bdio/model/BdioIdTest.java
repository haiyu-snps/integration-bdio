package com.synopsys.integration.bdio.model;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class BdioIdTest {
    @Test
    public void testBdioIdFromUUID() {
        UUID uuid = UUID.randomUUID();
        BdioId fromUUID = BdioId.createFromUUID(uuid.toString());

        Set<BdioId> ids = new HashSet<>();
        ids.add(fromUUID);

        String expected = "uuid:" + uuid.toString();
        assertEquals(expected, fromUUID.toString());
        assertTrue(ids.contains(new BdioId(expected)));
    }

    @Test
    public void testBdioIdFromPieces() {
        BdioId fromPieces = BdioId.createFromPieces("one", "two", "three");

        Set<BdioId> ids = new HashSet<>();
        ids.add(fromPieces);

        String expected = "http:one/two/three";
        assertEquals(expected, fromPieces.toString());
        assertTrue(ids.contains(new BdioId(expected)));
    }

    @Test
    public void testGson() {
        Gson gson = new Gson();
        BdioId fromPieces = BdioId.createFromPieces("one", "two", "three");
        String expected = "\"http:one/two/three\"";

        // should serialize to a String
        String json = gson.toJson(fromPieces);
        assertEquals(expected, json);

        BdioId fromGson = gson.fromJson(expected, BdioId.class);
        assertEquals(fromPieces, fromGson);
    }

}
