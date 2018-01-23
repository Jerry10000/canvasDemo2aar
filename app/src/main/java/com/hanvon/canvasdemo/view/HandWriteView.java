package com.hanvon.canvasdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HandWriteView extends View
{

	/* 位图 */
	public Bitmap mBitmap;
	/** 当前笔迹所占矩阵 */
	public int[]  mnRect = new int[4];
	/** 所有笔迹所占的大矩阵*/
	public int[]  mnTraceRect=new int[4];
	private Bitmap handwritingBitmap = null;
	//	private Bitmap logoBmp = null;
	private float clickX = 0,clickY = 0;
	private int width;
	private int height;

	int logoWidth;
	int logoHeight;
	Rect bmprect = new Rect();

	public HandWriteView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

	}


	public void clear(){
//		handwritingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		setBackgroundColor(0);
//		hwColorPen = HwColorPen.getInstance(handwritingBitmap);
//		invalidate();
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawBitmap(handwritingBitmap, 0, 0,null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		clickX = event.getX();
		clickY = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				break;
			case MotionEvent.ACTION_MOVE:

				break;
			case MotionEvent.ACTION_UP:

				break;
		}
		return true;
	}



	/**更新矩阵范围，参数一为新增小矩阵，参数二为合并后的大矩阵*/
	private void uniRect(int [] rect,int [] rectall)
	{
		if(rect[0]<rectall[0])
			rectall[0] = rect[0];
		if(rect[1]<rectall[1])
			rectall[1] = rect[1];
		if(rect[2]>rectall[2])
			rectall[2] = rect[2];
		if(rect[3]>rectall[3])
			rectall[3] = rect[3];
	}



}
