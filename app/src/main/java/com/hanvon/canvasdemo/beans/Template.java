package com.hanvon.canvasdemo.beans;


import org.litepal.crud.DataSupport;

/**
 * Created by pc on 2017/11/2.
 */

public class Template extends DataSupport{
    private int width;
    private int height;
//    private float x;    //左上角X坐标
//    private float y;    //左上角Y坐标
    private int[] data;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

//    public float getX() {
//        return x;
//    }
//
//    public void setX(float x) {
//        this.x = x;
//    }
//
//    public float getY() {
//        return y;
//    }
//
//    public void setY(float y) {
//        this.y = y;
//    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }
}
