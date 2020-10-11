package uk.co.davidkanekanian.fabrik.math;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Static library of various math helper functions.
 */
public final class MathStat {
    /** @return distance between two points */
    public static float getDist(Vector2f a, Vector2f b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /** @return edge lengths between vertices in a poly line. */
    public static List<Float> getEdgeLengthsOpen(List<Vector2f> verts) {
        List<Float> lengths = new ArrayList<>();
        for (int i = 1; i < verts.size(); i++) {
            lengths.add(MathStat.getDist(verts.get(i), verts.get(i - 1)));
        }
        return lengths;
    }

    /** Linearly interpolate between a and b. */
    public static float lerp(float a, float b, float bias) {
        return a + (b - a) * bias;
    }

    public static float lerp(int a, int b, float bias) {
        return a + (b - a) * bias;
    }

    public static Vector2f lerp(Vector2f a, Vector2f b, float bias) {
        Vector2f ret = new Vector2f(b);
        return ret.sub(a).mul(bias).add(a);
    }
}