package com.klinksoftware.rag.walkview;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class WalkViewMouseListener implements MouseListener {

    private final WalkViewMouseMotionListener mouseMotionListener;

    public WalkViewMouseListener(WalkViewMouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseMotionListener.switchState();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
