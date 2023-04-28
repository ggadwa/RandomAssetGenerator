package com.klinksoftware.rag;

import static com.klinksoftware.rag.AppWindow.TOOLBAR_HEIGHT;
import static com.klinksoftware.rag.AppWindow.WINDOW_WIDTH;
import com.klinksoftware.rag.walkview.WalkView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeListener;

public class ToolBar extends JToolBar {

    public static final int SLIDER_WIDTH = 150;

    private JToggleButton flyButton;
    private JToggleButton renderButton, colorButton, normalButton, MetallicRoughnessButton, emissiveButton, skeletonButton;
    private JSlider lightIntensitySlider, lightExponentSlider, lightAmbientSlider;

    public ToolBar() {
        ButtonGroup bg;

        setFloatable(false);
        setBounds(0, 0, WINDOW_WIDTH, TOOLBAR_HEIGHT);
        this.setBackground(new Color(0.9f, 0.9f, 1.0f));

        flyButton = addButton("tool_fly", "Fly Mode", false, null, (e) -> {
            AppWindow.walkView.setFlyMode(((JToggleButton) e.getSource()).isSelected());
        });

        addSeparator();

        bg = new ButtonGroup();

        renderButton = addButton("tool_render", "Show Rendering", true, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_RENDER);
        });
        colorButton = addButton("tool_color", "Show Color Only", false, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_COLOR);
        });
        normalButton = addButton("tool_normal", "Show Normal Only", false, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_NORMAL);
        });
        MetallicRoughnessButton = addButton("tool_metal_rough", "Show Metallic-Roughness Only", false, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_METALLIC_ROUGHNESS);
        });
        emissiveButton = addButton("tool_emissive", "Show Emissive Only", false, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_EMISSIVE);
        });
        skeletonButton = addButton("tool_skeleton", "Show Skeleton Only", false, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_SKELETON);
        });

        addSeparator();

        addIcon("tool_light", "Light Intensity");

        lightIntensitySlider = addSlider("Light Intensity", (e) -> {
            AppWindow.walkView.setLightIntensity(((JSlider) e.getSource()).getValue());
        });

        addSeparator();

        addIcon("tool_exponent", "Light Exponent");

        lightExponentSlider = addSlider("Light Exponent", (e) -> {
            AppWindow.walkView.setLightExponent(((JSlider) e.getSource()).getValue());
        });

        addSeparator();

        addIcon("tool_ambient", "Light Ambient");

        lightAmbientSlider = addSlider("Light Ambient", (e) -> {
            AppWindow.walkView.setLightAmbient(((JSlider) e.getSource()).getValue());
        });
    }

    private JToggleButton addButton(String imageName, String toolTipText, boolean highlighted, ButtonGroup bg, ActionListener al) {
        JToggleButton button;

        button = new JToggleButton();
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusable(false);
        button.setIcon(new ImageIcon(getClass().getResource("/graphics/" + imageName + ".png")));
        button.setToolTipText(toolTipText);
        button.addActionListener(al);
        button.setSelected(highlighted);

        if (bg != null) {
            bg.add(button);
        }

        add(button);

        return (button);
    }

    private void addIcon(String imageName, String toolTipText) {
        JLabel label;

        label = new JLabel(new ImageIcon(getClass().getResource("/graphics/" + imageName + ".png")));
        label.setToolTipText(toolTipText);

        add(label);
    }

    private JSlider addSlider(String toolTipText, ChangeListener cl) {
        JSlider slider;

        slider = new JSlider();
        slider.setFocusable(false);
        slider.setToolTipText(toolTipText);
        slider.addChangeListener(cl);
        slider.setPreferredSize(new Dimension(SLIDER_WIDTH, AppWindow.TOOLBAR_HEIGHT));
        slider.setMaximumSize(new Dimension(SLIDER_WIDTH, AppWindow.TOOLBAR_HEIGHT));

        add(slider);

        return (slider);
    }

    public void resetLightSliders(int intensitySlider, int exponentSlider, int ambientSlider) {
        lightIntensitySlider.setValue(intensitySlider);
        lightExponentSlider.setValue(exponentSlider);
        lightAmbientSlider.setValue(ambientSlider);
    }

    public void reset(boolean enableSkeleton) {
        flyButton.setSelected(false);
        renderButton.setSelected(true);
        colorButton.setSelected(false);
        normalButton.setSelected(false);
        MetallicRoughnessButton.setSelected(false);
        emissiveButton.setSelected(false);
        skeletonButton.setSelected(false);

        skeletonButton.setEnabled(enableSkeleton);

        AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_RENDER);
    }

}
