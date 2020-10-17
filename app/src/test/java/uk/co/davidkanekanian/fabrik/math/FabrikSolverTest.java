package uk.co.davidkanekanian.fabrik.math;

import org.joml.Vector2f;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FabrikSolverTest {
    @Test
    /**
     * When goal is out of reach the joint chain should be fully
     * extended towards the goal.
     */
    public void solve_outOfReach() {
        FabrikSolver solver = new FabrikSolver();

        // First bone is 10 units long.
        // Second bone is 15 units long.
        // Chain is 25 units long.
        solver.configure(Arrays.asList(
                new Vector2f(.0f, .0f),
                new Vector2f(10.0f, .0f),
                new Vector2f(10.0f, 15.0f)
        ));

        // Putting the goal 100 units away is out of reach.
        List<Vector2f> verts = solver.solve(new Vector2f(100.f, 0.f));

        // The goal is pointing in the positive X axis so points should
        // be aligned along the X axis with their correct lengths.
        float delta = 0.f;
        assertEquals(0.f, verts.get(0).x, delta);
        assertEquals(0.f, verts.get(0).y, delta);
        assertEquals(10.f, verts.get(1).x, delta);
        assertEquals(0.f, verts.get(1).y, delta);
        assertEquals(25.f, verts.get(2).x, delta);
        assertEquals(0.f, verts.get(2).y, delta);
    }
}
