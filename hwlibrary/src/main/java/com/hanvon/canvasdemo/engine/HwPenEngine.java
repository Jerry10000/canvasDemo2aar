package com.hanvon.canvasdemo.engine;

import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hanvon.canvasdemo.Utils.IOUtils;
import com.hanvon.canvasdemo.Utils.LogUtil;
import com.hanvon.canvasdemo.beans.Stack_stroke;
import com.hanvon.canvasdemo.beans.Stroke1;
import com.hanvon.penenginejni.HWPenEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by pc on 2017/11/23.
 */

public class HwPenEngine {
    private static final String TAG = "HwPenEngine";
    private static final boolean DEBUG = false;

//    private static final int MSG_BLEND = 0;


    private static final int ERROR_TYPE_NOT_INIT = -1;

    public static final int PEN_TYPE_FOUNTAIN1 = 0;
    public static final int PEN_TYPE_FOUNTAIN2 = 1;
    public static final int PEN_TYPE_PAINTBRUSH = 2;
    public static final int PEN_TYPE_PENCIL = 3;
    public static final int PEN_TYPE_MARKER = 4;
    public static final int PEN_TYPE_INK = 6;
    public static final int PEN_TYPE_CORRECTION = 7;
    public static final int PEN_TYPE_ERASER_FOR_STROKE = 10;
    public static final int PEN_TYPE_ERASER_FOR_POINT = 11;

    private boolean isEraserMode;
    private int penType = PEN_TYPE_INK;

    private long engine = -1;
    private boolean isDrawing = false;


    //每笔所有像素点索引和颜色集合
    private int[] positionPixels;
    //每笔的最大矩形框
    private Rect rect_big;
    //删除笔画所覆盖的矩形
    private Rect rect_delete;
    //栈元素
    private Stack_stroke stack_element;
    //所有笔画的索引-像素列表， 根据堆栈中记录的笔画的显示与否去这里取，然后融合。
    private List<Stroke1> strokes;
    private Stroke1 currentStroke;
    //用于整笔删除时存储要删除的笔画
    private List<Integer> deleteStrokeIDs;

    //撤销栈（撤销时进行抛栈）：绘画、擦除、恢复时进行压栈
    private Stack undoStack;
    //恢复栈（恢复时进行抛栈）：执行撤销时进行压栈，
    private Stack redoStack;
    //栈的深度
    private int stackSize = 50;

    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);

    private String savePath = "/mnt/sdcard/wwl/";
    private String suffix = ".st";

    private int surfaceWidth;
    private int surfaceHeight;
    private int[] drawMemory;
    private int[] bg;
    private float lastX = -1;
    private float lastY = -1;

    private Handler mainHandler;

//    private boolean isSaving = false;

    /**
     * 构造函数
     */
    public HwPenEngine() {
        isEraserMode = false;
//        rect_big = new Rect(0, 0, 0, 0);
        strokes = new LinkedList<Stroke1>();
        currentStroke = new Stroke1();
        undoStack = new Stack();
        redoStack = new Stack();
    }

    /********************************************** C 接口 *********************************/

    public void beginStroke_C(long engine){
        HWPenEngine.beginStroke(engine);
    }

    public void strokePoint_C(long engine, float x, float y, float p, int[] rect){
        HWPenEngine.strokePoint(engine, x, y, p, rect);
    }

    public void endStroke_C(long engine, int[] rect){
        HWPenEngine.endStroke(engine, rect);
    }

    public void fillSurface_C(long engine, int color){
        HWPenEngine.fillSurface(engine,color);
    }

    public void setPenStyle_C(long engine, int penType){
        HWPenEngine.setPenStyle(engine, penType);
    }

    public void setPenSize(long engine, int penWidth){

        HWPenEngine.setPenSize(engine, penWidth);
    }

    public void setPenColor(long engine, byte b, byte g, byte r, byte a){
        HWPenEngine.setColor(engine, b, g, r, a);
    }

    public long init_C(int surfaceWidth,int surfaceHeight, int[] drawMemory){
        return HWPenEngine.initialize(surfaceWidth, surfaceHeight, drawMemory);
    }

    public void destory_C(long engine){
        HWPenEngine.destroyEngine(engine);
    }

    /**************************************************************************************/

    /**
     * 初始化
     * @param width    画布宽度
     * @param height    画布高度
     * @param drawMemory    画布内存
     */
    public void init(int width, int height, int[] drawMemory){
        this.surfaceWidth = width;
        this.surfaceHeight = height;
        this.drawMemory = drawMemory;
        engine = HWPenEngine.initialize(width, height, drawMemory);
        rect_delete = new Rect(0,0,0,0);
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
//        mHandler = new Handler(handlerThread.getLooper()){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what){
//                    case MSG_BLEND:
//
////                        HWPenEngine.fillSurface(engine,0x00ffffff);
////                        if (bg != null && bg.length > 0){
////                            HWPenEngine.resetScan(engine, bg, bg.length);
////                        }
////                        blendStrokesToScreen();
//
//                        Message message = Message.obtain();
//                        message.what = 1;
//                        message.obj = rect_delete;
////                        isUpdating = true;
//                        mainHandler.sendMessage(message);
//
//                        break;
//                }
//            }
//        };
    }

    /**
     * 将核心中的内存区重置为某个颜色
     * @param color 将要重置的颜色
     */
    public void fillSurface(int color){
        HWPenEngine.fillSurface(engine,color);
    }

    /**
     * 刷新屏幕
     */
    public void getNewScreen(){
        HWPenEngine.fillSurface(engine,0x00ffffff);
        if (bg != null && bg.length > 0){
            HWPenEngine.resetScan(engine, bg, bg.length);
        }
        blendStrokesToScreen();
    }

    /**
     * 设置UI线程
     * @param handler UI线程
     */
    public void setHandler(Handler handler){
        mainHandler = handler;
    }

    /**
     * 设置文件的保存路径和后缀名，保证每个文件的保存路径都是唯一的
     * @param path 笔迹的保存路径，以“/”结束
     * @param suf 后缀名，必须以“.”开始
     */
    public void setSavePath(String path, String suf){
        savePath = path;
        suffix = suf;
    }

    /**
     *
     * @param antiLevel     反走样级别，暂时没用
     * @param penType       笔型
     * @param color         颜色
     * @param penWidth      笔宽
     * @param nColorRate    颜色变化速率，暂时没用
     * @return  错误返回值
     */
    public int setPenInfo(int antiLevel,int penType, int color, int penWidth, int nColorRate){
        if (engine == -1){
            LogUtil.e(TAG, "彩笔核心没有初始化！");
            return ERROR_TYPE_NOT_INIT;
        }
        isEraserMode = false;
        this.penType = penType;
        int a = (color & 0xff000000)>>24;
        int r = (color & 0x00ff0000)>>16;
        int g = (color & 0x0000ff00)>>8;
        int b = color & 0x000000ff;

        //根据type进行判断，设置isTransParent属性
        switch (penType){
            case PEN_TYPE_FOUNTAIN1:
                HWPenEngine.setPenStyle(engine, 20);
                break;
            case PEN_TYPE_FOUNTAIN2:
                HWPenEngine.setPenStyle(engine, 22);
                break;
            case PEN_TYPE_PAINTBRUSH:
                HWPenEngine.setPenStyle(engine, 3);
                break;
            case PEN_TYPE_PENCIL:
                HWPenEngine.setPenStyle(engine, 1);
                break;
            case PEN_TYPE_MARKER:
                HWPenEngine.setPenStyle(engine, 15);
                break;
            case PEN_TYPE_INK:
                HWPenEngine.setPenStyle(engine, 6);//毛笔
                break;
            case PEN_TYPE_CORRECTION:
                HWPenEngine.setPenStyle(engine, 21);
                HWPenEngine.setFadeStep(engine, 2f);
                break;


            default:
                LogUtil.e(TAG, "pen type is wrong!");
        }
        HWPenEngine.setColor(engine, (byte)b, (byte)g, (byte)r, (byte)a);
        HWPenEngine.setPenSize(engine, penWidth);
        return 0;
    }


//    public void setPenType(int type){
//
//    }
//
//    public void setPenColor(int color){
//        isEraserMode = false;
//        //对Alpha进行判断，设置isTransParent属性
//    }
//
//    public void setPenWidth(int width){
//        isEraserMode = false;
//    }

    /**
     * 设置橡皮擦类型
     * @param type 类型
     *           PEN_TYPE_ERASER_FOR_STROKE  整笔擦除
     *           PEN_TYPE_ERASER_FOR_POINT   精细擦除
     */
    public void setEraserType(int type, int penWidth){
        isEraserMode = true;
        penType = type;
        switch (type){
            case PEN_TYPE_ERASER_FOR_STROKE:

                break;
            case PEN_TYPE_ERASER_FOR_POINT:
                HWPenEngine.setPenStyle(engine, 11);
                HWPenEngine.setColor(engine, (byte)255, (byte)255, (byte)255, (byte)0);
                HWPenEngine.setPenSize(engine, penWidth);
                break;
            default:
                LogUtil.e(TAG, "eraser type is wrong!");
        }
    }

    /**
     * 获取当前笔刷或者项目类型
     * @return 类型
     */
    public int getPenOrEraserType(){
        return penType;
    }

    /**
     * 开始绘画，落笔时调用
     * @return
     */
    public int beginStroke(){
        if (engine == -1){
            LogUtil.e(TAG, "彩笔核心没有初始化！");
            return ERROR_TYPE_NOT_INIT;
        }
        rect_big = new Rect(0, 0, 0, 0);
        rect_delete.set(0,0,0,0);
        if (penType != PEN_TYPE_ERASER_FOR_STROKE){
            HWPenEngine.beginStroke(engine);
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    //修改当前笔的信息
                    currentStroke = new Stroke1();
                    currentStroke.setStrokeID(strokes.size());
                    currentStroke.setRects(new LinkedList<Rect>());
                    currentStroke.setVisible(true);
                    currentStroke.setPenType(penType);
                }
            });
        }else{
            deleteStrokeIDs = new LinkedList<Integer>();
            //重置为判定值
            lastX = -1;
            lastY = -1;
        }
        return 0;
    }

    /**
     * 绘画， move时调用
     * @param x 采样点的X坐标
     * @param y 采样点的Y坐标
     * @param p 采样点的压力值
     * @param rect 内存改变的最小矩形，用于刷新
     * @return
     */
    public int strokePoint(final float x, final float y, float p, final int[] rect){
        if (engine == -1){
            LogUtil.e(TAG, "彩笔核心没有初始化！");
            return ERROR_TYPE_NOT_INIT;
        }
        try{
            if (penType != PEN_TYPE_ERASER_FOR_STROKE){
                HWPenEngine.strokePoint(engine, x, y, p, rect);

                //修改当前笔的信息
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Rect rect1 = new Rect(rect[0],rect[1],rect[2],rect[3]);
                        synchronized (currentStroke){
                            if (currentStroke == null){
                                currentStroke = new Stroke1();
                                currentStroke.setStrokeID(strokes.size());
                                currentStroke.setRects(new LinkedList<Rect>());
                                currentStroke.setVisible(true);
                                currentStroke.setPenType(penType);
                            }
                        }
                        if (currentStroke.getRects() != null){
                            currentStroke.getRects().add(rect1);
                        }

                        rect_big.union(rect1);
                    }
                });
            }else{

                //处理整笔删除的逻辑
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (lastX == -1 && lastY == -1){//刚开始画，此时只记录上点位置
                            lastX = x;
                            lastY = y;
                        }else{

                            Rect rect_eraser = creatRect(lastX, lastY, x, y);
                            for (final Stroke1 stroke : strokes){
                                if (!stroke.isVisible()){
                                    continue;
                                }
                                if (isOverlap(stroke.getRect_big(), rect_eraser)){
                                    LogUtil.i(TAG, "和大矩形相交");
                                    for (Rect rec : stroke.getRects()){
                                        if (isOverlap(rec, rect_eraser)) {
                                            showStrokesVisibleState();
                                            LogUtil.i(TAG, "和小矩形相交");
                                            //删除当前笔（修改strokes中笔画状态、修改栈信息、更新显示）
                                            mainHandler.removeMessages(1);
                                            deleteStroke(stroke.getStrokeID());
                                            LogUtil.e(TAG, "删除笔画的ID = " + stroke.getStrokeID());
                                            break;
                                        }
                                    }
                                }
                            }
                            lastX = x;
                            lastY = y;
                        }
                    }
                });
            }
        }catch (Exception e){

        }
        return 0;
    }

    /**
     * 结束绘画
     * @param rect 内存改变的最小矩形，用于刷新
     * @return
     */
    public int endStroke(final int[] rect){
        if (engine == -1){
            LogUtil.e(TAG, "彩笔核心没有初始化！");
            return ERROR_TYPE_NOT_INIT;
        }
        if (penType != PEN_TYPE_ERASER_FOR_STROKE){
            HWPenEngine.endStroke(engine, rect);
            //根据点的长度申请空间
            positionPixels = new int[HWPenEngine.getPointNum(engine)*2];
            //获取点信息
            HWPenEngine.getPointAndPixel(engine, positionPixels);

            final Rect rect1 = new Rect(rect[0],rect[1],rect[2],rect[3]);
            rect_big.union(rect1);
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    //修改当前笔的信息
                    currentStroke.setPointPixels(positionPixels);
                    currentStroke.setRect_big(rect_big);
                    currentStroke.getRects().add(rect1);

                    //将当前笔添加到笔画总列表中
                    strokes.add(currentStroke);

                    //修改栈状态
//                List<Integer> strokeIDs;
//                if (stack_element.getStrokeIDs() == null){
//                    strokeIDs = new LinkedList<Integer>();
//                }else{
//                    strokeIDs = stack_element.getStrokeIDs();
//                }
                    List<Integer> strokeIDs = new LinkedList<Integer>();
                    strokeIDs.add(currentStroke.getStrokeID());
                    stack_element = new Stack_stroke();
                    stack_element.setStrokeIDs(strokeIDs);
                    stack_element.setVisible(false);

                    if (undoStack.size() >= stackSize){
                        undoStack.remove(0);
                    }
                    undoStack.push(stack_element);
                    redoStack.clear();

                }
            });
        }else{
            //处理整笔删除的逻辑


            //判断deleteStrokeIDs是否为空，如果不为空，则进行压栈
            if (deleteStrokeIDs.size() > 0){
                stack_element = new Stack_stroke();
                stack_element.setStrokeIDs(deleteStrokeIDs);
                stack_element.setVisible(true);

                if (undoStack.size() >= stackSize){
                    undoStack.remove(0);
                }
                undoStack.push(stack_element);
                redoStack.clear();
            }
            //重置为判定值
            lastX = -1;
            lastY = -1;

        }
        showStrokesVisibleState();
        return 0;
    }

    public int drawPoint(float x, float y){
        if (engine == -1){
            LogUtil.e(TAG, "彩笔核心没有初始化！");
            return ERROR_TYPE_NOT_INIT;
        }
//        HWPenEngine.hwPen_DrawLine((int) x,(int)y,int[] pRect,int nFlag,int nPenAlpha, int[] pUpdateRect);
        return 0;
    }

    public int drawPoints(String pixlesInfo){
        if (engine == -1){
            LogUtil.e(TAG, "彩笔核心没有初始化！");
            return ERROR_TYPE_NOT_INIT;
        }
        return 0;
    }

    /**
     * 保存
     * @throws IOException
     */
    public void save() throws IOException {
        //清空路径下的所有文件
        IOUtils.clearFiles(savePath);

//        //将所有笔画存到一个文件
//        saveStrokesAsJson(strokes, savePath, "0" + suffix);
        //按照每一笔存储一个文件，方便多线程读取
        saveStrokeAsJson(strokes, savePath);


//        //保存整个内存区的方式，此方式优点是快，缺点是加载后不能再次编辑了
//        IOUtils.writeIntArrayToFile(drawMemory, savePath, "strokes" + suffix);

        //清空strokes对象、栈
        strokes = new LinkedList<Stroke1>();
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * 加载
     * @throws IOException
     */
    public void load() throws IOException {
//        loadStrokesFromJson(savePath+"0" + suffix);
        loadStrokeFromJson(savePath);
//        loadStrokeFromJsonAsync(savePath);


//        //保存整个内存区的方式
//        IOUtils.readIntArrayFromFile(drawMemory, savePath+"strokes" + suffix);
//        bg = drawMemory.clone();
//        engine = HWPenEngine.initialize(surfaceWidth, surfaceHeight, drawMemory);


        //清空路径下的所有文件
//        IOUtils.clearFiles(savePath);
        blendStrokesToScreen();
    }

    /**
     * 清屏
     */
    public void clear(){
        //清空核心中的内存区
        HWPenEngine.fillSurface(engine,0x00ffffff);
        if(strokes == null || strokes.size() == 0)
            return;
        //修改栈状态
        List<Integer> strokeIDs = new LinkedList<Integer>();
        for (int i = 0; i < strokes.size(); i++){
            if (strokes.get(i).isVisible()){
                strokeIDs.add(strokes.get(i).getStrokeID());
            }
        }
        stack_element = new Stack_stroke();
        stack_element.setStrokeIDs(strokeIDs);
        stack_element.setVisible(true);
        undoStack.push(stack_element);
        redoStack.clear();

        //修改总笔画对象的显示状态
        for (int i = 0; i < strokes.size(); i++){
            strokes.get(i).setVisible(false);
        }
        showStrokesVisibleState();
        return;
    }


    /**
     * 撤销
     */
    public void undo(){
//        long time0 = System.currentTimeMillis();

        //清空核心中的内存区
        HWPenEngine.fillSurface(engine,0x00ffffff);
        if (bg != null && bg.length > 0){
            HWPenEngine.resetScan(engine, bg, bg.length);
        }

        //改变栈信息
        if (!changeStackState(undoStack, redoStack))return;
//        long time1 = System.currentTimeMillis();
//        LogUtil.e(TAG, "undo: cast1 = " + (time1 - time0) + "ms");
        //将显示的笔画融合到屏幕上
        blendStrokesToScreen();
//        LogUtil.e(TAG, "undo: cast2 = " + (System.currentTimeMillis() - time1) + "ms");

//        mainHandler.removeMessages(1);
//        mHandler.removeMessages(MSG_BLEND);
//        if (!changeStackState(undoStack, redoStack))return;
//        mHandler.sendEmptyMessageDelayed(MSG_BLEND, 50);


    }

    /**
     * 恢复
     */
    public void redo(){
        //清空核心中的内存区
        HWPenEngine.fillSurface(engine,0x00ffffff);
        if (bg != null && bg.length > 0){
            HWPenEngine.resetScan(engine, bg, bg.length);
        }
        //改变栈信息
        if (!changeStackState(redoStack, undoStack)) return;
        //将显示的笔画融合到屏幕上
        blendStrokesToScreen();
    }

    /**
     * 获取可撤销的步数
     * @return
     */
    public int getUndoSteps(){
        if (undoStack == null)return 0;
        return undoStack.size();
    }

    /**
     * 获取可恢复的步数
     * @return
     */
    public int getRedoSteps(){
        if (redoStack == null)return 0;
        return redoStack.size();
    }

    /**
     * 销毁引擎
     */
    public void destroy(){
        if (engine != -1){
            HWPenEngine.destroyEngine(engine);
        }
    }


    /**
     * 将所有笔画按照json格式存到一个文件中
     * @param data 所有笔画对象
     * @param path  存储路径
     * @param fileName  存储文件名
     */
    private void saveStrokesAsJson(Object data, String path, String fileName){
        Gson gson = new Gson();
        String jsonString = gson.toJson(data);
        IOUtils.writeStringToFile(jsonString, path, fileName);
    }

    /**
     * 将每一笔笔画按照json格式单独存为一个文件
     * @param data 所有笔画对象
     * @param path  存储路径
     */
    private void saveStrokeAsJson(final List<Stroke1> data, final String path){
        final Gson gson = new Gson();
        //将每个笔画提取出来
        for (int i = 0; i < data.size(); i++){
//            //将所有显示的笔画进行保存，不显示的就不做保存了
//            if (data.get(i).isVisible()){
                //异步存储
                final int finalI = i;
                fixedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        long time0 = System.currentTimeMillis();
                        String jsonString = gson.toJson(data.get(finalI));
                        long time1 = System.currentTimeMillis();
                        LogUtil.e(TAG, "转化为json的时间 = " + (time1 - time0) );
//                        LogUtil.e(TAG, "压缩前的长度: " + jsonString.length());
                        byte[] gzJsonByteArray = IOUtils.compress(jsonString, "UTF-8");
                        long time2 = System.currentTimeMillis();
                        LogUtil.e(TAG, "gzip压缩 = " + (time2 - time1) );
//                        String gzJsonString = new String(gzJsonByteArray);
//                        LogUtil.e(TAG, "压缩后的长度: " + gzJsonString.length());
                        jsonString = new String(IOUtils.uncompress(gzJsonByteArray));
                        long time3 = System.currentTimeMillis();
                        LogUtil.e(TAG, "gzip解压时间 = " + (time3 - time2) );
//                        LogUtil.e(TAG, "解压后的长度: " + jsonString.length());
                        IOUtils.writeStringToFile(jsonString, path, finalI + ".st");
                        long time4 = System.currentTimeMillis();
                        LogUtil.e(TAG, "写文件时间 = " + (time4 - time3) );
                        LogUtil.e(TAG, "save: " + finalI);
                    }
                });
//            }
        }
//        /*******************************/
//        fixedThreadPool.shutdown();
//
//        while(isSaving){
//            if(fixedThreadPool.isTerminated()){
//                isSaving = false;
//                break;
//            }
//        }
//
//
//        /******************************/
    }

    /**
     * 将strokes对象存储为一个json文件，将此json文件读取出来并解析到strokes对象
     * @param path
     */
    private void loadStrokesFromJson(String path){
        Gson gson = new Gson();
        File file = new File(path);
        String jsonString = IOUtils.readFileByChars(file.getAbsolutePath());
        Stroke1[] stroke = gson.fromJson(jsonString, Stroke1[].class);
        strokes = new LinkedList<Stroke1>();
        for (int i = 0; i < stroke.length; i++){
            strokes.add(stroke[i]);
        }
    }

    /**
     * 将某一路径下的所有文件按照json格式取出到strokes对象中
     * @param path 文件路径
     */
    private void loadStrokeFromJson(String path) {
        Gson gson = new Gson();
        File root = new File(path);
        File[] files = root.listFiles();
        if(files == null || files.length == 0)
            return;
        Stroke1[] strokeArray = new Stroke1[files.length];
        for (File file : files){
            String fileName = file.getName();
            int index = Integer.parseInt(fileName.substring(0, fileName.length()-3));
//            long timeBegin = System.currentTimeMillis();
            String jsonString = IOUtils.readFileByChars(file.getAbsolutePath());
//            LogUtil.e(TAG, "读文件的时间 = " + (System.currentTimeMillis() - timeBegin));
//            long time1 = System.currentTimeMillis();
            Stroke1 stroke = gson.fromJson(jsonString, Stroke1.class);
//            LogUtil.e(TAG, "生成json的时间为 = " + (System.currentTimeMillis() - time1));
            strokeArray[index] = stroke;
        }
        strokes = new LinkedList<Stroke1>();
        for (int i = 0; i < strokeArray.length; i++){
            strokes.add(strokeArray[i]);
        }

    }
    /**
     * 异步地将某一路径下的所有文件按照json格式取出到strokes对象中
     * @param path 文件路径
     */
    private void loadStrokeFromJsonAsync(String path) {
        final Gson gson = new Gson();
        File root = new File(path);
        final File[] files = root.listFiles();
        if(files == null || files.length == 0)
            return;

        for (int i = 0; i < files.length; i++){
            String fileName = files[i].getName();
            int index = Integer.parseInt(fileName.substring(0, fileName.length()-3));
            final int finalIndex = index;
            final int finalI = i;
//            try {
//                files.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    String jsonString = IOUtils.readFileByChars(files[finalI].getAbsolutePath());
                    LogUtil.e(TAG, "load: " + finalI);
                    Stroke1 stroke = gson.fromJson(jsonString, Stroke1.class);
                    strokes.add(finalIndex, stroke);
                }
            });
        }
    }


    /**
     * 改变撤销栈和恢复栈的状态，并修改总笔画列表的可见状态
     * @param stackForPop   要抛栈的那个栈
     * @param stackForPush  要压栈的那个栈
     * @return  true执行成功   false执行失败，比如抛栈的那个栈为空
     */
    private boolean changeStackState(Stack stackForPop, Stack stackForPush) {
        //非法状态检查
        if (stackForPop == null || stackForPush == null || stackForPop.isEmpty())
            return false;
        //抛栈
        Stack_stroke stack_element = (Stack_stroke) stackForPop.pop();
        if(stack_element == null)
            return false;
        //根据当前抛栈中的笔画显示与否改变总笔画状态
        List<Integer> strokeIds = stack_element.getStrokeIDs();
        if (strokeIds == null || strokeIds.size() == 0)
            return false;
        if (stack_element.isVisible()){
            for (int i = 0; i < strokeIds.size(); i++){
                strokes.get(strokeIds.get(i)).setVisible(true);
            }
        }else{
            for (int i = 0; i < strokeIds.size(); i++){
                strokes.get(strokeIds.get(i)).setVisible(false);
            }
        }
        showStrokesVisibleState();
        //改变栈中笔画的可见状态
        stack_element.setVisible(!stack_element.isVisible());
        //压栈
        stackForPush.push(stack_element);
        return true;
    }

    private void showStrokesVisibleState(){
//        for (int i = 0; i < strokes.size(); i++){
//            LogUtil.e(TAG, "strokes[" + i + "] is " + strokes.get(i).isVisible());
//        }
    }

    /**
     * 将所有可见的笔画融合到屏幕上
     */
    private void blendStrokesToScreen() {
        if(strokes == null || strokes.size() == 0)
            return;
        for (int i = 0; i < strokes.size(); i++){
            currentStroke = strokes.get(i);
            if(currentStroke != null){
                if (currentStroke.isVisible()){
                    //调接口，将currentStroke渲染到屏幕上
                    HWPenEngine.fusionDataToDisplay(engine, currentStroke.getPointPixels(), currentStroke.getPointPixels().length, currentStroke.getPenType()==PEN_TYPE_CORRECTION);
                }
            }
        }
    }

    /**
     * 创建矩形
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private Rect creatRect(float x1, float y1, float x2, float y2){
        int left, top, right, bottom;
        if (x1<x2){
            left = (int) x1;
            right = (int) x2;
        }else{
            left = (int) x2;
            right = (int) x1;
        }
        if(y1<y2){
            top = (int) y1;
            bottom = (int) y2;
        }else{
            top = (int) y2;
            bottom = (int) y1;
        }
        return new Rect(left, top, right, bottom);
    }

    /**
     * 判断两个矩形是否有相交
     * @param rc1
     * @param rc2
     * @return
     */
    private boolean isOverlap(Rect rc1, Rect rc2){
        if (rc1.right  > rc2.left &&
                rc2.right  > rc1.left &&
                rc1.bottom > rc2.top &&
                rc2.bottom > rc1.top
                )
            return true;
        else
            return false;
    }
    /**
     * 删除某一笔
     * @param strokeID 将要删除的笔画的ID
     */
    private void deleteStroke(int strokeID){
        //修改strokes中笔画状态为不可见
        strokes.get(strokeID).setVisible(false);

        //将要删除的笔画存入列表，为在endStroke时修改栈信息做准备
        deleteStrokeIDs.add(strokeID);


        rect_delete.union(strokes.get(strokeID).getRect_big());

        //更新显示
//        fixedThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                //清空核心中的内存区
//                HWPenEngine.fillSurface(engine,0x00ffffff);
//                if (bg != null && bg.length > 0){
//                    HWPenEngine.resetScan(engine, bg, bg.length);
//                }
//                blendStrokesToScreen();
//            }
//        });


//        HWPenEngine.fillSurface(engine,0x00ffffff);
//        if (bg != null && bg.length > 0){
//            HWPenEngine.resetScan(engine, bg, bg.length);
//        }
//        blendStrokesToScreen();

//        mHandler.removeMessages(MSG_BLEND);
//        mHandler.sendEmptyMessageDelayed(MSG_BLEND, 20);

        Message message = Message.obtain();
        message.what = 1;
        message.obj = rect_delete;
        mainHandler.removeMessages(1);
        mainHandler.sendMessageDelayed(message, 20);

    }
}
