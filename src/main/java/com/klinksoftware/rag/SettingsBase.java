package com.klinksoftware.rag;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SettingsBase extends JPanel {

    protected static final int PANEL_MIDPOINT = AppWindow.SETTING_WIDTH / 2;
    protected static final int ROW_HEIGHT = 25;
    protected static final int ROW_GAP = 2;

    protected final AppWindow appWindow;

    public SettingsBase(AppWindow appWindow) {
        this.appWindow = appWindow;
    }

    protected JButton addButton(int y, String title, int buttonId) {
        JButton button;

        button = new JButton(title);
        button.setBackground(new Color(0.4f, 0.4f, 1.0f));
        button.addActionListener(e -> buttonClick(buttonId));
        add(button);
        button.setBounds(5, (y + 5), ((PANEL_MIDPOINT * 2) - 15), ROW_HEIGHT);

        return (button);
    }

    protected JCheckBox addCheckBox(int y, String title, boolean checked) {
        JLabel label;
        JCheckBox checkBox;

        label = new JLabel(title + ":");
        label.setHorizontalAlignment(JLabel.RIGHT);
        add(label);
        label.setBounds(5, (y + 5), (PANEL_MIDPOINT - 10), ROW_HEIGHT);

        checkBox = new JCheckBox("", checked);
        add(checkBox);
        checkBox.setBounds(PANEL_MIDPOINT, (y + 5), (PANEL_MIDPOINT - 10), ROW_HEIGHT);

        return (checkBox);
    }

    protected JComboBox addComboBox(int y, String title, String[] items, int selectedIndex) {
        JLabel label;
        JComboBox comboxBox;

        label = new JLabel(title + ":");
        label.setHorizontalAlignment(JLabel.RIGHT);
        add(label);
        label.setBounds(5, (y + 5), (PANEL_MIDPOINT - 10), ROW_HEIGHT);

        comboxBox = new JComboBox(items);
        comboxBox.setSelectedIndex(selectedIndex);
        add(comboxBox);
        comboxBox.setBounds(PANEL_MIDPOINT, (y + 5), (PANEL_MIDPOINT - 10), ROW_HEIGHT);

        return (comboxBox);
    }

    public void enableAll(boolean enable) {
        for (Component component : getComponents()) {
            component.setEnabled(enable);
        }
    }

    public void buttonClick(int id) {
    }

}
