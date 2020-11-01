package uk.co.davidkanekanian.fabrik;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import org.joml.Vector2f;

import uk.co.davidkanekanian.fabrik.math.MathStat;

public class Canvas extends View {
    Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect myRect = new Rect();
    boolean isFirst = true;
    DrawHelper drawHelper = new DrawHelper(null);

    public boolean isDown = false;
    public Vector2f fingerLocation = new Vector2f();
    private final Vector2f effectorHalfSize = new Vector2f(50.f, 50.f);

    // All constructor overloads of View have to be defined!

    public Canvas(Context context) {
        super(context);
    }

    public Canvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Canvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Canvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        // Seems to only be called once. Maybe call multiple times.
        // But the next time it is drawn (with invalidate()) the canvas
        // is cleared automatically.
        Log.d("My canvas", "drawing");
        if (isFirst) {
            myRect.set(10, 10, 100, 100);
            myPaint.setColor(Color.RED);
            canvas.drawRect(myRect, myPaint);

            //canvas.drawLine(100.f, 10.f, 100.f, 200.f, myPaint);
            drawHelper.setCanvas(canvas)
                    .drawCross(
                            new Vector2f(110.f + 45.f, MathStat.lerp(10.f, 100.f, 0.5f)),
                            new Vector2f(45.f, 45.f),
                            myPaint);

            isFirst = false;
            // Maybe the easiest way is to not use a complex interval drawing
            // and just redraw when the model has changed.
            // this.invalidate();
        }

        if (isDown) {
            drawHelper.setCanvas(canvas)
                    .drawCross(fingerLocation, effectorHalfSize, myPaint);
        }
    }

    @Override
    public boolean performClick() {
        // Not sure why this is needed but IDE gave warning.
        return super.performClick();
    }
}
