package com.klinksoftware.rag;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Timer;
import javax.swing.JComponent;

public class SpinnerPane extends JComponent {

    private final static int SPINNER_SIZE = 40;
    private final static long SPINNER_SPEED = 10L;
    private final static int SPINNER_OFFSET = 50;
    private final static int SPINNER_REFRESH_MILLISEC = 50;

    private int ang;
    private Timer timer;

    public void start() {
        ang = 0;

        timer = new Timer(SPINNER_REFRESH_MILLISEC, e -> repaint());
        timer.setRepeats(true);
        timer.start();

        setVisible(true);
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        setVisible(false);
    }

    protected void paintComponent(Graphics g) {
        int width, height;
        Component parentWindow;

        parentWindow = getParent();
        width = parentWindow.getWidth();
        height = parentWindow.getHeight();

        ang = (int) ((System.currentTimeMillis() / SPINNER_SPEED) % 360L);

        g.setColor(Color.red);
        g.fillArc((width - SPINNER_OFFSET), (height - SPINNER_OFFSET), SPINNER_SIZE, SPINNER_SIZE, ang, 120);

        g.setColor(Color.green);
        g.fillArc((width - SPINNER_OFFSET), (height - SPINNER_OFFSET), SPINNER_SIZE, SPINNER_SIZE, (ang + 120), 120);

        g.setColor(Color.blue);
        g.fillArc((width - SPINNER_OFFSET), (height - SPINNER_OFFSET), SPINNER_SIZE, SPINNER_SIZE, (ang + 240), 120);
    }
}
