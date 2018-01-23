package com.hanvon.handwriting.landscaping;


import android.graphics.Bitmap;
import android.util.Log;

public class HwColorPen {
	
	//PenStyle
	public static final int PS_PENCIL = 1;
	public static final int PS_FOUNTAINPEN = 2;
	public static final int PS_BRUSHPEN = 3;
	//PenColor
	public static final int  PC_BLUE=0;
	public static final int  PC_GREEN=1;
	public static final int  PC_CYAN=2;
	public static final int  PC_RED=3;
	public static final int  PC_PINK=4;
	public static final int  PC_YELLOW=5;
	public static final int  PC_BLACK=6;
	public static final int  PC_DARKBLUE=7;
	public static final int  PC_OLIVE=8;
	public static final int  PC_LIGHTBLUE=9;
	public static final int  PC_MAROON=10;
	public static final int  PC_PURPLE=11;
	public static final int  PC_DARKYELLOW=12;
	public static final int  PC_SILVERGRAY=13;
	public static final int  PC_DARKGRAY=14;

	
	static HwColorPen   m_ColorPen;
	private static String TAG = "HwcolorPen";
	
	
	public HwColorPen()
	{
		
	}
	
	public static HwColorPen getInstance(Bitmap bmp)
	{
		Log.i(TAG, "getInstance begin");
		if(bmp == null)
		{
			if(m_ColorPen!=null)
				m_ColorPen.setBmp(null);
			m_ColorPen = null;
			return null;
		}
		
		if(m_ColorPen == null)
		{
			m_ColorPen = new HwColorPen();
			
			m_ColorPen.setBmp(bmp);
		}
		else
		{
			m_ColorPen.setBmp(bmp);
			Log.i(TAG , "getInstance setbmp");
		}
		return m_ColorPen;
	}
	
	
	public int setBmp(Bitmap bmp)
	{
		return nativeHWPEN_initialize(bmp);
	}
	public void releaseBmp()
	{
		setBmp(null);
	}
	
	public void setPenInfo(int nAntiLevel,int nPenStyle,int nColorType,int nPenWidth,int nColorRate)
	{
		nativeHWPEN_SetPen(nAntiLevel,nPenStyle,nColorType,nPenWidth,nColorRate);
	}
	
	public void drawLine(int x,int y,int nPenAlpha,int[] rect)
	{
		nativeHWPEN_DrawLine(x,y,nPenAlpha,rect);
	}

	public void fadeBmp(int nStep,int left,int top,int right,int bottom)
	{
		nativeHWPEN_Fade(nStep,left,top,right,bottom);
	}
	
	private static native int nativeHWPEN_initialize(Bitmap bmp);
	private static native void nativeHWPEN_SetPen(int nAntiLevel,int nPenStyle,int nColorType,int nPenWidth,int nColorRate);
	private static native void nativeHWPEN_DrawLine(int x,int y,int nPenAlpha,int [] pRect);
	private static native void nativeHWPEN_Fade(int nStep,int left,int top,int right,int bottom);


	
	
	static {
		 System.loadLibrary("HwColorPen");
	}
}
