package com.hanvon.penenginejni;

import android.graphics.Bitmap;

public class HWPenEngine {
	
	
    static {
        try {
            System.loadLibrary( "hw_PenEngine" );
        } catch ( UnsatisfiedLinkError e ) {
            e.printStackTrace( );
        } catch ( Exception e ) {
            e.printStackTrace( );//
        }
    }
    static int[] bitmapPixels;
    public static void setBristlDabImage(long engine,Bitmap bitmap)
    {
    	if (null!=bitmap&&0!=engine) {
          
          bitmapPixels = new int[bitmap.getWidth() * bitmap.getHeight()];

          bitmap.getPixels(bitmapPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
          
          setBristlDabImage(engine, bitmap.getWidth(), bitmap.getHeight(),bitmapPixels);
		}
    }    
	
//	public native static long initialize_ori(int width,int height,int[] pDrawMemory);
    public native static long initialize(int width,int height,int[] pDrawMemory);
	
	public native static void setColor(long engine,byte b,byte g,byte r,byte a);
	public native static void setPenSize(long engine,float size);
	public native static void setFadeStep(long engine,float fadeStep);
	public native static float getSize(long engine);
	public native static void fillSurface(long engine,int color);
	public native static void resetScan(long engine, int[] scan, int len);
	//融合
	public native static void fusionDataToDisplay(long engine, int[] pstData, int data_len, boolean isCorrectPen);
	public native static void setPenStyle(long engine,int brushType);
	private native static void setBristlDabImage(long engine,int width,int height,int[] bitmapMemory);			//ʲô����
	public native static void beginStroke(long engine);
	public native static void strokePoint(long engine,float x,float y,float p,int[] rect);
	public native static int strokePointStoreScreen(long engine,float x, float y, float p, int[] rect); // д�ʼ��洢,����
	public native static void endStroke(long engine,int[] rect);
	public native static void getPointAndPixel(long engine, int[] positionPixels);
	public native static int getPointNum(long engine);
	public native static boolean destroyEngine(long engine);
	public native static int strokePointBackOperationScreen(long engine, int[] rect);
	public native static int strokePointRestoreOperationScreen(long engine, int[] rect);
	public native static int strokePointWipeByLineScreen(long engine, double[] pData,int len, int[] rect);
	public native static int strokePointWipeBySelectPoint(long engine, float x, float y, int[] rect);
	public native static int createMemoryResource(int count);
	public native static int setMaxPointsPerSubScreen(int num);
	public native static int destroyMemoryResource();
	public native static boolean drawOneStrokeData(long engine, float[] pData,int len, int[] rect);
	public native static boolean drawOneStrokeDataScreen(long engine, double[] pData,int len, int[] rect);
	public native static int getValidNodeIdx(int[]pIdx, int len);

	public native static int saveStrokeData_float(double[] pData, int[] pLen );	

	public native static int loadStrokeData_float(long engine, int[] pRect, double[] pData, int iDataLen);

	public native static int getCanBackOperationStep();
	public native static int getCanRestoreOperationStep();
	
	public native static int cleanAllScreen(long engine, int[] rect);


	
}
