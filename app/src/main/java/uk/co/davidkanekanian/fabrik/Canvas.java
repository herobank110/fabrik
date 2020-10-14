package uk.co.davidkanekanian.fabrik;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class Canvas extends View {
    Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect myRect = new Rect();
    boolean isFirst = true;

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
            isFirst = false;
            this.invalidate();
        }
    }
}
