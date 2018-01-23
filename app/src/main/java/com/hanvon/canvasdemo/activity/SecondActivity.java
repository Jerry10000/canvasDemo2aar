package com.hanvon.canvasdemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hanvon.canvasdemo.R;
import com.hanvon.canvasdemo.beans.HWPen;
import com.hanvon.canvasdemo.beans.Stroke;
import com.hanvon.canvasdemo.beans.Template;
import com.hanvon.canvasdemo.constants.GrayCircle;

import java.util.LinkedList;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    private ImageView iv_canvas;
    private int mTotalWidth, mTotalHeight;
    private HWPen hwPen;
    private Template template;
    private int penType, penWidth, penColor, penAlpha;
    private boolean penIsBeautify, penIsTransparent;
    LinkedList<Stroke> list_strokes;
    private Button Btn_pre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        penType = HWPen.TYPE_PENCIL;
        penWidth = 10;
        penColor = 0xff0000;
        penAlpha = 125;
        penIsBeautify = false;
        penIsTransparent = false;
        template = new Template();
        template.setData(GrayCircle.gray_circle);
        template.setWidth(GrayCircle.width);
        template.setHeight(GrayCircle.height);

        setContentView(R.layout.activity_second);
        iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
        iv_canvas.setOnTouchListener(touch1);
        Btn_pre = (Button) findViewById(R.id.Btn_pre);
        Btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SecondActivity.this, FirstActivity.class);
                startActivity(intent);
            }
        });

        if(ContextCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            initHWPen();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    private void initHWPen(){
        mTotalWidth = iv_canvas.getWidth();
        mTotalHeight = iv_canvas.getHeight();
        if (hwPen == null){
//            hwPen = HWPen.getInstence();
            hwPen = new HWPen();
            hwPen.init(SecondActivity.this, mTotalWidth, mTotalHeight, penType, template, penWidth, penColor, penIsBeautify, penIsTransparent, penAlpha);
        }
    }

    private View.OnTouchListener touch1 = new View.OnTouchListener() {
        float xOld, yOld, xNew, yNew;
        Bundle bundle = new Bundle();
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    xOld = motionEvent.getX();
                    yOld = motionEvent.getY();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
////                            checkBitmapValid();
//                            //传递当前位图位图
//                            iv_canvas.setDrawingCacheEnabled(true);
//                            iv_canvas.buildDrawingCache();
//                            hwPen.setOriginalBitmap(Bitmap.createBitmap(iv_canvas.getDrawingCache()));
//                            iv_canvas.setDrawingCacheEnabled(false);
//                        }
//                    }).start();
                    hwPen.setOriginalBitmap(null);

                    break;
                case MotionEvent.ACTION_MOVE:
                    xNew = motionEvent.getX();
                    yNew = motionEvent.getY();
                    hwPen.drawLiner(iv_canvas, xOld, yOld, xNew, yNew);
                    xOld = xNew;
                    yOld = yNew;
                    break;
                case MotionEvent.ACTION_UP:

                    break;
                default:
            }
            return true;
        }
    };





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_pen:
                hwPen.getPen().setType(HWPen.TYPE_PENCIL);
                break;
            case R.id.item_eraser:
                hwPen.getPen().setType(HWPen.TYPE_ERASER_BY_LINE);
                break;
            case R.id.item_pre:
                if (hwPen.isUndoAble()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            hwPen.undo();
                        }
                    }).start();
                }
                break;
            case R.id.item_next:
                if (hwPen.isRedoAble()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            hwPen.redo();
                        }
                    }).start();
                }
                break;
            case R.id.save:

                long begin0 = System.currentTimeMillis();
                hwPen.save();
                long end0 = System.currentTimeMillis();
//                Toast.makeText(FirstActivity.this, "save:" + (end0 - begin0) + " 豪秒", Toast.LENGTH_SHORT).show();

                break;
            case R.id.setting:
                Toast.makeText(SecondActivity.this, "点击了设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.clear:
                hwPen.clear();
                break;
            case R.id.load:
                begin0 = System.currentTimeMillis();
                hwPen.load();
                end0 = System.currentTimeMillis();
                Toast.makeText(SecondActivity.this, "load:" + (end0 - begin0) + " 豪秒", Toast.LENGTH_SHORT).show();

                break;
            default:
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hwPen.recycle();
        hwPen = null;
    }
}
