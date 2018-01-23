package com.hanvon.canvasdemo.beans;

import android.graphics.Rect;

import java.util.LinkedList;

/**
 * Created by pc on 2017/12/5.
 */

public class Stroke1 {
    private int strokeID;
    private int[] pointPixels;
    private Rect rect_big;
    private LinkedList<Rect> rects;
    private boolean isVisible;
    private int penType;

    public int getStrokeID() {
        return strokeID;
    }

    public void setStrokeID(int strokeID) {
        this.strokeID = strokeID;
    }

    public int[] getPointPixels() {
        return pointPixels;
    }

    public void setPointPixels(int[] pointPixels) {
        this.pointPixels = pointPixels;
    }

    public Rect getRect_big() {
        return rect_big;
    }

    public void setRect_big(Rect rect_big) {
        this.rect_big = rect_big;
    }

    public LinkedList<Rect> getRects() {
        return rects;
    }

    public void setRects(LinkedList<Rect> rects) {
        this.rects = rects;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public int getPenType() {
        return penType;
    }

    public void setPenType(int penType) {
        this.penType = penType;
    }
}
