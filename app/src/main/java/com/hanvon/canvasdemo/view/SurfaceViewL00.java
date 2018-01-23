package com.hanvon.canvasdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hanvon.canvasdemo.engine.HwPenEngine;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by pc on 2017/11/23.
 */

public class SurfaceViewL00 extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "SurfaceViewL";
    // SurfaceHolder
    private SurfaceHolder mSurfaceHolder;
    // 画布
    private Canvas mCanvas;
    // 子线程标志位
    private boolean isDrawing;
    private Bitmap mBitmap;
    private int mWidth, mHeight;
    // 画笔
    private Paint mPaint;
    private Shader mShader;
    private Bitmap mUpdateBitmap;
    private int[] mPixels;
//    private HwColorPen hwColorPen;
//    private HwColorPen hwColorPen1;
    private int[] updateRect = new int[4];
    private int[] updateRect1 = new int[4];
    private Rect bitRect = new Rect(0,0,0,0);
    private LinkedList<Rect> rects = new LinkedList<Rect>();
    private HwPenEngine mPenEngine;


    // 路径
    Path mPath;
    private float mLastX, mLastY;//上次的坐标
    private Context context;
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public SurfaceViewL00(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SurfaceViewL00(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        //初始化 SurfaceHolder mSurfaceHolder
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);

        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        //画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStrokeWidth(50f);
        mPaint.setColor(Color.parseColor("#80FF4081"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

//        //设置着色器
//        mUpdateBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.template);
//        mShader = new BitmapShader(mUpdateBitmap,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT);
//        mPaint.setShader(mShader);

        //路径
        mPath = new Path();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {//创建
        isDrawing = true;
        Log.e("surfaceCreated","--"+isDrawing);
        mWidth = getWidth();
        mHeight = getHeight();
        mPixels = new int[mWidth * mHeight];

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

//        hwColorPen = new HwColorPen();
//        hwColorPen.setBmp(mBitmap);
//        hwColorPen.setPenInfo(1, HwColorPen.PS_BRUSHPEN, HwColorPen.PC_BLACK, 5, 0);
//        hwColorPen1 = new HwColorPen();
//        hwColorPen1.setBmp(mBitmap);
//        hwColorPen1.setPenInfo(1, HwColorPen.PS_FOUNTAINPEN, HwColorPen.PC_BLUE, 5, 0);
        mPixels = new int[mWidth * mHeight];
        mBitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
        if(mPenEngine == null){
            mPenEngine = new HwPenEngine();
        }
        mPenEngine.init(mWidth, mHeight, mPixels);
        mPenEngine.setPenInfo(0, HwPenEngine.PEN_TYPE_INK, 0x800000ff, 45, 0);

        //绘制线程
        new Thread(this).start();
    }

    private void changePixels(){

        mBitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
        for (int x = 0; x < 60; x++){
            mPixels[x] = 0xff000000;
        }
        for(int j = 10; j>0; j--) {
            for(int i = mWidth; i>1; i--) {
                mPixels[mWidth*(j+700)+i] = 0xff000000;
            }
        }

        mBitmap.setPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
        update(new Rect(10, 10, 500, 900));
//        update(new Rect(0, 0, mWidth, mHeight));
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {//改变

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {//销毁
        isDrawing = false;
        Log.e("surfaceDestroyed","--"+isDrawing);
    }

    @Override
    public void run() {
        while (isDrawing){
//            drawing();
        }
    }

    /**
     * 绘制
     */
    private void drawing() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null){
                mCanvas.drawPath(mPath,mPaint);

            }
        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
////        hwColorPen.drawLine((int) x, (int) y, 255, updateRect);
////        hwColorPen1.drawLine((int) x, (int) (y + 40), 255, updateRect1);
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//
//                mLastX = x;
//                mLastY = y;
//                mPath.reset();
//                mPath.moveTo(mLastX, mLastY);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float dx = Math.abs(x - mLastX);
//                float dy = Math.abs(y - mLastY);
//                if (dx >= 3 || dy >= 3) {
//                    mPath.quadTo(mLastX, mLastY, (mLastX + x) / 2, (mLastY + y) / 2);
//                }
//                mLastX = x;
//                mLastY = y;
//                break;
//            case MotionEvent.ACTION_UP:
//                short[] mStroke_s_2 = new short[]{230, 58, 217, 57, 208, 57, 197, 59, 181, 65, 166, 75, 152, 86, 141, 100, 131, 117, 123, 137, 119, 155, 118, 170, 119, 181, 123, 192, 132, 203, 144, 211, 158, 217, 175, 218, 193, 215, 215, 208, 235, 199, 251, 191, 268, 182, 284, 175, 299, 168, 311, 163, 322, 159, 331, 157, 339, 157, 344, 157, 348, 159, 352, 163, 355, 167, 359, 173, 361, 179, 362, 186, 362, 194, 362, 203, 360, 212, 357, 224, 354, 234, 350, 244, 345, 254, 339, 264, 332, 273, 325, 281, 318, 289, 310, 293, 303, 294, 295, 294, 290, 294, 284, 297, 279, 301, 275, 302, 271, 306, 268, 308, 266, 309, 264, 310, -1, 0, -1, -1};
//                short[] mStroke_s_1 = new short[]{268, 58, 258, 53, 251, 49, 245, 47, 239, 43, 231, 41, 222, 41, 212, 41, 198, 43, 184, 48, 172, 56, 161, 65, 152, 74, 145, 84, 141, 93, 140, 101, 141, 106, 144, 112, 151, 119, 160, 126, 171, 131, 184, 135, 197, 137, 214, 138, 232, 137, 250, 135, 268, 132, 286, 129, 301, 125, 314, 121, 325, 120, 335, 120, 341, 120, 346, 121, 352, 124, 357, 128, 362, 133, 364, 140, 365, 147, 365, 154, 363, 162, 360, 172, 354, 180, 348, 189, 342, 196, 334, 204, 327, 211, 319, 217, 312, 222, 305, 227, 301, 230, 295, 232, 292, 234, 289, 235, 286, 237, 284, 238, 285, 239, -1, 0, -1, -1};
//
//                mPath.reset();
//                mPath.moveTo(230, 58);
//                for (int i = 2; i < mStroke_s_1.length-4; i+=2){
//                    mPath.lineTo(mStroke_s_1[i], mStroke_s_1[i+1]);
//                }
//
//                drawing();
////                changePixels();
////                hwColorPen.drawLine(-1, -1, 255, updateRect);
////                hwColorPen1.drawLine(-1, -1, 255, updateRect1);
////                update(new Rect(0, 0, mWidth, mHeight));
//                break;
//        }
//        return true;
//    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float pressure = 1.0f;
        if (e.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
            pressure = 1.0f;
        } else if (e.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
            pressure = e.getPressure();
        } else if (e.getToolType(0) == MotionEvent.TOOL_TYPE_ERASER) {
            pressure = e.getPressure();
        }
        final Rect[] rect = new Rect[1];

        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPenEngine.beginStroke();
                break;
            case MotionEvent.ACTION_MOVE:
                mPenEngine.strokePoint(e.getX(), e.getY(), pressure, updateRect);
                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        rect[0] = new Rect(updateRect[0], updateRect[1], updateRect[2], updateRect[3]);
                        bitRect.union(rect[0]);
                        rects.add(rect[0]);
                        update(bitRect);
//                        update(new Rect(updateRect[0], updateRect[1], updateRect[2], updateRect[3]));
                    }
                });
                break;
            case MotionEvent.ACTION_UP:
                mPenEngine.endStroke(updateRect);
                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        rect[0] = new Rect(updateRect[0], updateRect[1], updateRect[2], updateRect[3]);
                        bitRect.union(rect[0]);
                        rects.add(rect[0]);
                        update(bitRect);
//                        update(new Rect(updateRect[0], updateRect[1], updateRect[2], updateRect[3]));
                    }
                });
                break;
        }
        return true;
    }


    public void update(Rect rect) {
        if (rect != null && !rect.isEmpty()) {
            try {
                //更新的是整个屏幕
                mBitmap.setPixels(mPixels, rect.top * this.getWidth()+ rect.left, mWidth,
                        rect.left, rect.top, rect.width(), rect.height());
//                //更新此矩形区域
//                mUpdateBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
//                mUpdateBitmap.setPixels(mPixels, rect.top * this.getWidth()+ rect.left,
//                        this.getWidth(), 0, 0, rect.width(), rect.height());

                Canvas canvas = mSurfaceHolder.lockCanvas(rect);
                DrawLayers(canvas);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void DrawLayers(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            //canvas.drawBitmap(mPaintSurface, matrix, paint)
            mPaint.setAlpha(255);
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        }
    }

    /**
     * 测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (wSpecMode == MeasureSpec.AT_MOST && hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300);
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, hSpecSize);
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wSpecSize, 300);
        }
    }
}
