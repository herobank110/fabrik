package uk.co.davidkanekanian.fabrik.math;

import org.joml.Vector2f;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MathStatTest {
    @Test
    public void getDist() {
        Vector2f a = new Vector2f(0.f, 0.f);
        Vector2f b = new Vector2f(10.f, 10.f);

        assertEquals(14.14214f, MathStat.getDist(a, b), 0.0001f);
    }

    @Test
    public void getEdgeLengthsOpen() {
        List<Vector2f> verts = Arrays.asList(
                new Vector2f(0.f, 0.f),
                new Vector2f(10.f, 0.f),
                new Vector2f(10.f, 10.f)
        );
        List<Float> lengths = MathStat.getEdgeLengthsOpen(verts);

        assertEquals(2, lengths.size());
        assertEquals(10.f, lengths.get(0), 0.f);
        assertEquals(10.f, lengths.get(1), 0.f);
    }

    @Test
    public void lerp_int() {
        assertEquals(5.f, MathStat.lerp(0, 10, 0.5f), 0.f);
    }

    @Test
    public void lerp_float() {
        assertEquals(5.f, MathStat.lerp(0.f, 10.f, 0.5f), 0.f);
    }

    @Test
    public void lerp_vector() {
        Vector2f a = new Vector2f(0.f, 0.f);
        Vector2f b = new Vector2f(10.f, 10.f);
        Vector2f result = MathStat.lerp(a, b, 0.5f);

        assertEquals(5.f, result.x, 0.f);
        assertEquals(5.f, result.y, 0.f);
    }

    @Test
    public void sum() {
        List<Float> a = Arrays.asList(1.f, 2.f, 3.f);
        assertEquals(6.f, MathStat.sum(a), 0.f);
    }
}
