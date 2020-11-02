package uk.co.davidkanekanian.fabrik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    /** Canvas to draw world onto. */
    private Canvas canvas;

    /** Drop dragged point here to delete it. */
    private ImageView deletePointImage;

    /** Point that is being dragged, or  -1 if not being dragged. */
    private int dragPointContext = -1;

    /** ALl active points in the world. */
    private List<Vector2f> points = new ArrayList<>();

    /** Screen distance to point to grab it, in pixels. */
    private static final float maxGrabDist = 100.f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvas = findViewById(R.id.fabrik_canvas);
        canvas.setOnTouchListener(this);
        canvas.master = this;

        deletePointImage = findViewById(R.id.delete_point_image);
        refreshDeletePointImage();
    }

    /** Hide the delete button unless drag in operation. */
    private void refreshDeletePointImage() {
        deletePointImage.setVisibility(dragPointContext != -1 ? View.VISIBLE : View.INVISIBLE);
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
                    x = deletePointImage.getX(),
                    y = deletePointImage.getY();
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
        for (int i = 0; i < points.size(); i++) {
            Vector2f point = points.get(i);
            if (point.distance(motionEvent.getX(), motionEvent.getY()) < maxGrabDist) {
                // The point can be dragged!
                dragPointContext = i;
                return true;
            }
        }
        return false;
    }

    /** Add a new point at the touch point. */
    private void addPointAndGrab(MotionEvent motionEvent) {
        points.add(new Vector2f(motionEvent.getX(), motionEvent.getY()));
        // Set the context so touch move events can drag this point.
        dragPointContext = points.size() - 1;
    }

    /** Move the grabbed point, if any, based on a touch event. */
    private void moveGrabbedPoint(MotionEvent motionEvent) {
        canvas.fingerLocation.set(motionEvent.getX(), motionEvent.getY());
        if (dragPointContext != -1) {
            Vector2f point = points.get(dragPointContext);
            point.set(motionEvent.getX(), motionEvent.getY());
        }
    }

    public int getDragPointContext() {
        return dragPointContext;
    }

    public List<Vector2f> getPoints() {
        return points;
    }
}