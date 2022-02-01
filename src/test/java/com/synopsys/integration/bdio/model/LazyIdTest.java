package com.synopsys.integration.bdio.model;

import com.synopsys.integration.bdio.graph.builder.LazyId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LazyIdTest {
    @Test
    public void testStringDependencyId() {
        final LazyId id1 = LazyId.fromString("hello");
        final LazyId id2 = LazyId.fromString("hello");
        final LazyId idDiff = LazyId.fromString("goodbye");

        assertEquals(id1, id2);
        assertNotEquals(id1, idDiff);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), idDiff.hashCode());
        assertEquals(id1.toString(), id2.toString());
        assertNotEquals(id1.toString(), idDiff.toString());
    }

    @Test
    public void testNameDependencyId() {
        final LazyId id1 = LazyId.fromName("hello");
        final LazyId id2 = LazyId.fromName("hello");
        final LazyId idDiff = LazyId.fromName("goodbye");

        assertEquals(id1, id2);
        assertNotEquals(id1, idDiff);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), idDiff.hashCode());
        assertEquals(id1.toString(), id2.toString());
        assertNotEquals(id1.toString(), idDiff.toString());
    }

    @Test
    public void testNameVersionDependencyId() {
        final LazyId id1 = LazyId.fromNameAndVersion("hello", "hi");
        final LazyId id2 = LazyId.fromNameAndVersion("hello", "hi");
        final LazyId idDiff = LazyId.fromNameAndVersion("hello", "bye");

        assertEquals(id1, id2);
        assertNotEquals(id1, idDiff);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), idDiff.hashCode());
        assertEquals(id1.toString(), id2.toString());
        assertNotEquals(id1.toString(), idDiff.toString());
    }

    @Test
    public void testDiffDependencyId() {
        final LazyId id1 = LazyId.fromName("hello");
        final LazyId id2 = LazyId.fromNameAndVersion("hello", "hi");
        final LazyId id3 = LazyId.fromName("goodbye");

        assertNotEquals(id1, id2);
        assertNotEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.toString(), id2.toString());

        assertNotEquals(id1, id3);
        assertNotEquals(id1.hashCode(), id3.hashCode());
        assertNotEquals(id1.toString(), id3.toString());

        assertNotEquals(id2, id3);
        assertNotEquals(id2.hashCode(), id3.hashCode());
        assertNotEquals(id2.toString(), id3.toString());
    }

}
