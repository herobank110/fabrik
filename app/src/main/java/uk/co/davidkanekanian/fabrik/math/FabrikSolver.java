package uk.co.davidkanekanian.fabrik.math;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import uk.co.davidkanekanian.fabrik.BuildConfig;

public class FabrikSolver {

    // Solve data
    private List<Vector2f> verts = null;
    private List<Vector2f> lastVerts = null;
    private List<Float> lengths;
    private Vector2f start = new Vector2f();
    private Vector2f goal = new Vector2f();

    // Utility
    private float tolerance = 0.01F;
    private int maxIterations = 10;

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

    /**
     * Move all joints along the chain to reach the goal.
     *
     * If the end effector is out of reach of the joint chain, it will
     * be fully extended towards the effector. If the effector is
     * within reach but there is no previous solve data, the original
     * joint chain is returned.
     *
     * @param goal The end effector to place the end joint of the IK
     * chain.
     * @return Solved vertex position.
     */
    public List<Vector2f> solve(Vector2f goal) {
        if (BuildConfig.DEBUG && verts == null) {
            throw new AssertionError("Cannot solve without vertices. Configure first.");
        }

        // TODO

        return new ArrayList<>();
    }

    public float getTolerance() {
        return tolerance;
    }

    public void setTolerance(float tolerance) {
        this.tolerance = tolerance;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
