package com.hanvon.canvasdemo.beans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hanvon.canvasdemo.Utils.IOUtils;
import com.hanvon.canvasdemo.constants.Templates;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.litepal.crud.DataSupport;
/**
 * Created by pc on 2017/11/2.
 */

public class HWPen {
    private static final String TAG = "HWPen.class";
    public static final int TYPE_ERASER_BY_LINE = 0;
    public static final int TYPE_ERASER_BY_POINT = 1;
    public static final int TYPE_PENCIL = 2;

    private static final int MSG_UPDATE_BITMAP_SHOW = 0;
    private static final int MSG_UPDATE_BITMAP_SHOW1 = 1;

    //画笔
    private Pen pen;

    //画布相关参数
    private Paint paint;
    private Canvas canvas_show, canvas_drawing;  //canvas_original;
    private Bitmap bitmap_show, bitmap_original, bitmap_drawing, bitmap_template;
    //模板参数
    private int templateWidth = 62;
    private int templateHeight = 62;
    //其他
    private Storage storage;
    private LinkedList<Point> listClone;
    private Matrix matrix;
    private int strokeID = 0;
    private ImageView iv_canvas;
    //笔画列表
    private LinkedList<Stroke> list_stroke;
    //撤销栈（撤销时进行抛栈）：绘画、擦除、恢复时进行压栈
    private Stack undoStack;
    //恢复栈（恢复时进行抛栈）：执行撤销时进行压栈，
    private Stack redoStack;
    private boolean isDown;     //用于标记被删除的笔画是否是在橡皮一次擦除的
    private Context context;


    public HWPen(){
    };

//    public HWPen(int type, Template template, int width, int color) {
//        new HWPen(type, template, width, color, false, false, 255);
//    }
//
//    public HWPen(int type, Template template, int width, int color, boolean isBeautify, boolean isTransparent, int alpha) {
//        this.type = type;
//        this.template = template;
//        this.width = width;
//        this.color = color;
//        this.isBeautify = isBeautify;
//        this.isTransparent = isTransparent;
//        this.alpha = alpha;
//    }

//    public static HWPen getInstence(){
//        return SingletonHolder.sInstance;
//    }
//    private static class SingletonHolder{
//        private static final HWPen sInstance = new HWPen();
//    }


/**************************************接口******************************************/
    /**
     * 初始化汉王画笔
     * @param screenWidth 画布的宽
     * @param screenHeight 画布的高
     * @param type  画笔类型
     * @param template 画笔模板
     * @param width 画笔宽
     * @param color 画笔颜色
     * @param isBeautify 画笔是否美化
     * @param isTransparent 画笔是否透明
     * @param alpha 画笔的透明度
     */
    public void init(Context context, int screenWidth, int screenHeight, int type, Template template, int width, int color, boolean isBeautify, boolean isTransparent, int alpha){
        //初始化画笔
        pen = new Pen(type, template, width, color, isBeautify, isTransparent, alpha);

        //初始化canvas和bitmap
        initBitmapAndCanvas(screenWidth, screenHeight);
        initPaint();

        //初始化一个点的仓库
        storage = new Storage();
        //初始化一个列表，用来存储从仓库中取出的点
        listClone = new LinkedList<Point>();
        //初始化笔画列表
        list_stroke = new LinkedList<Stroke>();
        undoStack = new Stack();
        redoStack = new Stack();
    }

    /**
     * 设置落笔时的画笔图层
     * @param bitmap
     */
    public void setOriginalBitmap(Bitmap bitmap){
        bitmap_original = bitmap;
        isDown = true;
//        canvas_original = new Canvas(bitmap_original);
//        canvas_original.drawColor(Color.argb(0,0,0,0));

        //清空临时位图，保证此图层中只包含当前笔
        if (bitmap_drawing != null){
            bitmap_drawing.eraseColor(Color.argb(0,0,0,0));
        }

        //因为不知道为什么Rects中莫名出现了大矩形，所以用蹩脚的方式删除它
        if(list_stroke.size() > 0 && list_stroke.getLast().getRects().size() > 0){
            Rect rect = list_stroke.getLast().getRects().get(0);
            Rect rect_big = list_stroke.getLast().getRect_big();
            if (rect.equals(rect_big)){
                list_stroke.getLast().getRects().remove(0);
            }
        }

        if (pen.getType() == TYPE_ERASER_BY_LINE || pen.getType() == TYPE_ERASER_BY_POINT){
            redoStack.clear();
        }else{
//            strokeID++;
            strokeID = list_stroke.size();
            //刚落笔时历史，在笔画列表中添加一个笔画，并设置当前笔画的ID、层、是否可见、笔等属性
            Stroke stroke = new Stroke();
            stroke.setStrokeID(strokeID);
            stroke.setCurrentLayer(strokeID);
            stroke.setVisible(true);   //感觉这里应该是true，之后再确认
            stroke.setPen(pen);
            list_stroke.addLast(stroke);

            //压栈
            State state = new State();
            state.getStrokes().add(strokeID);
            state.setVisible(false);
            state.setLayer(strokeID);
            undoStack.push(state);
            redoStack.clear();
        }
     }

    /**
     * 绘画
     * @param iv_canvas 画笔绘制后所更新的view
     * @param x0    线段起点的X坐标
     * @param y0    线段起点的Y坐标
     * @param x1    线段终点的X坐标
     * @param y1    线段终点的Y坐标
     */
    public void drawLiner(final ImageView iv_canvas, final float x0, final float y0, final float x1, final float y1){
        /********可以做异步优化， 将生成点、画点均设置成异步*************/
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                creatAndSavePointToStorage(x0, y0, x1, y1);
//                getPointFromStorageAndDraw(iv_canvas);
//            }
//        }).start();
        if(pen.getType() == TYPE_ERASER_BY_LINE || pen.getType() == TYPE_ERASER_BY_POINT){
            Rect rect_eraser = creatRect(x0, y0, x1, y1);
            for (final Stroke stroke : list_stroke){
                if (!stroke.isVisible()){
                    continue;
                }
                if (isOverlap(stroke.getRect_big(), rect_eraser)){
                    Log.i(TAG, "和大矩形相交");
                    for (Rect rect : stroke.getRects()){
                        if (isOverlap(rect, rect_eraser)) {
                            Log.i(TAG, "和小矩形相交");
                            if (pen.getType() == TYPE_ERASER_BY_LINE){      //整笔擦除
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteStrokeAll(stroke.getStrokeID());
                                    }
                                }).start();
//                                deleteStrokeAll(stroke.getStrokeID());

                            }else{      //精细擦除
//                                deleteStrokePart(stroke.getStrokeID(), );
                            }
                            break;
                        }
                    }
                }
            }
        }else{
            this.iv_canvas = iv_canvas;
            if (list_stroke.size() > 1){  //说明不是第一笔
                Stroke stroke_last = list_stroke.get(list_stroke.size() - 2);
                Stroke stroke_cur = list_stroke.getLast();
                //对所得到的笔迹进行合法化判断
                if (stroke_last == null){
                    stroke_last = new Stroke();
                    stroke_last.setStrokeID(strokeID);
                    stroke_last.setCurrentLayer(strokeID);
                    stroke_last.setVisible(true);          //感觉这里应该是true，之后再确认
                    stroke_last.setPen(pen);
                    list_stroke.addLast(stroke_last);
                }

                if (stroke_cur.getPoints_part() != null ) {
                    //将采集的点保存到笔迹列表中
                    stroke_cur.getPoints_part().add(new Point(strokeID, x1, y1));
                    //计算计算矩形块，并保存到笔迹列表中
                    Rect old_rect_big = stroke_cur.getRect_big();
                    Rect rect = creatRect(x0, y0, x1, y1);

                    if (rect.left < old_rect_big.left)
                        old_rect_big.left = rect.left;
                    if (rect.top < old_rect_big.top)
                        old_rect_big.top = rect.top;
                    if (rect.right > old_rect_big.right)
                        old_rect_big.right = rect.right;
                    if (rect.bottom > old_rect_big.bottom)
                        old_rect_big.bottom = rect.bottom;
                    stroke_cur.setRect_big(old_rect_big);
                    stroke_cur.getRects().addLast(rect);
//                    Log.i(TAG, "wwwww其他笔      l=" + rect.left + ", t=" + rect.top + ", r=" + rect.right + ", b=" + rect.bottom);
//                    Log.i(TAG, "wwwww其他笔 大框 l=" + old_rect_big.left + ", t=" + old_rect_big.top + ", r=" + old_rect_big.right + ", b=" + old_rect_big.bottom);
                }else{
                    //如果当前笔没有存储过采集笔迹点
                    LinkedList<Point> list = new LinkedList<Point>();
                    list.add(new Point(strokeID, x0, y0));
                    list.add(new Point(strokeID, x1, y1));
                    stroke_cur.setPoints_part(list);

                    Rect rect = creatRect(x0, y0, x1, y1);
                    LinkedList list_rect = new LinkedList();
                    list_rect.add(rect);
                    stroke_cur.setRects(list_rect);
                    stroke_cur.setRect_big(rect);
                }
            }else{ //说明是第一笔，此时不用做比较
                if (list_stroke.size() == 0){
                    Stroke stroke_last = new Stroke();
                    stroke_last.setStrokeID(strokeID);
                    stroke_last.setCurrentLayer(strokeID);
                    stroke_last.setVisible(true);          //感觉这里应该是true，之后再确认
                    stroke_last.setPen(pen);
                    list_stroke.addLast(stroke_last);
                }
                Stroke stroke_cur = list_stroke.getLast();

                if (stroke_cur.getPoints_part() != null ) {
                    //将采集的点保存到笔迹列表中
                    stroke_cur.getPoints_part().add(new Point(strokeID, x1, y1));
                    //计算计算矩形块，并保存到笔迹列表中
                    Rect old_rect_big = stroke_cur.getRect_big();
                    Rect rect = creatRect(x0, y0, x1, y1);
                    if (rect.left < old_rect_big.left)
                        old_rect_big.left = rect.left;
                    if (rect.top < old_rect_big.top)
                        old_rect_big.top = rect.top;
                    if (rect.right > old_rect_big.right)
                        old_rect_big.right = rect.right;
                    if (rect.bottom > old_rect_big.bottom)
                        old_rect_big.bottom = rect.bottom;
                    stroke_cur.setRect_big(old_rect_big);
                    stroke_cur.getRects().addLast(rect);
//                    Log.i(TAG, "wwwww第一笔      l=" + rect.left + ", t=" + rect.top + ", r=" + rect.right + ", b=" + rect.bottom);
//                    Log.i(TAG, "wwwww第一笔 大框 l=" + old_rect_big.left + ", t=" + old_rect_big.top + ", r=" + old_rect_big.right + ", b=" + old_rect_big.bottom);

//                    Rect rect_s = list_stroke.getLast().getRects().get(0);
//                    Rect rect_b = list_stroke.getLast().getRect_big();
//                    Log.i(TAG, "wwwww      l=" + rect_s.left + ", t=" + rect_s.top + ", r=" + rect_s.right + ", b=" + rect_s.bottom);
//                    Log.i(TAG, "wwwww 大框 l=" + rect_b.left + ", t=" + rect_b.top + ", r=" + rect_b.right + ", b=" + rect_b.bottom);
                }else {
                    //如果当前笔没有存储过采集笔迹点
                    LinkedList<Point> list = new LinkedList<Point>();
                    list.add(new Point(strokeID, x0, y0));
                    list.add(new Point(strokeID, x1, y1));
                    stroke_cur.setPoints_part(list);

                    //计算计算矩形块，并保存到笔迹列表中
                    Rect rect = creatRect(x0, y0, x1, y1);
                    LinkedList list_rect = new LinkedList();
                    list_rect.add(rect);
                    stroke_cur.setRects(list_rect);
                    stroke_cur.setRect_big(rect);
                }
            }


            creatAndSavePointToStorage(x0, y0, x1, y1);
            getPointFromStorageAndDraw(iv_canvas);
        }
    }


    public boolean isRedoAble(){
        return  !redoStack.isEmpty();
    };

    public boolean isUndoAble(){
        return  !undoStack.isEmpty();
    };


    public void save(){
        saveStrokesAsJson();
    }

    public void clear(){
        bitmap_show.eraseColor(0);
        iv_canvas.setImageBitmap(bitmap_show);
        list_stroke.clear();
    }

    public void load(){
        String jsonString = IOUtils.readFileByChars("/mnt/sdcard/wwl/strokes.st");
        Gson gson = new Gson();
        Stroke[] strokesArray = gson.fromJson(jsonString, Stroke[].class);
        LinkedList<Stroke> list = new LinkedList<Stroke>();
        if (strokesArray != null && strokesArray.length > 0){
            for (int i = 0; i < strokesArray.length; i++){
                list.add(strokesArray[i]);
            }
        }
        list_stroke = list;
        uiHandler.sendEmptyMessageDelayed(MSG_UPDATE_BITMAP_SHOW1, 5);
    }

    public void redo(){
        uiHandler.removeMessages(MSG_UPDATE_BITMAP_SHOW1);
        changeStack(redoStack, undoStack);
        if (pen.isTransparent()){
            draw();
        }else{
//            draw1();
            uiHandler.sendEmptyMessageDelayed(MSG_UPDATE_BITMAP_SHOW1, 5);
        }

    }

    public void undo(){
        uiHandler.removeMessages(MSG_UPDATE_BITMAP_SHOW1);
        changeStack(undoStack, redoStack);
        if (pen.isTransparent()){
            draw();
        }else{
//            draw1();
            uiHandler.sendEmptyMessageDelayed(MSG_UPDATE_BITMAP_SHOW1, 5);
        }
    }


    public String saveStrokesAsJson(){
        StringBuilder sb = new StringBuilder();
        Gson gson = new Gson();
        String jsonString = gson.toJson(list_stroke);
        IOUtils.writeStringToFile(jsonString, "/mnt/sdcard/wwl/", "strokes.st");
        return sb.toString();
    }

    /**
     * 精细擦除
     * @param strokeID      要擦除的笔画
     * @param pointIndex    擦除点在当前笔画列表中的索引
     * @param pointNums     根据橡皮大小确定擦除几个点
     */
    private void deleteStrokePart(int strokeID, int pointIndex, int pointNums) {
        //比判断用来使在同一次擦除的笔迹都被压到统一栈
        if (isDown){
            State state = new State();
            state.getStrokes().add(strokeID);
            state.setVisible(true);
            try {
                state.setLayer(list_stroke.get(strokeID).getCurrentLayer());
            }catch (Exception e){
                Log.e(TAG, "strokeID = " + strokeID);
                Log.e(TAG, "list_stroke.size() = " + list_stroke.size());
                e.printStackTrace();
            }
            undoStack.push(state);
            redoStack.clear();
        }else{

        }
        isDown = false;     //应该放到第一个分支的结束

        //改变笔画列表中的信息：将原笔画中点改为子笔画部分的点，并在笔画列表中新增笔画，层为原来的层
        Stroke stroke_original = list_stroke.get(strokeID);
        int fromIndex = pointIndex + pointNums;
        int toIndex = stroke_original.getPoints_part().size() - 1;
        if (pointIndex != 0 && fromIndex >= toIndex){
            //此时说明删除的是笔画的末尾，不会有新增笔画

        }else if (pointIndex == 0 && fromIndex < toIndex){
            //此时说明删除的是笔画的开头，

        }else if (pointIndex == 0 && fromIndex >= toIndex){
            //此时说明整笔都被删除了

        }else{
            LinkedList<Point> list_points_part_new = (LinkedList<Point>) stroke_original.getPoints_part().subList(fromIndex, toIndex);



        }


        //将新增笔画也添加到当前栈，状态为不显示。   暂时先按显示状态做，肯定是有重叠，即颜色加深

        //重绘

    }

    /**
     * 整笔擦除
     * @param strokeID      要擦除的笔画
     */
    private void deleteStrokeAll(int strokeID){
        //此判断用来使在同一次擦除的笔迹都被压到同一栈
        if (isDown){
            //压栈
            State state = new State();
            state.getStrokes().add(strokeID);
            state.setVisible(true);
            try{
                state.setLayer(list_stroke.get(strokeID).getCurrentLayer());                //这里经常会有越界异常
            }catch (Exception e){
                Log.e(TAG, "strokeID = " + strokeID);
                Log.e(TAG, "list_stroke.size() = " + list_stroke.size());
                e.printStackTrace();
            }

            undoStack.push(state);
            redoStack.clear();
        }else{
            State state = (State) undoStack.pop();
            state.getStrokes().add(strokeID);
            undoStack.push(state);
        }
        isDown = false;     //应该放到第一个分支的结束

        //改变笔画列表中的信息
        try{
            list_stroke.get(strokeID).setVisible(false);
        }catch (Exception e){
            Log.e(TAG, "---strokeID = " + strokeID);
            Log.e(TAG, "---list_stroke.size() = " + list_stroke.size());
            e.printStackTrace();
        }
        uiHandler.removeMessages(MSG_UPDATE_BITMAP_SHOW1);
        uiHandler.sendEmptyMessageDelayed(MSG_UPDATE_BITMAP_SHOW1, 15);
    }

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



    private void changeStack(Stack stackForPop, Stack stackForPush) {
        try {
            State stateFromRedoStack = (State) stackForPop.pop();
            State stateForUndoStack = new State();
            List<Integer> strokesID = stateFromRedoStack.getStrokes();
            Iterator it = strokesID.iterator();
            while (it.hasNext()){
                int strokeID = (int) it.next();
                //将涉及的笔画的现状信息进行存栈
                stateForUndoStack.setVisible(list_stroke.get(strokeID).isVisible());
//                //修改层的逻辑现在先不做
//                stateForRedoStack.setLayer(list_stroke.get(strokeID).getCurrentLayer());
                stateForUndoStack.getStrokes().add(strokeID);

                //改变涉及笔画的状态
                Stroke stroke = list_stroke.get(strokeID);
                stroke.setVisible(stateFromRedoStack.isVisible());
                list_stroke.set(strokeID, stroke);
                //修改层的逻辑现在先不做
            }
            stackForPush.push(stateForUndoStack);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void draw() {
        //先清空屏幕
        bitmap_show.eraseColor(0);
        iv_canvas.setImageBitmap(bitmap_show);

        List<Point> list;
        Point point;
        for(int i = 0; i < list_stroke.size(); i++){
            long beginTime = System.currentTimeMillis();
//            list =  DataSupport.where("strokeID = ?", (i+1)+"").find(Point.class);
            if(i < 0 || i >= list_stroke.size()){
                return;
            }
            //判断当前笔迹是否可见，如果不可见，则调到下一步
            if(!list_stroke.get(i).isVisible()){
                continue;
            }
            list = list_stroke.get(i).getPoints();
            long engTime = System.currentTimeMillis();
//            Log.i(TAG, "读第" + i + "笔的时间 = " + (engTime-beginTime));
            if (list == null || list.size() == 0 ){
                continue;
            }
            bitmap_drawing.eraseColor(0);
            paint.setAlpha(255);
            //异步的将仓库中的点绘制到画笔图层中
            Iterator it = list.iterator();
            while (it.hasNext()){
                point = (Point) it.next();
                canvas_drawing.drawBitmap(bitmap_template, point.getX(), point.getY(), paint);
            }
            beginTime = engTime;
            engTime = System.currentTimeMillis();
//            Log.i(TAG, "绘制一笔的时间" + (engTime - beginTime));
            paint.setAlpha(pen.getAlpha());
            canvas_show.drawBitmap(bitmap_drawing, 0, 0, paint);
            iv_canvas.setImageBitmap(bitmap_show);
            beginTime = engTime;
            engTime = System.currentTimeMillis();
//            Log.i(TAG, "刷新的时间" + (engTime - beginTime));
        }
    }

    private void draw1() {
        //先清空屏幕
        bitmap_show.eraseColor(0);
        uiHandler.sendEmptyMessage(MSG_UPDATE_BITMAP_SHOW);

        List<Point> list;
        for(int i = 0; i < list_stroke.size(); i++){
            if(i < 0 || i >= list_stroke.size()){
                return;
            }
            //判断当前笔迹是否可见，如果不可见，则调到下一步
            if(!list_stroke.get(i).isVisible()){
                continue;
            }
            list = list_stroke.get(i).getPoints();
            if (list == null || list.size() == 0 ){
                continue;
            }
            paint.setAlpha(255);
            //异步的将仓库中的点绘制到画笔图层中
            for (Point point : list){
                canvas_show.drawBitmap(bitmap_template, point.getX(), point.getY(), paint);
            }
        }
        uiHandler.sendEmptyMessage(MSG_UPDATE_BITMAP_SHOW);
    }


    private Handler uiHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_BITMAP_SHOW:
                    iv_canvas.setImageBitmap(bitmap_show);
                    break;
                case MSG_UPDATE_BITMAP_SHOW1:
                    //重绘
                    //先清空屏幕
                    bitmap_show.eraseColor(0);
                    iv_canvas.setImageBitmap(bitmap_show);

                    List<Point> list;
                    for(int i = 0; i < list_stroke.size(); i++){
                        if(i < 0 || i >= list_stroke.size()){
                            return;
                        }
                        //判断当前笔迹是否可见，如果不可见，则跳到下一步
                        if(!list_stroke.get(i).isVisible()){
                            continue;
                        }
                        list = list_stroke.get(i).getPoints();
                        if (list == null || list.size() == 0 ){
                            continue;
                        }
                        bitmap_drawing.eraseColor(0);
                        paint.setAlpha(255);
                        //根据当前笔是否透明，采取不同的绘制机制
                        if (list_stroke.get(i).getPen().isTransparent()){
                            //异步的将仓库中的点绘制到画笔图层中
                            for (Point point: list){
                                canvas_drawing.drawBitmap(bitmap_template, point.getX(), point.getY(), paint);
                            }
                            paint.setAlpha(pen.getAlpha());
                            canvas_show.drawBitmap(bitmap_drawing, 0, 0, paint);
                            uiHandler.sendEmptyMessage(MSG_UPDATE_BITMAP_SHOW);
                        }else{
                            //异步的将仓库中的点绘制到画笔图层中
                            for (Point point: list){
                                canvas_show.drawBitmap(bitmap_template, point.getX(), point.getY(), paint);
                            }
                            uiHandler.sendEmptyMessage(MSG_UPDATE_BITMAP_SHOW);
                        }
                    }
                    break;
            }
        }
    };



//
//    private void updateStrokeState(){
//        if(stackIndex >=0 && stackIndex < list_state.size()){
//            State state = list_state.get(stackIndex);
//            List<Integer> strokesID = state.getStrokes();
//            Iterator it = strokesID.iterator();
//            if(it.hasNext()){
//                Stroke stroke = list_stroke.get((Integer) it.next());
//                state.setVisible(state.isVisible());
//                //修改层的逻辑现在先不做
//            }
//            if(stackIndex > 0){
//                stackIndex--;
//            }
//        }
//    }


    private void initPaint(){
//        paint = new Paint();
//        paint.setStrokeWidth(5);
//        paint.setColor(Color.RED);

        //创建Paint
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        if (pen.isTransparent()){
            paint.setAlpha(pen.getAlpha());
        }else{
            paint.setAlpha(255);
        }
    }

    private void initBitmapAndCanvas(int screenWidth, int screenHeight) {
        //初始化显示位图
        if (bitmap_show == null) {
            bitmap_show = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
            canvas_show = new Canvas(bitmap_show);
            canvas_show.drawColor(Color.argb(0,0,0,0));
        }
        //初始化临时位图
        if(bitmap_drawing == null){
            bitmap_drawing = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
            canvas_drawing = new Canvas(bitmap_drawing);
            canvas_drawing.drawColor(Color.argb(0,0,0,0));
        }
        //设置模板位图
        if(bitmap_template == null){
            bitmap_template = Bitmap.createBitmap(templateWidth, templateHeight, Bitmap.Config.ARGB_8888);
        }
        bitmap_template.setPixels(Templates.red_circle, 0, templateWidth, 0, 0, templateWidth, templateHeight);
//        //对模板进行缩放
//        if(matrix == null){
//            matrix = new Matrix();
//        }
//        float zoom = (float) (Math.random() + 0.5);
//        matrix.postScale(zoom, zoom);
//        //bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
//        bitmap_template = Bitmap.createBitmap(bitmap_template, 0, 0, templateWidth, templateHeight,
//                matrix, true);
    }
    //绘制模板
    private void drawPoint(float x, float y){
        if (pen.isTransparent()){
            //将模板绘制在临时图层上
            if(canvas_drawing != null && bitmap_template != null && paint != null){
                paint.setAlpha(255);
                canvas_drawing.drawBitmap(bitmap_template, x, y, paint);
            }
        }else{
            if(canvas_show != null && bitmap_template != null && paint != null){
                paint.setAlpha(255);
                canvas_show.drawBitmap(bitmap_template, x, y, paint);
            }
        }

    }

    private void savePointsToDB(final LinkedList<Point> list){
        //用于将轨迹存入数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Stroke stroke = new Stroke();
                LinkedList<Point> mList = (LinkedList<Point>) list.clone();
                DataSupport.saveAll(mList);
            }
        }).start();
    }

    private void getPointFromStorageAndDraw(ImageView iv_canvas) {
        Point point;
        listClone = storage.consume();
        if (listClone == null || listClone.size() == 0){
            return;
        }
//        //异步的将仓库中的点存入到数据库中
//        savePointsToDB(listClone);
        //将点列表添加到笔画列表的对应笔画属性中
        if (list_stroke.getLast() != null && list_stroke.getLast().getPoints() != null ) {
            list_stroke.getLast().getPoints().addAll((LinkedList<Point>) listClone.clone());
        }else{
            list_stroke.getLast().setPoints((LinkedList<Point>) listClone.clone());
        }

        //异步的将仓库中的点绘制到画笔图层中
        Iterator it = listClone.iterator();
        while (it.hasNext()){
            point = (Point) it.next();
            drawPoint(point.getX(), point.getY());
        }
        //根据透明和不透明采用不同的绘制策略
        if(pen.isTransparent()){
            updateTempBitmapToScreen();
            iv_canvas.setImageBitmap(bitmap_show);
            listClone.clear();
        }else{
            iv_canvas.setImageBitmap(bitmap_show);
            listClone.clear();
        }

    }
    /**
     * 只是将画笔图层和原始图层合并到显示图层上，但是并没有刷新显示
     */
    private void updateTempBitmapToScreen(){
        //清空显示图层
        bitmap_show.eraseColor(0);
        //将原始图层绘制到base图层上
        paint.setAlpha(255);
        canvas_show.drawBitmap(bitmap_original, 0, 0, paint);
        //将临时图层绘制在base图层上
        paint.setAlpha(pen.getAlpha());
        canvas_show.drawBitmap(bitmap_drawing, 0, 0, paint);
    }
    private void creatAndSavePointToStorage(float x0, float y0, float x1, float y1){
        float tempX, tempY;
        float rate;
        float xSize = x1 - x0;
        float ySize = y1 - y0;
        boolean isXInc = x1 > x0 ? true : false;
        boolean isYInc = y1 > y0 ? true : false;
        boolean isXLongSide = Math.abs(xSize) > Math.abs(ySize) ? true : false;

        if (isXLongSide){
            if (xSize == 0){
                return;
            }
            rate = Math.abs(ySize/xSize);
            if (isXInc){
                if (isYInc){
                    for (int i = 0; i < Math.abs(xSize); i++){
                        storage.produce(new Point(strokeID, x0 + i, y0 + (i * rate)));
                    }
                }else{
                    for (int i = 0; i < Math.abs(xSize); i++){
                        storage.produce(new Point(strokeID, x0 + i, y0 - (i * rate)));
                    }
                }
            }else{
                if (isYInc){
                    for (int i = 0; i < Math.abs(xSize); i++){
                        storage.produce(new Point(strokeID, x0 - i, y0 + (i * rate)));
                    }
                }else{
                    for (int i = 0; i < Math.abs(xSize); i++){
                        storage.produce(new Point(strokeID, x0 - i, y0 - (i * rate)));
                    }
                }
            }
        }else{
            if (ySize == 0){
                return;
            }
            rate = Math.abs(xSize/ySize);
            if (isYInc){
                if(isXInc){
                    for (int i = 0; i < Math.abs(ySize); i++){
                        storage.produce(new Point(strokeID, x0 + (i * rate), y0 + i));
                    }
                }else{
                    for (int i = 0; i < Math.abs(ySize); i++){
                        storage.produce(new Point(strokeID, x0 - (i * rate), y0 + i));
                    }
                }
            }else{
                if(isXInc){
                    for (int i = 0; i < Math.abs(ySize); i++){
                        storage.produce(new Point(strokeID, x0 + (i * rate), y0 - i));
                    }
                }else{
                    for (int i = 0; i < Math.abs(ySize); i++){
                        storage.produce(new Point(strokeID, x0 - (i * rate), y0 - i));
                    }
                }
            }
        }
    }

    public void recycle(){
        paint = null;
        canvas_show = null;
        canvas_drawing = null;
        if(bitmap_show != null){
            bitmap_show.recycle();
            bitmap_show = null;
        }
        if(bitmap_original != null){
            bitmap_original.recycle();
            bitmap_original = null;
        }
        if (bitmap_drawing != null){
            bitmap_drawing.recycle();
            bitmap_drawing = null;
        }
        if(bitmap_template != null){
            bitmap_template.recycle();
            bitmap_template = null;
        }
        storage = null;
        if(listClone != null){
            listClone.clear();
            listClone = null;
        }
    }

    public Pen getPen() {
        return pen;
    }

    public void setPen(Pen pen) {
        this.pen = pen;
    }
}
