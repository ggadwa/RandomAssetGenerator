package com.klinksoftware.rag.walkview;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WalkViewKeyListener implements KeyListener {

    private static final float RAG_MOVE_SPEED = 0.2f;
    private static final float RAG_SPEED_MULTIPLIER = 3.0f;

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
                view.speedMultiplier = RAG_SPEED_MULTIPLIER;
                return;
            case KeyEvent.VK_W:
                view.moveZ = RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_S:
                view.moveZ = -RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_A:
                view.moveX = RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_D:
                view.moveX = -RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_Q:
                view.moveY = RAG_MOVE_SPEED;
                return;
            case KeyEvent.VK_E:
                view.moveY = -RAG_MOVE_SPEED;
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
                view.speedMultiplier = 1.0f;
                return;
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
                view.moveZ = 0.0f;
                return;
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                view.moveX = 0.0f;
                return;
            case KeyEvent.VK_Q:
            case KeyEvent.VK_E:
                view.moveY = 0.0f;
                return;
        }
    }

}