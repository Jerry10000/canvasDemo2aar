package com.hanvon.canvasdemo.beans;

import java.util.List;

/**
 * Created by pc on 2017/12/5.
 */

public class Stack_stroke {
    private List<Integer> strokeIDs;
    private boolean isVisible;

    public List<Integer> getStrokeIDs() {
        return strokeIDs;
    }

    public void setStrokeIDs(List<Integer> strokeIDs) {
        this.strokeIDs = strokeIDs;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
