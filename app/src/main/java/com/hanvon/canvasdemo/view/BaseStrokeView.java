package com.hanvon.canvasdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hanvon.canvasdemo.constants.CallaViewInfo;
import com.hanvon.canvasdemo.core.HWPenEngine;

import java.util.List;


/**
 * 一个带有彩色画笔的View
 */
public class BaseStrokeView extends View {
	private static final String TAG = "BaseStrokeView";
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

	public BaseStrokeView(Context context) {
		super(context);
		//初始化View宽高，若构造函数中没有传入宽高信息，则采用设备的宽高
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
//		mWidth = CallaViewInfo.width;
//		mHeight = CallaViewInfo.height;
		Log.e(TAG, "BaseStrokeView: width = " + mWidth);
		Log.e(TAG, "BaseStrokeView: height = " + mHeight);
		//初始化画布
		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		Log.e(TAG, "BaseStrokeView: createBitmap之后");
		setBackgroundColor(mBackgroundColor);
		mBitmap.eraseColor(mBackgroundColor);
		mPixels = new int[mWidth * mHeight];
		mBitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
		Log.e(TAG, "BaseStrokeView: getPixels之后");
		//初始化画笔引擎
//		initPen();
		Log.e(TAG, "BaseStrokeView: initPen之后");
	}
//	public BaseStrokeView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		mWidth = CallaViewInfo.width;
//		mHeight = CallaViewInfo.height;
//		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//		setBackgroundColor(mBackgroundColor);
//		mBitmap.eraseColor(mBackgroundColor);
//		mPixels = new int[mWidth * mHeight];
//		mBitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
//		initPen();
//	}
//	public BaseStrokeView(Context context, int width, int height) {
//		super(context);
//		//初始化View宽高
//		mWidth = width;
//		mHeight = height;
//		//初始化画布
//		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//		setBackgroundColor(mBackgroundColor);
//		mBitmap.eraseColor(mBackgroundColor);
//		mPixels = new int[mWidth * mHeight];
//		mBitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
//		//初始化画笔引擎
//		initPen();
//	}

//	public void destroy(){
//		if(mBitmap!=null)
//			mBitmap.recycle();
//		mBitmap = null;
//		if (mPixels != null){
//			mPixels = null;
//		}
//		if(penEngine != 0)
//		{
////			penEngine.releaseBmp();
//			HWPenEngine.destroyEngine(penEngine);
//			penEngine = 0L;
//		}
//	}
//
//	/**
//	 * 合并矩形
//	 * @param rect			被合并的矩形
//	 * @param bigRect		合并的大矩形
//	 */
//	private void uniRect(int[] rect,int[] bigRect)
//	{
//		if(rect[0]<bigRect[0])
//			bigRect[0] = rect[0];
//		if(rect[1]<bigRect[1])
//			bigRect[1] = rect[1];
//		if(rect[2]>bigRect[2])
//			bigRect[2] = rect[2];
//		if(rect[3]>bigRect[3])
//			bigRect[3] = rect[3];
//	}
//
//	/**
//	 * 设置View大小
//	 * @param width 宽
//	 * @param height 高
//	 */
//	public void reSize(final int width, final int height) {
//		if (width <= 0) {
//			mWidth = CallaViewInfo.width;
//		} else {
//			mWidth = width;
//		}
//		if (height <= 0) {
//			mHeight = CallaViewInfo.height;
//		} else {
//			mHeight = height;
//		}
//		// 判断当前画布状态，若不为空且宽高发生变化则销毁，意思就是若不为空且宽高没发生变化就不用重建了
//		if (mBitmap != null && (mBitmap.getWidth() != mWidth || mBitmap.getHeight() != mHeight)) {
//			if (!mBitmap.isRecycled())
//			{
//				mBitmap.recycle();
//			}
//			mBitmap = null;
//			if(penEngine != 0){
////				penEngine.releaseBmp();
//				HWPenEngine.destroyEngine(penEngine);
//				penEngine = 0;
//			}
//			System.gc();
//		}
//		// 判断当前画布是否为空，若为空则重建
//		if (mBitmap == null) {
//			mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//		}
//		//初始化画布，并获取像素区
//		setBackgroundColor(mBackgroundColor);
//		mBitmap.eraseColor(mBackgroundColor);
//		mPixels = new int[mWidth * mHeight];
//		mBitmap.getPixels(mPixels, 0, mWidth, 0, 0, mWidth, mHeight);
//		//初始化画笔核心
//	    initPen();
//		measure(MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.UNSPECIFIED),
//				MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.UNSPECIFIED));
//
//	}
	public static void initPen() {
		String path = "/mnt/sdcard/";
		if (penEngine == 0){
			penEngine = HWPenEngine.initialize(CallaViewInfo.width, CallaViewInfo.height, mPixels);
		}
		//设置画笔参数
		HWPenEngine.fillSurface(penEngine, 0x00ffffff);
		HWPenEngine.setPenStyle(penEngine, 14);
		HWPenEngine.setColor(penEngine, (byte)128, (byte)128, (byte)128, (byte)155);
		HWPenEngine.setPenSize(penEngine, 35);
		HWPenEngine.createMemoryResource(30);
		HWPenEngine.setMaxPointsPerSubScreen(256);
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		float x = event.getX();
//		float y = event.getY();
//		switch (event.getAction()){
//			case MotionEvent.ACTION_DOWN:
//				HWPenEngine.strokePointStoreScreen(penEngine, x, y, 1.0f, smallRect);
//				break;
//			case MotionEvent.ACTION_MOVE:
//				HWPenEngine.strokePointStoreScreen(penEngine, x, y, 1.0f, smallRect);
//				break;
//			case MotionEvent.ACTION_UP:
//				HWPenEngine.strokePointStoreScreen(penEngine, -1, -1, 1.0f, smallRect);
//				break;
//		}
//		uniRect(smallRect, bigRect);
//		invalidate(bigRect[0], bigRect[1], bigRect[2], bigRect[3]);
//		return true;
//	}
//
//	/** 清空画布 */
//	public void clear() {
//		if (mBitmap != null)
//		{
//			mBitmap.eraseColor(mBackgroundColor);
//		}
//		if (bigRect != null){
//			bigRect[0] = mWidth;
//			bigRect[1] = mHeight;
//			bigRect[2]= bigRect[3] = 0;
//		}
//	}
//
//	/** 关闭视图 */
//	public void close() {
//		if (mBitmap != null && !mBitmap.isRecycled())
//		{
//			mBitmap.recycle();
//		}
//		mBitmap = null;
//	}

	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		if (mWidth > 1 && mHeight > 1) {
//			setMeasuredDimension(mWidth, mHeight);
//		} else {
//			mWidth = MeasureSpec.getSize(widthMeasureSpec);
//			mHeight = MeasureSpec.getSize(heightMeasureSpec);
//			setMeasuredDimension(mWidth, mHeight);
//		}
//		return;
//	}
//
//	@Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//    }

}
