package com.hanvon.canvasdemo.beans;


import android.graphics.Rect;
import java.util.LinkedList;

/**
 * Created by pc on 2017/10/27.
 */

public class Stroke{
    private int strokeID;
    private int currentLayer;
    private LinkedList<Point> points;//绘制所用的点
    private LinkedList<Point> points_part;//采集的点
    private Rect rect_big;
    private LinkedList<Rect> rects;
    private boolean isVisible;
    private Pen pen;

    public int getStrokeID() {
        return strokeID;
    }

    public void setStrokeID(int strokeID) {
        this.strokeID = strokeID;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
    }

    public LinkedList<Point> getPoints() {
        return points;
    }

    public void setPoints(LinkedList<Point> points) {
        this.points = points;
    }

    public LinkedList<Point> getPoints_part() {
        return points_part;
    }

    public void setPoints_part(LinkedList<Point> points_part) {
        this.points_part = points_part;
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

    public Pen getPen() {
        return pen;
    }

    public void setPen(Pen pen) {
        this.pen = pen;
    }
}
