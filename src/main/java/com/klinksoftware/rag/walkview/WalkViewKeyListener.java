package com.klinksoftware.rag.walkview;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WalkViewKeyListener implements KeyListener {
    private WalkView view;
    private WalkViewMouseMotionListener mouseMotionListener;

    public WalkViewKeyListener(WalkView view, WalkViewMouseMotionListener mouseMotionListener) {
        this.view = view;
        this.mouseMotionListener = mouseMotionListener;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                view.physics.speedFast = true;
                return;
            case KeyEvent.VK_W:
                view.physics.moveVector.z = 1.0f;
                return;
            case KeyEvent.VK_S:
                view.physics.moveVector.z = -1.0f;
                return;
            case KeyEvent.VK_A:
                view.physics.moveVector.x = 1.0f;
                return;
            case KeyEvent.VK_D:
                view.physics.moveVector.x = -1.0f;
                return;
            case KeyEvent.VK_Q:
                view.physics.moveVector.y = 1.0f;
                return;
            case KeyEvent.VK_E:
                view.physics.moveVector.y = -1.0f;
                return;
            case KeyEvent.VK_ESCAPE:
                mouseMotionListener.turnMotionOff();
                return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                view.physics.speedFast = false;
                return;
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
                view.physics.moveVector.z = 0.0f;
                return;
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                view.physics.moveVector.x = 0.0f;
                return;
            case KeyEvent.VK_Q:
            case KeyEvent.VK_E:
                view.physics.moveVector.y = 0.0f;
                return;
        }
    }

}
