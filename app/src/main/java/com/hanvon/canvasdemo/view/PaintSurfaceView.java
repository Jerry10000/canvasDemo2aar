package com.hanvon.canvasdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/11/22.
 */

public class PaintSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{
    private Context mContex;
    private float mX;
    private float mY;

    private SurfaceHolder sfh;
    private Canvas canvas;
    private float mCurveEndX;
    private float mCurveEndY;

    private Paint mGesturePaint;
    private Path mPath;
    private final Rect mInvalidRect = new Rect();

    private boolean isDrawing;

    private Bitmap mPaintSurface=null;
    private int[] mPixels;
    public Canvas mPaintCanvas = null;
    private Paint mPaint = null;
    private int mHeight;
    private int mWidth;
    private List<Float> mTrackDelPath;
    private int[] rectUpdate;

    public PaintSurfaceView(Context context) {
        super(context);
        mContex = context;
        init();
    }

    public PaintSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContex = context;
        init();
    }
    /**
     * 初始化
     */
    private void init() {
        sfh = getHolder();
        sfh.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        //画笔
        mGesturePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Paint.Style.STROKE);
        mGesturePaint.setStrokeJoin(Paint.Join.ROUND);
        mGesturePaint.setStrokeCap(Paint.Cap.ROUND);
        mGesturePaint.setStrokeWidth(15f);
        mGesturePaint.setColor(Color.parseColor("#FF4081"));
        //路径
        mPath = new Path();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        isDrawing = true;
        Log.e("surfaceCreated","--"+isDrawing);
        //绘制线程
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public void drawCanvas() {
        try {
            canvas = sfh.lockCanvas();
//            canvas.drawBitmap(this.getDrawingCache(),0,0,mGesturePaint);
            if (canvas != null) {
//                canvas.drawColor(Color.WHITE);
                canvas.drawPath(mPath, mGesturePaint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDrawing)
                {
                    Rect rect = touchMove(event);
                    if (rect != null) {
//                        invalidate(rect);
//                        update(rect);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isDrawing)
                {
                    touchUp(event);
//                    invalidate();
//                    update();
                }
                break;
        }
        return true;
    }

    private void touchDown(MotionEvent event)
    {
        isDrawing = true;
        mPath.reset();
        float x = event.getX();
        float y = event.getY();

        mX = x;
        mY = y;

        mPath.moveTo(x, y);

        mInvalidRect.set((int) x, (int) y, (int) x , (int) y);
        mCurveEndX = x;
        mCurveEndY = y;
    }

    private Rect touchMove(MotionEvent event)
    {
        Rect areaToRefresh = null;

        final float x = event.getX();
        final float y = event.getY();

        final float previousX = mX;
        final float previousY = mY;

        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);

        if (dx >= 3 || dy >= 3)
        {
            areaToRefresh = mInvalidRect;
            areaToRefresh.set((int) mCurveEndX , (int) mCurveEndY ,
                    (int) mCurveEndX, (int) mCurveEndY);

            //设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = mCurveEndX = (x + previousX) / 2;
            float cY = mCurveEndY = (y + previousY) / 2;

            //实现绘制贝塞尔平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath.quadTo(previousX, previousY, cX, cY);
            //mPath.lineTo(x, y);

            // union with the control point of the new curve
            /*areaToRefresh矩形扩大了border(宽和高扩大了两倍border)，
             * border值由设置手势画笔粗细值决定
             */
            areaToRefresh.union((int) previousX, (int) previousY,
                    (int) previousX, (int) previousY);
           /* areaToRefresh.union((int) x, (int) y,
                    (int) x, (int) y);*/


            // union with the end point of the new curve
            areaToRefresh.union((int) cX, (int) cY ,
                    (int) cX, (int) cY);

            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
            drawCanvas();
        }
        return areaToRefresh;
    }

    private void touchUp(MotionEvent event)
    {
        isDrawing = false;
    }



    public void Update() {
        Update(new Rect(0, 0, this.getWidth(), this.getHeight()));
    }

    public void Update(Rect rect) {
        if (rect != null && !rect.isEmpty()) {
            try {
                mPaintSurface.setPixels(mPixels, rect.top * this.getWidth()
                                + rect.left, this.getWidth(), rect.left, rect.top,
                        rect.width(), rect.height());
                Canvas canvas = sfh.lockCanvas(rect);
                DrawLayers(canvas);
                sfh.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    public void DrawLayers(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            mPaint.setAlpha(255);
            canvas.drawBitmap(mPaintSurface, 0, 0, mPaint);
        }
    }


    @Override
    public void run() {
        while (isDrawing){
            drawing();
        }
    }

    /**
     * 绘制
     */
    private void drawing() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawPath(mPath,mPaint);
        } finally {
            if (canvas != null) {
                sfh.unlockCanvasAndPost(canvas);
            }
        }
    }
}
