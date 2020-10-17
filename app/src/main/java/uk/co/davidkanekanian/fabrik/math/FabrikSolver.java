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
     * Warning: Saves joint data between calls to solve.
     *
     * @param goal The end effector to place the end joint of the IK
     * chain.
     * @return Solved vertex position.
     */
    public List<Vector2f> solve(Vector2f goal) {
        if (BuildConfig.DEBUG && verts == null) {
            throw new AssertionError("Cannot solve without vertices. Configure first.");
        }

        float chainLength = MathStat.sum(lengths);
        if (MathStat.getDist(start, goal) < chainLength) {
            // The effector is beyond joint capability.
            straightenTowardsGoal();
        } else {
            // The effector is within reach.
            if (lastVerts == null) {
                // Cannot solve if last verts not valid.
                // Prepare last verts for next iteration.
                lastVerts = new ArrayList<>(verts);
                // Return original vertices unchanged.
                return verts;
            }
            doSolve();
        }

        // Save for the next solve.
        // TODO find a way to avoid dynamic allocation if possible
        lastVerts = new ArrayList<>(verts);

        return verts;
    }

    private void straightenTowardsGoal() {
        Vector2f direction = new Vector2f(start);
        direction.sub(goal);
        // No vector division function exists!
        direction.mul(1 / direction.length());
        Vector2f dirTemp = new Vector2f(direction);

        Vector2f prev = new Vector2f();
        for (int i = 0; i + 1 < verts.size(); i++) {
            float lengthToNext = lengths.get(i);
            dirTemp.set(direction).mul(lengthToNext);
            prev.set(verts.get(i));
            verts.set(i + 1, prev.add(dirTemp));
        }
    }

    private void doSolve() {
        for (int iterCount = 0; iterCount < maxIterations; iterCount++) {
            // Put the end vertex at the goal.
            verts.set(verts.size() - 1, goal);

            // Iterate backwards to solve in place.
            Vector2f direction = new Vector2f();
            Vector2f prev = new Vector2f();
            for (int i = verts.size() - 1; i > 0; i--) {
                direction.set(verts.get(i + 1)).sub(verts.get(i));
                direction.mul(1 / direction.length());
                prev.set(verts.get(i));
                verts.set(i + 1, prev.add(direction.mul(lengths.get(i - 1))));
            }

            // Put the first point at the original start.
            verts.set(0, start);

            // Iterate forwards to solve in place.
            for (int i = 0; i < verts.size() - 1; i++) {
                direction.set(verts.get(i + 1)).sub(verts.get(i));
                direction.mul(1 / direction.length());
                prev.set(verts.get(i));
                verts.set(i + 1, prev.add(direction.mul(lengths.get(i))));
            }

            Vector2f toleranceCheck = new Vector2f(verts.get(verts.size() - 1));
            if (toleranceCheck.sub(goal).length() < tolerance) {
                // Stop early if within allowed tolerance.
                break;
            }
        }
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
