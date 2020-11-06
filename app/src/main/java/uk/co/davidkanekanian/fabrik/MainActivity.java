package uk.co.davidkanekanian.fabrik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import uk.co.davidkanekanian.fabrik.math.FabrikSolver;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    /** Canvas to draw world onto. */
    private Canvas canvas;

    /** Drop dragged point here to delete it. */
    private ImageView deletePointImage;

    /** Point that is being dragged, or  -1 if not being dragged. */
    private int dragPointContext = -1;

    /** ALl active points in the world. */
    private List<Vector2f> points = new ArrayList<>();

    /** Previously created points in the world. */
    private List<Vector2f> lastPoints = new ArrayList<>();

    /** Screen distance to point to grab it, in pixels. */
    private static final float maxGrabDist = 100.f;

    /** Whether the points are in locked mode. */
    private boolean isLocked = false;

    /** Location of the end effector. */
    private Vector2f endEffector = new Vector2f(100.f, 100.f);

    /** Solver of IK system. */
    private FabrikSolver fabrikSolver = new FabrikSolver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvas = findViewById(R.id.fabrik_canvas);
        canvas.setOnTouchListener(this);
        canvas.master = this;

        deletePointImage = findViewById(R.id.delete_point_image);
        refreshDeletePointImage();

        final ImageView moreActionsButton = findViewById(R.id.more_actions_button);
        final LinearLayout moreActionsMenu = findViewById(R.id.more_actions_menu);

        // Hide more actions menu by default.
        moreActionsMenu.setVisibility(View.GONE);

        // Toggle more actions menu visibility when more action button clicked.
        moreActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (moreActionsMenu.getVisibility()) {
                    case View.VISIBLE: moreActionsMenu.setVisibility(View.GONE); break;
                    case View.GONE: moreActionsMenu.setVisibility(View.VISIBLE); break;
                    case View.INVISIBLE: throw new Error("Invalid visibility state for moreActionMenu");
                }
            }
        });

        final ImageView lockButton = findViewById(R.id.lock_button);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { onClickLock(view); }
        });

        final ImageView saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { onClickSave(view); }
        });
    }

    /** Hide the delete button unless drag in operation. */
    private void refreshDeletePointImage() {
        deletePointImage.setVisibility(
                dragPointContext != -1 && !isLocked
                ? View.VISIBLE : View.INVISIBLE);
    }

    private void onClickLock(View view) {
        isLocked = !isLocked;
        if (isLocked) {
            // When from editing to locked.
            // Deep clone all points, not just their references.
            lastPoints.clear();
            for (Vector2f p : points) { lastPoints.add(new Vector2f(p)); }
        } else {
            // Went from locked to editing mode.
            // Restore the points before editing.
            points.clear();
            points.addAll(lastPoints);
        }
        canvas.invalidate();
    }

    private void onClickSave(View view) {
        throw new Error("save not implemented yet");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                canvas.isDown = true;
                if (!tryGrabPoint(motionEvent)) {
                    addPointAndGrab(motionEvent);
                }
                refreshDeletePointImage();
                break;
            case MotionEvent.ACTION_UP:
                canvas.isDown = false;
                tryDeleteGrabbedPoint();
                // Invalidate dragged point regardless of whether it was deleted.
                invalidateDragPointContext();
                refreshDeletePointImage();
                break;
            case MotionEvent.ACTION_MOVE:
                moveGrabbedPoint(motionEvent);
                break;
        }
        // Redraw canvas.
        canvas.invalidate();
        return true;  // Event is consumed.
    }

    /** Make drag context invalid. */
    private void invalidateDragPointContext() {
        dragPointContext = -1;
    }

    /** Delete the dragged point if released over the delete image.
     *
     * Naturally this invalidates drag context if the point was deleted.
     *
     * @returns Whether a point was deleted. */
    private boolean tryDeleteGrabbedPoint() {
        if (dragPointContext != -1) {
            Vector2f point = points.get(dragPointContext);
//                    if (deletePointImage.getClipBounds().contains((int)point.x, (int)point.y)) {
            float w = deletePointImage.getWidth(),
                    h = deletePointImage.getHeight(),
                    x = deletePointImage.getX() + ((LinearLayout)deletePointImage.getParent()).getX(),
                    y = deletePointImage.getY() + ((LinearLayout)deletePointImage.getParent()).getY();
            if (x < point.x && point.x < x + w &&
                    y < point.y && point.y < y + h) {
                // Delete that point.
                points.remove(dragPointContext);
                invalidateDragPointContext();
                return true;
            }
        }
        // Point was not deleted.
        return false;
    }

    /** Try to grab a point under the finger. */
    private boolean tryGrabPoint(MotionEvent motionEvent) {
        if (isLocked) {
            // Snap end effector to finger in any point when locked.
            dragPointContext = 0;
            return true;
        }
        for (int i = 0; i < points.size(); i++) {
            Vector2f point = points.get(i);
            if (isWithinRange(point, motionEvent)) {
                // The point can be dragged!
                dragPointContext = i;
                return true;
            }
        }
        return false;
    }

    private boolean isWithinRange(Vector2f point, MotionEvent motionEvent) {
        return point.distance(motionEvent.getX(), motionEvent.getY()) < maxGrabDist;
    }

    /** Add a new point at the touch point. */
    private void addPointAndGrab(MotionEvent motionEvent) {
        if (!isLocked) {
            points.add(new Vector2f(motionEvent.getX(), motionEvent.getY()));
            // Set the context so touch move events can drag this point.
            dragPointContext = points.size() - 1;
        }
    }

    /** Move the grabbed point, if any, based on a touch event. */
    private void moveGrabbedPoint(MotionEvent motionEvent) {
        canvas.fingerLocation.set(motionEvent.getX(), motionEvent.getY());
        if (dragPointContext != -1) {
            final Vector2f point = isLocked ? endEffector : points.get(dragPointContext);
            point.set(motionEvent.getX(), motionEvent.getY());
            if (isLocked) {
                // Update fabrik solver.
                fabrikSolver.configure(points);
                fabrikSolver.solve(new Vector2f(endEffector));
            }
        }
    }

    public int getDragPointContext() {
        return dragPointContext;
    }

    public List<Vector2f> getPoints() {
        return points;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public Vector2f getEndEffector() {
        return endEffector;
    }
}