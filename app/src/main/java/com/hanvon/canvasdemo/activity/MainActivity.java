//package com.hanvon.canvasdemo.activity;
//
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.hanvon.canvasdemo.R;
//import com.hanvon.canvasdemo.constants.Templates;
//import com.hanvon.canvasdemo.beans.Storage;
//
//import java.util.Iterator;
//import java.util.LinkedList;
//
//public class MainActivity extends AppCompatActivity {
//    private static final String TAG = "MainActivity";
//    private static final int MSG_DRAWLINE = 0;
//    private static final int MSG_UPDATE_SCREEN = 1;
//    private static final int MSG_CHILD_DRAWLINE = 0;
//    private static final int MSG_CHILD_CREAT_AND_SAVE_POINT = 1;
//    private static final int MSG_UPDATE_BITMAP_TO_SCREEN = 2;
//
//
//    private ImageView iv_canvas;
//    private Canvas canvas_show;
//    private Canvas tempCanvas;
//    private Canvas canvas_original;
//    private Paint paint,mBitPaint, tempPaint;
//    private Resources mResources;
//    private Bitmap bitmap_show;
//    private Bitmap mBitmap;
//    private Bitmap bitmapTemplate;
//    private Bitmap bitmap_temp;
//    private Bitmap bitmap_original;
//    private int mBitWidth,mBitHeight;
//    private int mTotalWidth, mTotalHeight;
//    private Storage storage;
//    private LinkedList<Object> listClone;
//    private boolean isMoveUp = false;
//    private Handler mUIHandler, mChildHandler;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
////        final DrawBitmapView drawBitmapView = new DrawBitmapView(this);
////        setContentView(drawBitmapView, new LayoutParams(LayoutParams.MATCH_PARENT,
////                LayoutParams.MATCH_PARENT));
////        mView = (DrawBitmapView) findViewById(R.id.mView);
//        mResources = getResources();
//        iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
//        iv_canvas.setOnTouchListener(touch1);
//        mTotalWidth = iv_canvas.getWidth();
//        mTotalHeight = iv_canvas.getHeight();
//
//        initPaint();
//        initBitmap();
//
//        storage = new Storage();
//        listClone = new LinkedList<Object>();
////        mSrcRect = new Rect(0, 0, mBitWidth, mBitHeight);
//
//        HandlerThread handlerThread = new HandlerThread("drawBitmap");
//        //必须先开启线程
//        handlerThread.start();
//        //子线程Handler
//        mChildHandler = new Handler(handlerThread.getLooper(),new ChildCallback());
//        mUIHandler = new Handler(getMainLooper(), new UICallback());
//    }
//
//    private void initPaint() {
//        paint = new Paint();
//        paint.setStrokeWidth(5);
//        paint.setColor(Color.RED);
//
//        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mBitPaint.setFilterBitmap(true);
//        paint.setAntiAlias(true);
//        mBitPaint.setDither(true);
//        mBitPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
//        mBitPaint.setAlpha(120);
//
//        tempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        tempPaint.setFilterBitmap(true);
//        paint.setAntiAlias(true);
//        tempPaint.setDither(true);
//        tempPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
//        tempPaint.setAlpha(255);
//    }
//
//    private void initBitmap() {
//        mBitmap = ((BitmapDrawable) mResources.getDrawable(R.mipmap.ic_launcher))
//                .getBitmap();
//        mBitWidth = mBitmap.getWidth();
//        mBitHeight = mBitmap.getHeight();
//
//        createTemplateBitmap(62, 62);
//    }
//
//    /**
//     *
//     * @param templateWidth 模板宽度
//     * @param templateHeight 模板高度
//     */
//    private void createTemplateBitmap(int templateWidth, int templateHeight) {
//        bitmapTemplate = Bitmap.createBitmap(templateWidth, templateHeight, Bitmap.Config.ARGB_8888);
//    }
//
//    private View.OnTouchListener touch = new View.OnTouchListener() {
//        // 定义手指开始触摸的坐标
//        float startX;
//        float startY;
//        int pointNum;
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                // 用户按下动作
//                case MotionEvent.ACTION_DOWN:
//                    // 第一次绘图初始化内存图片，指定背景为白色
//                    if (bitmap_show == null) {
//                        bitmap_show = Bitmap.createBitmap(iv_canvas.getWidth(),
//                                iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
//                        canvas_show = new Canvas(bitmap_show);
//                        canvas_show.drawColor(Color.WHITE);
//                        }
//                    // 记录开始触摸的点的坐标
//                    startX = event.getX();
//                    startY = event.getY();
//
//                    pointNum = 1;
//                    break;
//                // 用户手指在屏幕上移动的动作
//                case MotionEvent.ACTION_MOVE:
//                    // 记录移动位置的点的坐标
//                    float stopX = event.getX();
//                    float stopY = event.getY();
//                    //根据两点坐标，绘制连线
//                    canvas_show.drawLine(startX, startY, stopX, stopY, paint);
//
//                    // 更新开始点的位置
//                    startX = event.getX();
//                    startY = event.getY();
//
//                    // 把图片展示到ImageView中
//                    iv_canvas.setImageBitmap(bitmap_show);
//                    pointNum++;
//                    break;
//                case MotionEvent.ACTION_UP:
//                    Log.e(TAG, "采集了" + pointNum + " 个点");
//                    Toast.makeText(MainActivity.this, "采集了" + pointNum + " 个点", Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    break;
//            }
//            return true;
//        }
//    };
//
//    private View.OnTouchListener touch1 = new View.OnTouchListener() {
//        float xOld, yOld, xNew, yNew;
//        Bundle bundle = new Bundle();
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            switch (motionEvent.getAction()){
//                case MotionEvent.ACTION_DOWN:
//                    isMoveUp = false;
//                    xOld = motionEvent.getX();
//                    yOld = motionEvent.getY();
//                    Log.e(TAG, "在ACTION_DOWN时调用reset");
//                    checkBitmapValid();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    xNew = motionEvent.getX();
//                    yNew = motionEvent.getY();
////                    creatAndSavePointToStorage(xOld-62, yOld-62, xNew-62, yNew-62);
//                    Message msg = Message.obtain();
//                    msg.what = MSG_CHILD_CREAT_AND_SAVE_POINT;
//                    bundle.putFloat("xOld", xOld);
//                    bundle.putFloat("yOld", yOld);
//                    bundle.putFloat("xNew", xNew);
//                    bundle.putFloat("yNew", yNew);
//                    msg.setData(bundle);
//                    mChildHandler.sendMessage(msg);
//                    xOld = xNew;
//                    yOld = yNew;
////                    updateTempBitmapToScreen();
//                    break;
//                case MotionEvent.ACTION_UP:
////                    test_rate();
////                    test_getPixels_and_setPixels(motionEvent);
//
////                    updateTempBitmapToScreen();
//
////                    resetSrcBitmapAndTempBitmap();
////                    isMoveUp = true;
//                    break;
//                default:
//            }
//
//            return true;
//        }
//    };
//
//    class UICallback implements Handler.Callback{
//        float temp;
//        @Override
//        public boolean handleMessage(Message message) {
//            switch (message.what){
//                case MSG_DRAWLINE :
//                    listClone = storage.consume();
//                    Iterator it = listClone.iterator();
//                    while (it.hasNext()){
//                        temp = (float)it.next();
//                        if(it.hasNext()){
//                            drawPoint(temp, (float)it.next(), tempCanvas, bitmapTemplate, tempPaint);
//                        }
//                    }
//                    updateTempBitmapToScreen();
//                    iv_canvas.setImageBitmap(bitmap_show);
////                    mUIHandler.sendEmptyMessage(MSG_UPDATE_SCREEN);
//                    listClone.clear();
//                    return true;
//                case MSG_UPDATE_SCREEN:
//                    //将绘制后的显示图层更新到屏幕上
//                    iv_canvas.setImageBitmap(bitmap_show);
////                    listClone.clear();
//                    return true;
//                default:
//                    return false;
//            }
//        }
//    }
//
//    /**
//     * 该callback运行于子线程
//     */
//    class ChildCallback implements Handler.Callback {
//        Bundle bundle = new Bundle();
//        float temp;
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what){
//                case MSG_CHILD_DRAWLINE:
//                    listClone = storage.consume();
//                    Iterator it = listClone.iterator();
//                    while (it.hasNext()){
//                        temp = (float)it.next();
//                        if(it.hasNext()){
//                            drawPoint(temp, (float)it.next(), tempCanvas, bitmapTemplate, tempPaint);
//                        }
//                    }
//
//                    updateTempBitmapToScreen();
//                    mUIHandler.sendEmptyMessage(MSG_UPDATE_SCREEN);
////                    mUIHandler.sendEmptyMessage(MSG_UPDATE_BITMAP_TO_SCREEN);
////                    listClone.clear();
//                    return true;
//                case MSG_CHILD_CREAT_AND_SAVE_POINT:
//                    bundle = msg.getData();
//                    creatAndSavePointToStorage((float)bundle.get("xOld"), (float)bundle.get("yOld"),
//                            (float)bundle.get("xNew"), (float)bundle.get("yNew"));
//                    return true;
//                case MSG_UPDATE_BITMAP_TO_SCREEN:
//
//                    return true;
//                default:
//                    return false;
//            }
//        }
//    }
//
//    /**
//     * 将模板绘制到图层上
//     * @param x 模板的左上角X坐标
//     * @param y 模板的左上角Y坐标
//     */
//    private void drawPoint(float x, float y, Canvas canvas, Bitmap bitmap, Paint paint){
//        //将模板绘制在临时图层上
//        canvas.drawBitmap(bitmap, x, y, paint);
//    }
//
//    private void creatAndSavePointToStorage(float x0, float y0, float x1, float y1){
//        float tempX, tempY;
//        float rate;
//        float xSize = x1 - x0;
//        float ySize = y1 - y0;
//        boolean isXInc = x1 > x0 ? true : false;
//        boolean isYInc = y1 > y0 ? true : false;
//        boolean isXLongSide = Math.abs(xSize) > Math.abs(ySize) ? true : false;
//        if (isXLongSide){
//            if (xSize == 0){
//                return;
//            }
//            rate = Math.abs(ySize/xSize);
//            if (isXInc){
//                if (isYInc){
//                    for (int i = 0; i < Math.abs(xSize); i++){
//                        tempX = x0 + i;
//                        tempY = y0 + (i * rate);
////                        drawPoint(tempX, tempY);
//                        storage.produce(tempX);
//                        storage.produce(tempY);
//                    }
//                }else{
//                    for (int i = 0; i < Math.abs(xSize); i++){
//                        tempX = x0 + i;
//                        tempY = y0 - (i * rate);
////                        drawPoint(tempX, tempY);
//                        storage.produce(tempX);
//                        storage.produce(tempY);
//                    }
//                }
//            }else{
//                if (isYInc){
//                    for (int i = 0; i < Math.abs(xSize); i++){
//                        tempX = x0 - i;
//                        tempY = y0 + (i * rate);
////                        drawPoint(tempX, tempY);
//                        storage.produce(tempX);
//                        storage.produce(tempY);
//                    }
//                }else{
//                    for (int i = 0; i < Math.abs(xSize); i++){
//                        tempX = x0 - i;
//                        tempY = y0 - (i * rate);
////                        drawPoint(tempX, tempY);
//                        storage.produce(tempX);
//                        storage.produce(tempY);
//                    }
//                }
//            }
//        }else{
//            if (ySize == 0){
//                return;
//            }
//            rate = Math.abs(xSize/ySize);
//            if (isYInc){
//                if(isXInc){
//                    for (int i = 0; i < Math.abs(ySize); i++){
//                        tempY = y0 + i;
//                        tempX = x0 + (i * rate);
////                        drawPoint(tempX, tempY);
//                        storage.produce(tempX);
//                        storage.produce(tempY);
//                    }
//                }else{
//                    for (int i = 0; i < Math.abs(ySize); i++){
//                        tempY = y0 + i;
//                        tempX = x0 - (i * rate);
////                        drawPoint(tempX, tempY);
//                        storage.produce(tempX);
//                        storage.produce(tempY);
//                    }
//                }
//            }else{
//                if(isXInc){
//                    for (int i = 0; i < Math.abs(ySize); i++){
//                        tempY = y0 - i;
//                        tempX = x0 + (i * rate);
////                        drawPoint(tempX, tempY);
//                        storage.produce(tempX);
//                        storage.produce(tempY);
//                    }
//                }else{
//                    for (int i = 0; i < Math.abs(ySize); i++){
//                        tempY = y0 - i;
//                        tempX = x0 - (i * rate);
////                        drawPoint(tempX, tempY);
//                        storage.produce(tempX);
//                        storage.produce(tempY);
//                    }
//                }
//            }
//        }
//        mUIHandler.sendEmptyMessage(MSG_DRAWLINE);
//        Log.e(TAG, "发送绘制消息");
////        mChildHandler.sendEmptyMessage(MSG_CHILD_DRAWLINE);
//    }
//
//    private void checkBitmapValid() {
////        resetSrcBitmapAndTempBitmap();
//
//        //清空原始位图
//        iv_canvas.setDrawingCacheEnabled(true);
//        iv_canvas.buildDrawingCache();
//        bitmap_original = Bitmap.createBitmap(iv_canvas.getDrawingCache());
//        iv_canvas.setDrawingCacheEnabled(false);
//
//        //清空临时位图，保证此图层中只包含当前笔
//        if (bitmap_temp != null){
//            bitmap_temp.eraseColor(Color.argb(0,0,0,0));
//        }
//
//        //初始化显示位图
//        if (bitmap_show == null) {
//            int width = iv_canvas.getWidth();
//            int height = iv_canvas.getHeight();
//            bitmap_show = Bitmap.createBitmap(iv_canvas.getWidth(),
//                    iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
//            canvas_show = new Canvas(bitmap_show);
//            canvas_show.drawColor(Color.argb(0,0,0,0));
//        }
//
//
//        //初始化临时位图
//        if(bitmap_temp == null){
//            bitmap_temp = Bitmap.createBitmap(iv_canvas.getWidth(),
//                    iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
//            tempCanvas = new Canvas(bitmap_temp);
//            tempCanvas.drawColor(Color.argb(0,0,0,0));
//        }
//        //设置模板位图
//        bitmapTemplate.setPixels(Templates.red_circle, 0, bitmapTemplate.getWidth(), 0, 0, bitmapTemplate.getWidth(), bitmapTemplate.getHeight());
//    }
//
//    private void resetSrcBitmapAndTempBitmap() {
//        Log.e(TAG, "原始图层和画笔被清空");
//        //清空原始位图
//        iv_canvas.setDrawingCacheEnabled(true);
//        iv_canvas.buildDrawingCache();
//        bitmap_original = Bitmap.createBitmap(iv_canvas.getDrawingCache());
//        iv_canvas.setDrawingCacheEnabled(false);
////        canvas_original = new Canvas(bitmap_original);
////        canvas_original.drawColor(Color.argb(0,0,0,0));
//        //清空临时位图，保证此图层中只包含当前笔
//        if (bitmap_temp != null){
//            bitmap_temp.eraseColor(Color.argb(0,0,0,0));
//        }
//    }
//
//    /**
//     * 只是将画笔图层和原始图层合并到显示图层上，但是并没有刷新显示
//     */
//    public void updateTempBitmapToScreen(){
//        //清空显示图层
//        bitmap_show.eraseColor(0);
//        Log.e(TAG, "显示层被清空");
//        //将原始图层绘制到base图层上
//        canvas_show.drawBitmap(bitmap_original, 0, 0, tempPaint);
//        Log.e(TAG, "显示层被贴上原始图层");
//        //将临时图层绘制在base图层上
//        canvas_show.drawBitmap(bitmap_temp, 0, 0, mBitPaint);
//        Log.e(TAG, "显示层被贴上画笔图层");
//    }
//
//
//
//    /**
//     * 实验getPixels和setPixels两个函数的使用
//     * @param motionEvent
//     */
//    private void test_getPixels_and_setPixels(MotionEvent motionEvent) {
//        int[] pixels = new int[mBitWidth * mBitHeight];
//        mBitmap.getPixels(pixels, 0, mBitWidth, 0, 0, mBitWidth, mBitHeight);
//        mBitmap.setPixels(pixels, 0, mBitWidth, 0, 0, mBitWidth, mBitHeight);
//        canvas_show.drawBitmap(mBitmap, (int) motionEvent.getX(), (int) motionEvent.getY(), mBitPaint);
//        iv_canvas.setImageBitmap(bitmap_show);
//    }
//
//
//    /**
//     * 计算canvas.drawBitmap执行速率
//     */
//    private void test_rate() {
//        Long timeBegin = System.currentTimeMillis();
//        int x, y;
//        int z = 0;
//
//        for(y = 100; y<800; y+=7){
//            for(x=50; x<750; x+=5){
////                canvas_show.drawBitmap(mBitmap, x, y, mBitPaint);
//                //将模板绘制在临时图层上
//                tempCanvas.drawBitmap(bitmapTemplate, x, y, tempPaint);
////                if(z%20 == 19){
////                    //清空显示图层
////                    bitmap_show.eraseColor(0);
////                    //将原始图层绘制到base图层上
////                    canvas_show.drawBitmap(bitmap_original, 0, 0, tempPaint);
////                    //将临时图层绘制在base图层上
////                    canvas_show.drawBitmap(bitmap_temp, 0, 0, mBitPaint);
////
////                    //将绘制后的显示图层更新到屏幕上
////                    iv_canvas.setImageBitmap(bitmap_show);
////                }
////                z++;
//
//            }
//        }
//        Log.i("wwl","更新前timeCast = " + (System.currentTimeMillis()-timeBegin));
//        //清空显示图层
//        bitmap_show.eraseColor(0);
//        //将原始图层绘制到base图层上
//        canvas_show.drawBitmap(bitmap_original, 0, 0, tempPaint);
//        //将临时图层绘制在base图层上
//        canvas_show.drawBitmap(bitmap_temp, 0, 0, mBitPaint);
//
//        //将绘制后的显示图层更新到屏幕上
//        iv_canvas.setImageBitmap(bitmap_show);
//        Log.i("wwl","timeCast = " + (System.currentTimeMillis()-timeBegin));
//    }
//
//
//    private void test_rate1() {
//        Long timeBegin = System.currentTimeMillis();
//        int x, y;
//        for(x = 50, y = 100; y<800; y++,x++){
//                tempCanvas.drawBitmap(bitmapTemplate, x, y, tempPaint);
//        }
//        Log.i("wwl","融合前timeCast = " + (System.currentTimeMillis()-timeBegin));
//        //清空显示图层
//        bitmap_show.eraseColor(0);
//        //将原始图层绘制到base图层上
//        canvas_show.drawBitmap(bitmap_original, 0, 0, tempPaint);
//        //将临时图层绘制在base图层上
//        canvas_show.drawBitmap(bitmap_temp, 0, 0, mBitPaint);
//        Log.i("wwl","更新前timeCast = " + (System.currentTimeMillis()-timeBegin));
//        //将绘制后的显示图层更新到屏幕上
//        iv_canvas.setImageBitmap(bitmap_show);
//        Log.i("wwl","timeCast = " + (System.currentTimeMillis()-timeBegin));
//    }
//
//
//
//
//
//
//
//
//
//
//}
