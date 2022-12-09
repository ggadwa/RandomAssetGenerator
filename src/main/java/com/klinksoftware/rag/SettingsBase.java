package com.klinksoftware.rag;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class SettingsBase extends JPanel {

    protected static final int FIELD_LEFT = (int) ((float) AppWindow.SETTING_WIDTH * 0.4f);
    protected static final int FIELD_SIZE = (AppWindow.SETTING_WIDTH - 10) - FIELD_LEFT;
    protected static final int ROW_HEIGHT = 25;
    protected static final int ROW_LIST_HEIGHT = 515;
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

    protected JList addList(int y, String title, ArrayList<String> items, int selectedIndex) {
        JScrollPane scroll;
        JList<String> list;

        list = new JList<>(items.toArray(new String[0]));
        list.setSelectedIndex(selectedIndex);

        scroll = new JScrollPane(list);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll);
        scroll.setBounds(5, (y + 5), (AppWindow.SETTING_WIDTH - 15), ROW_LIST_HEIGHT);

        return (list);
    }

    public void enableAll(boolean enable) {
        for (Component component : getComponents()) {
            component.setEnabled(enable);
        }
    }

    public void buttonClick(int id) {
    }

    private String mangleClassNameForDisplay(Class cls, String prefix) {
        String className, displayName;

        className = cls.getSimpleName().substring(prefix.length());

        displayName = "";

        for (Character ch : className.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                if (!displayName.isEmpty()) {
                    displayName += " ";
                }
            }
            displayName += ch;
        }

        return (displayName);
    }

    protected String demangleDisplayNameForClass(JList<String> list, String prefix) {
        return (prefix + (((String) list.getModel().getElementAt(list.getSelectedIndex())).replace(" ", "")));
    }

    protected ArrayList<String> getAnnotationClasses(String packagePath, String prefix, Class annotationClass) {
        ArrayList<String> items;
        Reflections reflections;
        Set<Class<?>> classes;

        setLayout(null);

        // get all the bitmaps
        reflections = new Reflections(packagePath, Scanners.TypesAnnotated);
        classes = reflections.getTypesAnnotatedWith(annotationClass);

        items = new ArrayList<>();
        for (Class cls : classes) {
            items.add(mangleClassNameForDisplay(cls, prefix));
        }

        items.sort(null);

        return (items);
    }
}
