package com.hanvon.canvasdemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hanvon.canvasdemo.R;
import com.hanvon.canvasdemo.beans.Stroke;
import com.hanvon.canvasdemo.beans.Template;
import com.hanvon.canvasdemo.engine.HwPenEngine;
import com.hanvon.canvasdemo.view.SurfaceViewL2;

import java.io.IOException;
import java.util.LinkedList;

public class ThirdActivity2 extends AppCompatActivity {
    private static final String TAG = "FirstActivity";

    private int mTotalWidth, mTotalHeight;
    private HwPenEngine hwPen;
    private long engine = -1;
    private Template template;
    private int penType, penWidth, penColor, penAlpha;
    private boolean penIsBeautify, penIsTransparent;
    LinkedList<Stroke> list_strokes;
    private SurfaceViewL2 mSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: setContentView之前");

        setContentView(R.layout.activity_third);
//        setContentView(new SurfaceViewL(this));
        mSurfaceView = (SurfaceViewL2) findViewById(R.id.strokeView1);

        if(ContextCompat.checkSelfPermission(ThirdActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ThirdActivity2.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

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
                hwPen.setPenInfo(0, HwPenEngine.PEN_TYPE_INK, 0x80000000, 45, 0);
                break;
            case R.id.item_eraser:
                hwPen.setEraserType(HwPenEngine.PEN_TYPE_ERASER_FOR_STROKE, 45);
                break;
            case R.id.item_pre:
                if (hwPen.getUndoSteps() > 0){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            hwPen.undo();
                            mSurfaceView.update(new Rect(0, 0, mSurfaceView.getmWidth(), mSurfaceView.getHeight()));
                        }
                    }).start();
                }
                break;
            case R.id.item_next:
                if (hwPen.getRedoSteps() > 0){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            hwPen.redo();
                            mSurfaceView.update(new Rect(0, 0, mSurfaceView.getmWidth(), mSurfaceView.getHeight()));
                        }
                    }).start();
                }
                break;
            case R.id.save:

                long begin0 = System.currentTimeMillis();
                try {
                    hwPen.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long end0 = System.currentTimeMillis();
//                Toast.makeText(FirstActivity.this, "save:" + (end0 - begin0) + " 豪秒", Toast.LENGTH_SHORT).show();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final String log = getLog();
//                    }
//                }).start();
                Toast.makeText(ThirdActivity2.this, "点击了保存", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Toast.makeText(ThirdActivity2.this, "点击了设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear:
                mSurfaceView.clear();
                break;
            case R.id.load:
                begin0 = System.currentTimeMillis();
                try {
                    hwPen.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSurfaceView.update(new Rect(0, 0, mSurfaceView.getmWidth(), mSurfaceView.getHeight()));
                end0 = System.currentTimeMillis();
                Toast.makeText(ThirdActivity2.this, "load:" + (end0 - begin0) + " 豪秒", Toast.LENGTH_SHORT).show();
                break;
            case R.id.changePage:
                Intent intent = new Intent();
                intent.setClass(ThirdActivity2.this, FirstActivity.class);
                startActivity(intent);
                break;
            default:
        }
        return true;
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
