package uk.co.davidkanekanian.fabrik;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import org.joml.Vector2f;

import java.util.List;

import uk.co.davidkanekanian.fabrik.math.MathStat;

public class Canvas extends View {
    Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint myPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect myRect = new Rect();
    boolean isFirst = true;
    DrawHelper drawHelper = new DrawHelper(null);
    public MainActivity master;

    public boolean isDown = false;
    public Vector2f fingerLocation = new Vector2f();
    private static final Vector2f effectorHalfSize = new Vector2f(50.f, 50.f);
    private static final Vector2f heldPointSize = new Vector2f(70.f, 70.f);
    private static final Vector2f unheldPointSize = new Vector2f(50.f, 50.f);
    private static final Vector2f indexLabelOffset = new Vector2f(15.f, 50.f);
    private static final float lockedPointRadius = 50.f;

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
        if (isFirst) {
            myPaint.setColor(Color.BLACK);
            myPaint.setTextAlign(Paint.Align.CENTER);
            myPaint.setStrokeWidth(1.f);
            myPaint.setTextSize(48.f);
            canvas.drawText("Tap to add a point",
                    canvas.getWidth() / 2, canvas.getHeight() / 2,
                    myPaint);

            isFirst = false;
            // Maybe the easiest way is to not use a complex interval drawing
            // and just redraw when the model has changed.
            // this.invalidate();
        }

        // Set paint options outside draw loop.
        myPaint.setColor(Color.RED);
        myPaint.setStrokeWidth(3.f);

        myPaint2.setColor(Color.BLACK);
        myPaint2.setStrokeWidth(1.f);
        myPaint2.setTextSize(48.f);
        myPaint2.setTextAlign(Paint.Align.LEFT);

        List<Vector2f> points = master.getPoints();
        if (master.isLocked()) {
            for (int i = 0; i < points.size(); i++) {
                // Draw each point as a circle.
                final Vector2f point = points.get(i);
                canvas.drawCircle(point.x, point.y, lockedPointRadius, myPaint);
            }
            // Draw the end effector as a cross.
            myPaint.setColor(Color.GRAY);
            final Vector2f point = master.getEndEffector();
            final Vector2f size = master.getDragPointContext() != -1 ? heldPointSize : unheldPointSize;
            drawHelper.setCanvas(canvas).drawCross(point, size, myPaint);
        } else {
            for (int i = 0; i < points.size(); i++) {
                // Draw each point as a cross, different size if held.
                final Vector2f point = points.get(i);
                final Vector2f size = i == master.getDragPointContext() ? heldPointSize : unheldPointSize;
                drawHelper.setCanvas(canvas).drawCross(point, size, myPaint);

                canvas.drawText(String.format("%d", i + 1),
                        point.x + indexLabelOffset.x, point.y + indexLabelOffset.y,
                        myPaint2);
            }
        }
    }

    @Override
    public boolean performClick() {
        // Not sure why this is needed but IDE gave warning.
        return super.performClick();
    }
}
