package uk.co.davidkanekanian.fabrik;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.Nullable;

import org.joml.Vector2f;

public class DrawHelper {
    private Canvas canvas;

    public DrawHelper(@Nullable Canvas canvas) {
        this.canvas = canvas;
    }

    public DrawHelper drawCross(Vector2f center, Vector2f halfSize, Paint paint) {
        // Make the vertical down line.
        this.canvas.drawLine(
                center.x, center.y - halfSize.y,
                center.x, center.y + halfSize.y,
                paint
        );

        // Draw horizontal "cross" line.
        this.canvas.drawLine(
                center.x - halfSize.x, center.y,
                center.x + halfSize.x, center.y,
                paint
        );

        return this;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public DrawHelper setCanvas(Canvas canvas) {
        this.canvas = canvas;
        return this;
    }
}
