package com.klinksoftware.rag.soundview;

import com.klinksoftware.rag.sound.utility.SoundBase;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class SoundView extends JPanel {

    private static final int WAVE_CLIP_HEIGHT = 150;
    private static final int WAVE_ALL_HEIGHT = 50;
    private static final int WAVE_GAP_HEIGHT = 20;
    private static final int WAVE_MAG_FACTOR = 3;

    private int soundOffset;
    private SoundBase sound;

    public SoundView() {
        sound = null;

        setFocusable(true);

        addMouseListener(
                new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        eventMouseClicked(e.getButton(), e.getX(), e.getY());
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
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
        });
    }

    private void drawSingleWave(Graphics2D g2d, int width, int my, float amplitude, int offset, boolean sizeToFit) {
        int n, x, y, ny, len, skip;
        float[] waveData;

        // wave data
        waveData = sound.getWaveData();
        len = waveData.length;
        skip = sizeToFit ? (len / width) : WAVE_MAG_FACTOR;

        x = 0;
        y = my + (int) (waveData[offset] * amplitude);
        g2d.setColor(Color.GREEN);

        for (n = offset; n < len; n += skip) {
            ny = my + (int) (waveData[n] * amplitude);
            g2d.drawLine(x, y, (x + 1), ny);

            x++;
            if (x > width) {
                break;
            }
            y = ny;
        }

        // the midline
        g2d.setColor(Color.WHITE);
        g2d.drawLine(0, my, width, my);

    }

    @Override
    public void paint(Graphics g) {
        int width, height, len;
        int waveClipMidY, waveClipTopY, waveClipBotY;
        int waveAllMidY, waveAllTopY, waveAllBotY;
        Graphics2D g2d;

        // positions
        g2d = (Graphics2D) g;
        width = (int) getBounds().getWidth();
        height = (int) getBounds().getHeight();

        waveClipTopY = (height - ((WAVE_CLIP_HEIGHT * 2) + WAVE_GAP_HEIGHT + (WAVE_ALL_HEIGHT * 2))) / 2;
        waveClipBotY = waveClipTopY + (WAVE_CLIP_HEIGHT * 2);
        waveClipMidY = (waveClipTopY + waveClipBotY) / 2;

        waveAllTopY = waveClipBotY + WAVE_GAP_HEIGHT;
        waveAllBotY = waveAllTopY + (WAVE_ALL_HEIGHT * 2);
        waveAllMidY = (waveAllTopY + waveAllBotY) / 2;

        // clear
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, waveClipTopY);
        g2d.fillRect(0, waveClipBotY, width, (waveAllTopY - waveClipBotY));
        g2d.fillRect(0, waveAllBotY, width, (height - waveAllBotY));

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, waveClipTopY, width, (waveClipBotY - waveClipTopY));
        g2d.fillRect(0, waveAllTopY, width, (waveAllBotY - waveAllTopY));

        // no sound
        if (sound == null) {
            return;
        }

        // wave data
        drawSingleWave(g2d, width, waveClipMidY, (float) WAVE_CLIP_HEIGHT, soundOffset, false);
        drawSingleWave(g2d, width, waveAllMidY, (float) WAVE_ALL_HEIGHT, 0, true);

        // wave marker
        len = sound.getWaveData().length;

        g2d.setColor(Color.YELLOW);
        g2d.drawRect(((soundOffset * width) / len), (waveAllTopY - 2), (((width * WAVE_MAG_FACTOR) * width) / len), ((waveAllBotY - waveAllTopY) + 4));
    }

    public void setSound(SoundBase sound) {
        this.sound = sound;
        soundOffset = 0;

        repaint();
    }

    public void eventMouseClicked(int button, int x, int y) {
        int width, height;
        int waveClipTopY, waveClipBotY, waveAllTopY, waveAllBotY;

        // positions
        width = (int) getBounds().getWidth();
        height = (int) getBounds().getHeight();

        waveClipTopY = (height - ((WAVE_CLIP_HEIGHT * 2) + WAVE_GAP_HEIGHT + (WAVE_ALL_HEIGHT * 2))) / 2;
        waveClipBotY = waveClipTopY + (WAVE_CLIP_HEIGHT * 2);

        waveAllTopY = waveClipBotY + WAVE_GAP_HEIGHT;
        waveAllBotY = waveAllTopY + (WAVE_ALL_HEIGHT * 2);

        // play click
        if ((y > waveClipTopY) && (y < waveClipBotY)) {
            sound.play();
            return;
        }

        // change offset
        if ((y > waveAllTopY) && (y < waveAllBotY)) {
            soundOffset = ((x * sound.getWaveData().length) / width) - ((width * WAVE_MAG_FACTOR) / 2);
            if (soundOffset < 0) {
                soundOffset = 0;
            }
            repaint();
            return;
        }
    }
}
