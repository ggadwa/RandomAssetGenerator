package com.klinksoftware.rag.walkview;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

public class WalkViewMouseMotionListener implements MouseMotionListener {

    private int lastMouseX, lastMouseY;
    private boolean movementOn;
    private WalkView view;
    private Cursor blankCursor;
    private Robot robot;

    public WalkViewMouseMotionListener(WalkView view) {
        this.view = view;

        movementOn = false;

        // custom blank cursor
        blankCursor = view.getToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                new Point(),
                null);

        // robot for relative motion centering
        try {
            robot = new Robot();
        } catch (AWTException e) {
            robot = null;
        }
    }

    public void turnMotionOn() {
        movementOn = true;
        view.setCursor(blankCursor);
        resetMotion();
    }

    public void turnMotionOff() {
        movementOn = false;
        view.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        resetMotion();
    }

    public void switchState() {
        if (!movementOn) {
            turnMotionOn();
        } else {
            turnMotionOff();
        }
    }

    public void resetMotion() {
        Point pnt;

        if (robot != null) {
            lastMouseX = view.wid / 2;
            lastMouseY = view.high / 2;

            pnt = new Point(lastMouseX, lastMouseY);
            SwingUtilities.convertPointToScreen(pnt, view);
            robot.mouseMove(pnt.x, pnt.y);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x, y;

        if (!movementOn) {
            return;
        }

        x = e.getX();
        y = e.getY();

        // get the relative movement
        if (lastMouseX != x) {
            view.cameraAngle.y -= ((float) (x - lastMouseX) * 0.5f);
            if (view.cameraAngle.y < 0) {
                view.cameraAngle.y = 360.0f + view.cameraAngle.y;
            }
            if (view.cameraAngle.y >= 360) {
                view.cameraAngle.y = view.cameraAngle.y - 360.0f;
            }
            lastMouseX = x;
        }

        if (lastMouseY != y) {
            view.cameraAngle.x += ((float) (y - lastMouseY) * 0.2f);
            if (view.cameraAngle.x < -89.0f) {
                view.cameraAngle.x = -89.0f;
            }
            if (view.cameraAngle.x > 89.0f) {
                view.cameraAngle.x = 89.0f;
            }
            lastMouseY = y;
        }

        // recenter for relative movement
        resetMotion();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

}
