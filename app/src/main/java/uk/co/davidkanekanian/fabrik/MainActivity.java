package uk.co.davidkanekanian.fabrik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.joml.Vector2f;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private Canvas canvas;
    private Button placeButton;
    private boolean isInPlaceMode = false;
    private int dragPointContext = -1;
    private List<Vector2f> points = new ArrayList<>();

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
                    // Add a new point here.
                    points.add(new Vector2f(motionEvent.getX(), motionEvent.getY()));
                } else {
                    //
                }
                break;
            case MotionEvent.ACTION_UP:
                canvas.isDown = false;
                break;
            case MotionEvent.ACTION_MOVE:
                canvas.fingerLocation.set(motionEvent.getX(), motionEvent.getY());
                if (dragPointContext != -1) {
                    if (isInPlaceMode) {
                        // Move last point.
                        Vector2f point = points.get(dragPointContext);
                        point.set(motionEvent.getX(), motionEvent.getY());
                    }
                }
                break;
        }
        // Redraw canvas.
        canvas.invalidate();
        return true;  // Event is consumed.
    }
}