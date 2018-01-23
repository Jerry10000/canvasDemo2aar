package com.hanvon.canvasdemo.constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

/** 
 * <b>获取屏幕信息</b>
 * <p>如使用该数据，需首先运行getWindowInfo(Context)
 */
public class CallaViewInfo { 
	private static final String TAG = "CallaViewInfo";
	
	public static View candView = null;
	
	/** 屏幕宽 */
	public static int width = 320;
	/** 屏幕高 */
	public static int height = 480;
	/** 宽高较小者 */
	public static int minSize = 320;

	/** 按钮大小 */
	public static int buttonWidth = 40;
	public static int buttonHeight = 40;
	
	public static int status_bar_height = 40;
	
	/** 字体大小 */
	public static int textSize = 48;
	
	/** 屏幕方向 */
	public static int orientation = -1;
	//private static int systemOri = -1;
	
	//public static Display display;
	
	public static int padding = 10;
	
	public static int wordHeight;
	public static void bakCandView(View v)
	{
		candView = v;
	}
	public static View getCandView()
	{
		return candView;
	}
	
	/** 获取屏幕信息 */
	public static void getWindowInfo(Context c){
		/*if (display != null && systemOri == display.getOrientation()) {
			return;
		}
		
		display = ((WindowManager)c
				.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();*/

		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		
		width = /*display.getWidth()*/dm.widthPixels;
		height = /*display.getHeight()*/dm.heightPixels;
		orientation = height >= width ? 0 : 1;//display.getOrientation();DELL 360x600有问题
		/*systemOri = dm.;*/
		minSize = Math.min(width, height);
	
		if (orientation == 0) {	// 竖屏
			buttonWidth = width / 8;
			buttonHeight = height / 11;
			wordHeight = height / 11;
		} else {
			buttonWidth = width / 12;
			buttonHeight = height / 8;	//
			wordHeight = buttonHeight;
		}
		
		textSize = buttonHeight >> 1;
		padding = Math.max(width, height) / 40;
		
		status_bar_height = c.getResources().getSystem().getDimensionPixelSize(
				c.getResources().getSystem().getIdentifier("status_bar_height", "dimen", "android"));
		
//		if (DemoData.debug_version) {
//			Log.d(TAG, "Display:(width=" + width + "; height=" + height + ";orientation="
//					+ orientation + ";button width=" + buttonWidth);
//		}
	}
	
	private static final int COLOR_GAP = 5;
	private static final int HEIGHT = 40;
	private static final int ROUND_RADIUS = 5;
	/** 
	 * 绘制笔迹（颜色，粗细）示例
	 * @param type  类别： >=0：颜色；<0：粗细
	 * @param data  额外信息：颜色或者粗细索引
	 * @return Drawable 
	 */
	public static Drawable drawSample(Context c, final int type, final int data){
		getWindowInfo(c);

		final RectF rect = new RectF(0, 0, width>>1, HEIGHT);
		//HWColorPaint paint = new HWColorPaint();
		Paint paint = new  Paint();
		paint.setStyle(Style.FILL);
		
		Bitmap bitmap = Bitmap.createBitmap(width>>1, HEIGHT, Bitmap.Config.RGB_565);
        bitmap.eraseColor(0xffdfe7f2);
        Canvas canvas= new Canvas(bitmap);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true); 

        rect.top = 0;
        rect.bottom = HEIGHT;
        canvas.drawRoundRect(rect, ROUND_RADIUS, ROUND_RADIUS, paint);
        
        rect.left += COLOR_GAP;
        rect.right -= COLOR_GAP;
        if (type >= 0) {
            rect.top += COLOR_GAP;
            rect.bottom -= COLOR_GAP;
        	//paint.init(0, 1, data, 30);
           // paint.drawRectF(rect, canvas);
            canvas.drawRect(rect, paint);
        	
        } else {
        	rect.top = (((int)rect.height() - 2)>>1) - data;
        	rect.bottom = rect.top + (data<<1) + 2;
        	paint.setColor(Color.BLACK);
        	canvas.drawRect(rect, paint);
        }
        
		return new BitmapDrawable(bitmap);
	}
}
