package com.hanvon.canvasdemo.core;

import android.graphics.Bitmap;

public class HWPenEngine {

	static {
		try {
			System.loadLibrary( "hw_PenEngine" );
		} catch ( UnsatisfiedLinkError e ) {
			e.printStackTrace( );
		} catch ( Exception e ) {
			e.printStackTrace( );
		}
	}
	static int[] bitmapPixels;
	public static void setBristlDabImage(long engine,Bitmap bitmap)												//干什么的？没有找到引用
	{
		if (null!=bitmap&&0!=engine) {

			bitmapPixels = new int[bitmap.getWidth() * bitmap.getHeight()];

			bitmap.getPixels(bitmapPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

			setBristlDabImage(engine, bitmap.getWidth(), bitmap.getHeight(),bitmapPixels);
		}
	}

	//初始化引擎 宽高，内存 需要类中保存handle值
	//public native static long initialize(int width,int height,int[] pDrawMemory);
	/**
	 * 彩笔核心初始化
	 * @param width			画布宽度
	 * @param height		画布高度
	 * @param pDrawMemory	画布的内存
	 * @param pathString	笔迹保存的路径名
	 * @param pathStringLen	笔迹保存的路径名长度
	 * @return
	 */
	public native static long initialize(int width,int height,int[] pDrawMemory);

	//设置颜色
	public native static void setColor(long engine,byte b,byte g,byte r,byte a);

	//设置笔型粗细
	public native static void setPenSize(long engine,float size);

	//获取笔型粗细
	public native static float getSize(long engine);

	//填充canvas
	public native static void fillSurface(long engine,int color);									//还用吗？

	//切换笔橡皮  brushType 默认为 fletpen;
	public native static void setPenStyle(long engine,int brushType);

	//设置毛笔笔头
	private native static void setBristlDabImage(long engine,int width,int height,int[] bitmapMemory);			//暂时没用

	//起笔 mouseDown penDown
	public native static void beginStroke(long engine);

	//运笔 mouseMove penMove
	public native static void strokePoint(long engine,float x,float y,float p,int[] rect);
	//nFlag: 0 - 写笔迹存储; 1 - 删除笔迹
//	public native static int strokePointWipe(long engine,float x, float y, float p, int[] rect, int delFlag);
	// 写笔迹存储
	//public native static int strokePointStore(long engine,float x, float y, float p, int[] rect);
	public native static int strokePointStoreScreen(long engine,float x, float y, float p, int[] rect); // 写笔迹存储,分屏


	//抬笔 mouseUp penUp
	public native static void endStroke(long engine,int[] rect);

	//销毁引擎
	public native static boolean destroyEngine(long engine);

	//public native static int strokePointBackOperation(long engine, int[] rect);//撤销功能,可多次撤销;撤销操作时出栈入恢复栈;出栈个数由operatorNum决定，每次操作的次数
	public native static int strokePointBackOperationScreen(long engine, int[] rect);
	//public native static int strokePointRestoreOperation(long engine, int[] rect);//恢复功能,可多次恢复;恢复操作时出栈入撤销栈;出栈个数由operatorNum决定，每次操作的次数
	public native static int strokePointRestoreOperationScreen(long engine, int[] rect);//恢复功能,可多次恢复;恢复操作时出栈入撤销栈;出栈个数由operatorNum决定，每次操作的次数
	//public native static int strokePointWipeByLine(long engine, float[] pData,int len, int[] rect);//根据上层传来的数据解析数据,画一笔;删除与该曲线相交的笔画,输出更新区域
	public native static int strokePointWipeByLineScreen(long engine, float[] pData,int len, int[] rect);//根据上层传来的数据解析数据,画一笔;删除与该曲线相交的笔画,输出更新区域
	//public native static int strokePointWipeByLine_line(long engine, float[] pData,int len, int pointstep, int[] rect);//根据上层传来的数据解析数据,画一笔;删除与该曲线相交的笔画,输出更新区域
	////回删一笔;可多次回删
	//public native static int strokePointWipeBack(long engine, int[] rect);
	//删除指定点的笔画;可多次删
	public native static int strokePointWipeBySelectPoint(long engine, float x, float y, int[] rect);

	//public native static int restoreDeletedStroke(long engine, int[] rect);//回退删除的笔;可多次回退//撤销删除的笔;可多次撤销

	public native static int createMemoryResource(int count);// 分配撤销恢复的栈空间大小
	public native static int setMaxPointsPerSubScreen(int num);// 设置每个子屏的点数最大值
	public native static int destroyMemoryResource();//释放所有空间，包括stack和THMemManager(不包括engine)//close window 时调用
	public native static boolean drawOneStrokeData(long engine, float[] pData,int len, int[] rect);//根据上层传来的数据解析数据,画一笔;返回true or false 代表绘制完成
	public native static boolean drawOneStrokeDataScreen(long engine, float[] pData,int len, int[] rect);
	public native static int getValidNodeIdx(int[]pIdx, int len);//保存文件时返回给上层的当前有效的节点编号和长度;
	//public native static int calMemoryNeed();//calculate the memory need for save data,单位Byte
	//public native static int saveStrokeData(int[] pLen);//out to pData and length, 空间由外部申请好调用,//返回实际存放的点和个数pLen(数组长度);执行成功或失败
	//public native static int loadStrokeDate(long engine);//此处engine应初始化过,load stroke data,返回实际读取的数据个数,应该==len - 2(除过所有笔结束符(-1,-1))

	//add WipeBackStep and RestoreStep interface
	public native static int getCanBackOperationStep();//查询当前可以撤销的步数
	public native static int getCanRestoreOperationStep();//查询当前可以恢复的步数

	public native static int cleanAllScreen(long engine, int[] rect);//清屏;all screen ,include subscreen

	/*//用于中断保存时获取笔画标志位,输入：存放结果的数组和数组长度,输出：笔画显示标记(1代表显示;0不显示),	返回值：数组数据有效个数(笔画个数)
	public native static int getStrokeFlag(int[] pFlag, int len);

	//返回数据长度；核心内部写的输出数据应为逆序，即从栈底开始读，读到栈顶结束传递数组到上层；解析时从栈底开始赋值即可；
	public native static int getStackInfo(int[] pInfo, int len);

	//输入：存放栈数据的数组和数组长度，栈数据(核心内部解析)：返回读取数据个数
	public native static int setStackInfo(int[] pInfo, int iInfoLen);

	//输入：engine引擎、更新区域、数据(包含strokeFlag\penAttribute\xyp)和数据长度;	输出：更新区域矩形框; 返回值：成功或失败: 1代表加载成功
	public native static int loadStrokeDataToCanvas(long engine, int[] pRect, float[] pData, int len);*/


}
