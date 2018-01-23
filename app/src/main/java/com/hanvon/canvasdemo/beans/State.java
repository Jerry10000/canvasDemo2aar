package com.hanvon.canvasdemo.beans;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pc on 2017/11/5.
 */

public class State {
    private List<Integer> strokes;
    private int layer;
    private boolean isVisible;

    public State() {
        this.strokes = new LinkedList<Integer>();
    }

    public List<Integer> getStrokes() {
        return strokes;
    }

    public void setStrokes(List<Integer> strokes) {
        this.strokes = strokes;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
