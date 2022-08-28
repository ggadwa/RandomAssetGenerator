package com.klinksoftware.rag.utility;

public class RagRect {

    private int lft, top, rgt, bot;

    public RagRect(int lft, int top, int rgt, int bot) {
        this.lft = lft;
        this.top = top;
        this.rgt = rgt;
        this.bot = bot;
    }

    public int getLeft() {
        return (lft);
    }

    public int getTop() {
        return (top);
    }

    public int getRight() {
        return (rgt);
    }

    public int getBottom() {
        return (bot);
    }

}
