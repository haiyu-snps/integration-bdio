package com.synopsys.integration.bdio.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameVersionDependencyId;
import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;

public class DependencyIdTest {

    @Test
    public void testStringDependencyId() {
        final DependencyId id1 = new StringDependencyId("hello");
        final DependencyId id2 = new StringDependencyId("hello");
        final DependencyId idDiff = new StringDependencyId("goodbye");

        assertEquals(id1, id2);
        assertNotEquals(id1, idDiff);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), idDiff.hashCode());
        assertEquals(id1.toString(), id2.toString());
        assertNotEquals(id1.toString(), idDiff.toString());

    }

    @Test
    public void testNameDependencyId() {
        final DependencyId id1 = new NameDependencyId("hello");
        final DependencyId id2 = new NameDependencyId("hello");
        final DependencyId idDiff = new NameDependencyId("goodbye");

        assertEquals(id1, id2);
        assertNotEquals(id1, idDiff);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), idDiff.hashCode());
        assertEquals(id1.toString(), id2.toString());
        assertNotEquals(id1.toString(), idDiff.toString());
    }

    @Test
    public void testNameVersionDependencyId() {
        final DependencyId id1 = new NameVersionDependencyId("hello", "hi");
        final DependencyId id2 = new NameVersionDependencyId("hello", "hi");
        final DependencyId idDiff = new NameVersionDependencyId("hello", "bye");

        assertEquals(id1, id2);
        assertNotEquals(id1, idDiff);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), idDiff.hashCode());
        assertEquals(id1.toString(), id2.toString());
        assertNotEquals(id1.toString(), idDiff.toString());
    }

    @Test
    public void testDiffDependencyId() {
        final DependencyId id1 = new NameDependencyId("hello");
        final DependencyId id2 = new NameVersionDependencyId("hello", "hi");
        final DependencyId id3 = new NameDependencyId("goodbye");

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
