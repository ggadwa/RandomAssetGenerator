package com.klinksoftware.rag;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class SettingsBase extends JPanel {

    protected static final int FIELD_LEFT = (int) ((float) AppWindow.SETTING_WIDTH * 0.4f);
    protected static final int FIELD_SIZE = (AppWindow.SETTING_WIDTH - 10) - FIELD_LEFT;
    protected static final int ROW_HEIGHT = 25;
    protected static final int ROW_LIST_HEIGHT = 250;
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
        button.setBounds(5, (y + 5), (AppWindow.SETTING_WIDTH - 15), ROW_HEIGHT);

        return (button);
    }

    protected JCheckBox addCheckBox(int y, String title, boolean checked) {
        JLabel label;
        JCheckBox checkBox;

        label = new JLabel(title + ":");
        label.setHorizontalAlignment(JLabel.RIGHT);
        add(label);
        label.setBounds(5, (y + 5), (FIELD_LEFT - 10), ROW_HEIGHT);

        checkBox = new JCheckBox("", checked);
        add(checkBox);
        checkBox.setBounds(FIELD_LEFT, (y + 5), FIELD_SIZE, ROW_HEIGHT);

        return (checkBox);
    }

    protected JComboBox addComboBox(int y, String title, String[] items, int selectedIndex) {
        JLabel label;
        JComboBox comboxBox;

        label = new JLabel(title + ":");
        label.setHorizontalAlignment(JLabel.RIGHT);
        add(label);
        label.setBounds(5, (y + 5), (FIELD_LEFT - 10), ROW_HEIGHT);

        comboxBox = new JComboBox(items);
        comboxBox.setSelectedIndex(selectedIndex);
        add(comboxBox);
        comboxBox.setBounds(FIELD_LEFT, (y + 5), FIELD_SIZE, ROW_HEIGHT);

        return (comboxBox);
    }

    protected JList addList(int y, String title, ArrayList<String> items, int selectedIndex) {
        JScrollPane scroll;
        JList list;

        list = new JList(items.toArray(new String[0]));
        list.setSelectedIndex(selectedIndex);

        scroll = new JScrollPane(list);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll);
        scroll.setBounds(5, (y + 5), (AppWindow.SETTING_WIDTH - 15), ROW_LIST_HEIGHT);

        return (list);
    }

    protected JSlider addSlider(int y, String title, float value) {
        JLabel label;
        JSlider slider;

        label = new JLabel(title + ":");
        label.setHorizontalAlignment(JLabel.RIGHT);
        add(label);
        label.setBounds(5, (y + 5), (FIELD_LEFT - 10), ROW_HEIGHT);

        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (value * 100.0f));
        slider.setMinorTickSpacing(20);
        slider.setPaintTicks(true);
        add(slider);
        slider.setBounds(FIELD_LEFT, (y + 5), FIELD_SIZE, ROW_HEIGHT);

        return (slider);
    }

    public void enableAll(boolean enable) {
        for (Component component : getComponents()) {
            component.setEnabled(enable);
        }
    }

    public void buttonClick(int id) {
    }

    protected ArrayList<String> getAnnotationClasses(String packagePath, String prefix, Class annotationClass) {
        int prefixLen;
        ArrayList<String> items;
        Reflections reflections;
        Set<Class<?>> classes;

        setLayout(null);

        // get all the bitmaps
        reflections = new Reflections(packagePath, Scanners.TypesAnnotated);
        classes = reflections.getTypesAnnotatedWith(annotationClass);

        prefixLen = prefix.length();

        items = new ArrayList<>();
        for (Class cls : classes) {
            items.add(cls.getSimpleName().substring(prefixLen));
        }

        items.sort(null);

        return (items);
    }

    protected int getIntFromStringCombo(JComboBox combo) {
        return (Integer.parseInt((String) combo.getModel().getElementAt(combo.getSelectedIndex())));
    }

}
