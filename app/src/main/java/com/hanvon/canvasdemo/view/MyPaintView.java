package com.hanvon.canvasdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

//import com.hanvon.canvasdemo.core.HWPenEngine;

import java.util.List;

/**
 * Created by pc on 2017/11/22.
 */

public class MyPaintView extends View{
    private static final String TAG = "MyPaintView";
    private static final Boolean isDebug = false;

    /** View宽 */
    public int mWidth = 1;
    /** View高 */
    public int  mHeight = 1;
    /** 彩笔引擎句柄 */
    public static long penEngine = 0L;
    /** 笔画采样点矩形列表 */
    private List<Rect> rects;
    /** 用于刷新的矩形区域 */
    public int[] bigRect =new int[4];
    private int[] smallRect = new int[4];
    /** 画笔 */
    private static int mBackgroundColor = 0;
    /** 当前画布位图 */
    public Bitmap mBitmap;
    /** 当前画布位图的像素区 */
    private static int[] mPixels;
    private static long[] mPixels1;


    /*************临时的***************/
    private Paint mPaint;
    private Canvas mCanvas;
    private float oldPointX;
    private float oldPointY;
    private float newPointX;
    private float newPointY;
    private Path path = new Path();
    /**********************************/

    public MyPaintView(Context context) {
        super(context);
        //初始化View宽高，若构造函数中没有传入宽高信息，则采用设备的宽高
//        mWidth = CallaViewInfo.width;
//		mHeight = CallaViewInfo.height;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        //初始化画布
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        setBackgroundColor(mBackgroundColor);
        mBitmap.eraseColor(mBackgroundColor);
        mPixels = new int[mWidth * mHeight];
        mBitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
        for (int i = 0; i < mPixels.length; i++){
            mPixels1[i] = (long) mPixels[i];
        }
        //初始化画笔引擎
		initPen();
    }

    public MyPaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化View宽高，若构造函数中没有传入宽高信息，则采用设备的宽高
//        mWidth = CallaViewInfo.width;
//		mHeight = CallaViewInfo.height;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        //初始化画布
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        setBackgroundColor(mBackgroundColor);
        mBitmap.eraseColor(mBackgroundColor);
        mPixels = new int[mWidth * mHeight];
        mBitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
        for (int i = 0; i < mPixels.length; i++){
            mPixels1[i] = (long) mPixels[i];
        }
        //初始化画笔引擎
		initPen();
    }

    public void initPen() {
        if (mPaint == null){
            mPaint = new Paint();
            mPaint.setAlpha(100);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(8);
            mPaint.setAntiAlias(true);
        }
//        HWPenEngine.hwPen_Initialize(mWidth, mHeight, mPixels, 1, 1);
//        HWPenEngine.hwPen_Initialize_Dword(mWidth, mHeight, mPixels1, 1, 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(getX(), getY());
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(getX(), getY());
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(getX(),getY());
                break;
        }
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, mPaint);
    }
}
