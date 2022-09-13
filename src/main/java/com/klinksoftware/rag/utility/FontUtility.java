package com.klinksoftware.rag.utility;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class FontUtility {

    private static final int FONT_SIZE = 18;
    private static final int CHAR_SIZE = 20;
    private static final int CHAR_CODE_START = 48;
    private static final int CHAR_CODE_END = 122;

    private static float[] fontData;

    public static void initialize() {
        int n, rgb, charCount, wid, len, fIdx;
        BufferedImage fontImg;
        Graphics2D g2d;
        DataBuffer buf;

        charCount = CHAR_CODE_END - CHAR_CODE_START;
        wid = CHAR_SIZE * charCount;

        // draw the graphic
        fontImg = new BufferedImage(wid, CHAR_SIZE, BufferedImage.TYPE_INT_RGB);
        g2d = (Graphics2D) fontImg.getGraphics();

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, wid, CHAR_SIZE);

        g2d.setColor(Color.white);
        g2d.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE));

        for (n = 0; n != charCount; n++) {
            g2d.drawString(Character.toString(n + CHAR_CODE_START), (n * CHAR_SIZE), (CHAR_SIZE - (CHAR_SIZE - FONT_SIZE)));
        }

        // get the bytes
        len = wid * CHAR_SIZE;
        fontData = new float[len * 4];
        buf = fontImg.getData().getDataBuffer();

        fIdx = 0;

        for (n = 0; n != len; n++) {
            rgb = buf.getElem(n);

            fontData[fIdx++] = ((float) ((rgb >> 16) & 0xFF)) / 255.0f;
            fontData[fIdx++] = ((float) ((rgb >> 8) & 0xFF)) / 255.0f;
            fontData[fIdx++] = ((float) (rgb & 0xFF)) / 255.0f;
            fIdx++;
        }
    }
}
