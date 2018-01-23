package com.hanvon.canvasdemo.beans;

/**
 * Created by pc on 2017/11/8.
 */

public class Pen {
    private int type;
    private Template template;
    private int width;
    private int color;
    private boolean isBeautify;
    private boolean isTransparent;
    private int alpha;

    public Pen(){};

    public Pen(int type, Template template, int width, int color) {
        this.type = type;
        this.template = template;
        this.width = width;
        this.color = color;
    }

    public Pen(int type, Template template, int width, int color, boolean isBeautify, boolean isTransparent, int alpha) {
        this.type = type;
        this.template = template;
        this.width = width;
        this.color = color;
        this.isBeautify = isBeautify;
        this.isTransparent = isTransparent;
        this.alpha = alpha;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isBeautify() {
        return isBeautify;
    }

    public void setBeautify(boolean beautify) {
        isBeautify = beautify;
    }

    public boolean isTransparent() {
        return isTransparent;
    }

    public void setTransparent(boolean transparent) {
        isTransparent = transparent;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }
}
