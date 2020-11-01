package uk.co.davidkanekanian.fabrik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.joml.Vector2f;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    /** Canvas to draw world onto. */
    private Canvas canvas;

    /** Point that is being dragged, or  -1 if not being dragged. */
    private int dragPointContext = -1;

    /** ALl active points in the world. */
    private List<Vector2f> points = new ArrayList<>();

    /** Screen distance to point to grab it, in pixels. */
    private final float maxGrabDist = 50.f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vector2f a = new Vector2f(2.25f, 0.f);
        Vector2f b = new Vector2f(5.25f, 10.f);
        a.add(b);

        canvas = findViewById(R.id.fabrik_canvas);
        canvas.setOnTouchListener(this);
        canvas.master = this;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                canvas.isDown = true;
                if (!tryGrabPoint(motionEvent)) {
                    addPointAndGrab(motionEvent);
                }
                break;
            case MotionEvent.ACTION_UP:
                canvas.isDown = false;
                // Invalidate dragged point.
                dragPointContext = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                moveGrabbedPoint(motionEvent);
                break;
        }
        // Redraw canvas.
        canvas.invalidate();
        return true;  // Event is consumed.
    }

    /** Try to grab a point under the finger. */
    private boolean tryGrabPoint(MotionEvent motionEvent) {
        for (int i = 0; i < points.size(); i++) {
            Vector2f point = points.get(i);
            if (point.distance(motionEvent.getX(), motionEvent.getY()) < maxGrabDist) {
                // The point can be dragged!
                dragPointContext = i;
                Log.d("David", "Started dragging point at index " + dragPointContext);
                return true;
            }
        }
        Log.d("David", "Failed to start drag");
        return false;
    }

    /** Add a new point at the touch point. */
    private void addPointAndGrab(MotionEvent motionEvent) {
        points.add(new Vector2f(motionEvent.getX(), motionEvent.getY()));
        // Set the context so touch move events can drag this point.
        dragPointContext = points.size() - 1;
        Log.d("David", "Added point at index " + dragPointContext);
    }

    /** Move the grabbed point, if any, based on a touch event. */
    private void moveGrabbedPoint(MotionEvent motionEvent) {
        canvas.fingerLocation.set(motionEvent.getX(), motionEvent.getY());
        if (dragPointContext != -1) {
            Vector2f point = points.get(dragPointContext);
            point.set(motionEvent.getX(), motionEvent.getY());
            Log.d("David", "Moved point at index " + dragPointContext + " to "
            + motionEvent.getX() + ", " + motionEvent.getY());
        }
    }

    public int getDragPointContext() {
        return dragPointContext;
    }

    public List<Vector2f> getPoints() {
        return points;
    }
}