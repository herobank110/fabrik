package uk.co.davidkanekanian.fabrik;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

    /** Button to toggle place and edit mode. */
    private Button placeButton;

    /** Whether a new point should be placed on touch. */
    private boolean isInPlaceMode = false;

    /** Point that is being dragged, or  -1 if not being dragged. */
    private int dragPointContext = -1;

    /** ALl active points in the world. */
    private List<Vector2f> points = new ArrayList<>();

    /** Screen distance to point to grab it, in pixels. */
    private float maxGrabDist = 10.f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vector2f a = new Vector2f(2.25f, 0.f);
        Vector2f b = new Vector2f(5.25f, 10.f);
        a.add(b);

        TextView textView = findViewById(R.id.test_text);
        textView.setText(a.toString(NumberFormat.getNumberInstance()));

        canvas = findViewById(R.id.fabrik_canvas);
        canvas.setOnTouchListener(this);

        placeButton = findViewById(R.id.place_button);
        refreshPlaceButton();
        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle place mode on click.
                isInPlaceMode = !isInPlaceMode;
                refreshPlaceButton();
            }
        });
    }

    private void refreshPlaceButton() {
        placeButton.setText(isInPlaceMode ? "edit mode" : "place mode");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                canvas.isDown = true;
                if (isInPlaceMode) {
                    addPointAndGrab(motionEvent);
                } else {
                    tryGrabPoint(motionEvent);
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

    public int getDragPointContext() {
        return dragPointContext;
    }

}