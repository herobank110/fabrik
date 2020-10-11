package uk.co.davidkanekanian.fabrik.math;

import org.joml.Vector2f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathStatTest {
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

        assertEquals(result.x, 5.f, 0.f);
        assertEquals(result.y, 5.f, 0.f);
    }
}
