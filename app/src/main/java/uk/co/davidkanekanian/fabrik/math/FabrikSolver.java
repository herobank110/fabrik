package uk.co.davidkanekanian.fabrik.math;

import org.joml.Vector2f;

import java.util.List;

import uk.co.davidkanekanian.fabrik.BuildConfig;

public class FabrikSolver {

    private List<Vector2f> verts = null;
    private List<Float> lengths;
    private Vector2f start = new Vector2f();
    private Vector2f goal = new Vector2f();

    /**
     * Prepare the data for solving.
     *
     * Only needs to be called if the vertices positions changed or
     * vertices were added or removed.
     *
     * @param newVerts The new vertices to use.
     */
    public void configure(List<Vector2f> newVerts) {
        if (BuildConfig.DEBUG && newVerts.size() == 0) {
            throw new AssertionError("Fabrik solver requires at least 1 point");
        }

        verts = newVerts;
        lengths = MathStat.getEdgeLengthsOpen(newVerts);
        start.set(newVerts.get(0));
        goal.set(newVerts.get(newVerts.size() - 1));
    }

    public void solve() {
        // TODO
    }

}
