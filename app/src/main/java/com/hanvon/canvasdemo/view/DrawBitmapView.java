package com.hanvon.canvasdemo.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.hanvon.canvasdemo.R;

/**
 * Created by pc on 2017/10/26.
 */

public class DrawBitmapView extends View {
    private Resources mResources;
    private Paint mBitPaint;
    private Bitmap mBitmap;
    private Rect mSrcRect, mDestRect;
    private int mBitWidth, mBitHeight;
    private Canvas mCanvas;
    private int width;
    private int height;

    // view 的宽高
    private int mTotalWidth, mTotalHeight;

    public DrawBitmapView(Context context) {
        super(context);
        mResources = getResources();

//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        width = dm.widthPixels;
//        height = dm.heightPixels;
//        mCanvas = new Canvas(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
        initBitmap();
        initPaint();

    }

    private void initPaint() {
        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
    }

    private void initBitmap() {
        mBitmap = ((BitmapDrawable) mResources.getDrawable(R.mipmap.ic_launcher))
                .getBitmap();
        mBitWidth = mBitmap.getWidth();
        mBitHeight = mBitmap.getHeight();
    }

    protected void drawImg(int x, int y){
        int left, top;
        // 计算左边位置
        if((x - mBitWidth / 2) < 0){
            left = 0;
        }else if((x + mBitWidth / 2) > mTotalWidth){
            left = mTotalWidth - mBitWidth / 2;
        }else{
            left = x - mBitWidth / 2;
        }

        // 计算上边位置
        if((y - mBitHeight / 2) < 0){
            top = 0;
        }else if((y + mBitHeight / 2) > mTotalHeight){
            top = mTotalHeight - mBitHeight / 2;
        }else{
            top = x - mBitHeight / 2;
        }
        mDestRect = new Rect(left, top, left + mBitWidth, top + mBitHeight);
        mCanvas.drawBitmap(mBitmap, mSrcRect, mDestRect, mBitPaint);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        drawImg((int) event.getX(), (int) event.getY());
//        postInvalidate();
//        return true;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mSrcRect = new Rect(0, 0, mBitWidth, mBitHeight);
        mDestRect = new Rect(50, 50, 50 + mBitWidth, 50 + mBitHeight);
        mCanvas.drawBitmap(mBitmap, mSrcRect, mDestRect, mBitPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;

    }
}
