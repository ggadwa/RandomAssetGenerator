package com.klinksoftware.rag;

import java.awt.*;
import java.net.*;
import javassist.bytecode.stackmap.TypeData.ClassName;
import javax.swing.*;

public class RandomAssetGenerator {

    public static void main(String[] args) {
        URL iconURL;
        Image image;
        AppWindow appWindow;

        // window icon
        iconURL = ClassName.class.getResource("/Graphics/icon.png");
        image = new ImageIcon(iconURL).getImage();

        // look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Taskbar.getTaskbar().setIconImage(image);
        } catch (Exception e) {
        }

        appWindow = new AppWindow(image);
    }
}
