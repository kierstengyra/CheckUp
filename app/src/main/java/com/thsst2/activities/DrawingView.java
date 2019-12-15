package com.thsst2.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private boolean isSigned;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();

        this.isSigned = false;
    }

    public void setupDrawing() {
        this.drawPath = new Path();
        this.drawPaint = new Paint();
        this.canvasPaint = new Paint(Paint.DITHER_FLAG);

        this.drawPaint.setColor(this.paintColor);
        this.drawPaint.setAntiAlias(true);
        this.drawPaint.setStrokeWidth(5);
        this.drawPaint.setStyle(Paint.Style.STROKE);
        this.drawPaint.setStrokeJoin(Paint.Join.ROUND);
        this.drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public boolean getIsSigned() {
        return this.isSigned;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                this.isSigned = true;
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                this.isSigned = true;
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                this.isSigned = true;
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }
}
