package com.hanvon.canvasdemo.beans;

import org.litepal.crud.DataSupport;

/**
 * Created by pc on 2017/10/27.
 */

public class Point extends DataSupport{
    private  int strokeID;
    private float x;
    private float y;
    private int pressure;

    public Point(){}

    public Point(int strokeID, float x, float y) {
        this.strokeID = strokeID;
        this.x = x;
        this.y = y;
    }

    public int getStrokeID() {
        return strokeID;
    }

    public void setStrokeID(int strokeID) {
        this.strokeID = strokeID;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }
}
