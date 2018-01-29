package com.hanvon.canvasdemo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.canvasdemo.R;
import com.hanvon.canvasdemo.Utils.LogUtil;
import com.hanvon.canvasdemo.beans.Stroke;
import com.hanvon.canvasdemo.beans.Template;
import com.hanvon.canvasdemo.engine.HwPenEngine;
import com.hanvon.canvasdemo.view.SurfaceViewL;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = "FirstActivity";

    private int mTotalWidth, mTotalHeight;
    private HwPenEngine hwPen;
    private int penBtnClickNum = 1;
    private int eraserBtnClickNum = 0;
    private long engine = -1;
    private Template template;
    private int penType, penWidth, penColor, penAlpha;
    private boolean penIsBeautify, penIsTransparent;
    LinkedList<Stroke> list_strokes;
    private SurfaceViewL mSurfaceView;
//    private ProgressBar progressBar;
    private ProgressDialog pd;

    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: setContentView之前");

        setContentView(R.layout.activity_main);
//        setContentView(new SurfaceViewL(this));
        mSurfaceView = (SurfaceViewL) findViewById(R.id.strokeView1);
//        progressBar = (ProgressBar)findViewById(R.id.pb);
        pd = new ProgressDialog(this);
        pd.setTitle("提示");
        pd.setMessage("正在进行保存，请稍等");
        pd.setCancelable(false);
        if(ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FirstActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (hwPen == null){
            hwPen = mSurfaceView.getPenEngine();
        }

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        hwPen.getNewScreen();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (hwPen == null){
            hwPen = mSurfaceView.getPenEngine();
        }
        switch (item.getItemId()){
            case R.id.item_pen:
                penBtnClickNum++;
                switchPenType(penBtnClickNum);
//                hwPen.getNewScreen();

                break;
            case R.id.item_eraser:
                eraserBtnClickNum++;
                if (eraserBtnClickNum % 2 == 0){
                    hwPen.setEraserType(HwPenEngine.PEN_TYPE_ERASER_FOR_STROKE, 35);
                    mSurfaceView.penType = HwPenEngine.PEN_TYPE_ERASER_FOR_STROKE;
                }
                else if (eraserBtnClickNum % 2 == 1){
                    hwPen.setEraserType(HwPenEngine.PEN_TYPE_ERASER_FOR_POINT, 35);
                    mSurfaceView.penType = HwPenEngine.PEN_TYPE_ERASER_FOR_POINT;
                }

                break;
            case R.id.item_pre:
                if (hwPen.getUndoSteps() > 0){
                    long time0 = System.currentTimeMillis();
                    hwPen.undo();
                    mSurfaceView.update();
//                    Toast.makeText(FirstActivity.this, (System.currentTimeMillis() - time0) + "ms", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.item_next:
                if (hwPen.getRedoSteps() > 0){
                    hwPen.redo();
                    mSurfaceView.update();
                }
                break;
            case R.id.save:
                //设置一个dialog
                pd.setTitle("提示");
                pd.setMessage("正在进行保存，请稍等");
                pd.show();

                //保存
                try {
                    hwPen.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                singleThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean isOver = false;
                        while (!isOver){
                            if (hwPen.isFinished){
                                hwPen.isFinished = false;
                                mSurfaceView.clearScreen();
//                                mSurfaceView.mainHandler.sendEmptyMessage(mSurfaceView.MSG_CLEARSCREEN);
                                pd.dismiss();
                                isOver = true;
                            }
                        }
                    }
                });

                break;
            case R.id.setting:
                Toast.makeText(FirstActivity.this, "点击了设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear:
                mSurfaceView.clear();
                break;
            case R.id.load:
                //设置一个dialog
                pd.setTitle("提示");
                pd.setMessage("正在进行加载，请稍等");
                pd.show();

                //加载
                try {
                    hwPen.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                singleThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean isOver = false;
                        while (!isOver){
                            if (hwPen.isFinished){
                                hwPen.isFinished = false;
                                LogUtil.e(TAG, "width = " + mSurfaceView.getmWidth());
                                LogUtil.e(TAG, "height = " + mSurfaceView.getHeight());
                                mSurfaceView.update();
//                                mSurfaceView.mainHandler.sendEmptyMessage(mSurfaceView.MSG_UPDATESCREEN);
                                pd.dismiss();
                                isOver = true;
                            }
                        }
                    }
                });

                break;
            case R.id.changePage:
                Intent intent = new Intent();
                intent.setClass(FirstActivity.this, ThirdActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }

    private void switchPenType(int num) {
        if (hwPen == null){
            hwPen = mSurfaceView.getPenEngine();
        }
        if(num%7 == 1) {
            hwPen.setPenInfo(0, HwPenEngine.PEN_TYPE_MARKER, 0x80FFE533, 45, 0);
            mSurfaceView.penType = HwPenEngine.PEN_TYPE_MARKER;
        }else if(num%7 == 2){
            hwPen.setPenInfo(0, HwPenEngine.PEN_TYPE_FOUNTAIN1, 0xe0000000, 30, 0);
            mSurfaceView.penType = HwPenEngine.PEN_TYPE_FOUNTAIN1;
        }else if(num%7 == 3){
            hwPen.setPenInfo(0, HwPenEngine.PEN_TYPE_FOUNTAIN2, 0xe0000000, 15, 0);
            mSurfaceView.penType = HwPenEngine.PEN_TYPE_FOUNTAIN2;
        }else if(num%7 == 4){
            hwPen.setPenInfo(0, HwPenEngine.PEN_TYPE_PAINTBRUSH, 0xff808080, 45, 0);
            mSurfaceView.penType = HwPenEngine.PEN_TYPE_PAINTBRUSH;
        }else if(num%7 == 5){
            hwPen.setPenInfo(0, HwPenEngine.PEN_TYPE_INK, 0xe0000000, 45, 0);
            mSurfaceView.penType = HwPenEngine.PEN_TYPE_INK;
        }else if(num%7 == 6){
            hwPen.setPenInfo(0, HwPenEngine.PEN_TYPE_PENCIL, 0xff303030, 30, 0);
            mSurfaceView.penType = HwPenEngine.PEN_TYPE_PENCIL;
        }else{
            hwPen.setPenInfo(0, HwPenEngine.PEN_TYPE_CORRECTION, 0xe0800000, 50, 0);
            mSurfaceView.penType = HwPenEngine.PEN_TYPE_CORRECTION;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hwPen != null){
            hwPen.destroy();
            hwPen = null;
        }

    }
}
