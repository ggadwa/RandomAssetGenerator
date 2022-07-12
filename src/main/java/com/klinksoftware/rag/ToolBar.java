package com.klinksoftware.rag;

import static com.klinksoftware.rag.AppWindow.TOOLBAR_HEIGHT;
import static com.klinksoftware.rag.AppWindow.WINDOW_WIDTH;
import com.klinksoftware.rag.walkview.WalkView;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class ToolBar extends JToolBar {

    private JToggleButton lightButton, colorButton, normalButton, MetallicRoughnessButton, emissiveButton;

    public ToolBar() {
        ButtonGroup bg;

        setFloatable(false);
        setBounds(0, 0, WINDOW_WIDTH, TOOLBAR_HEIGHT);
        this.setBackground(new Color(0.9f, 0.9f, 1.0f));

        lightButton = addButton("tool_light", "Min or Max Light", false, null, (e) -> {
            AppWindow.walkView.toggleLightView();
        });

        addSeparator();

        bg = new ButtonGroup();

        colorButton = addButton("tool_color", "Show Texture Colors", true, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_COLOR);
        });
        normalButton = addButton("tool_normal", "Show Texture Normals", false, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_NORMAL);
        });
        MetallicRoughnessButton = addButton("tool_metal_rough", "Show Texture Metallic-Roughness", false, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_METALLIC_ROUGHNESS);
        });
        emissiveButton = addButton("tool_emissive", "Show Emissive", false, bg, (e) -> {
            AppWindow.walkView.setDisplayType(WalkView.WV_DISPLAY_EMISSIVE);
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

    public void reset() {
        lightButton.setSelected(false);
        colorButton.setSelected(false);
        normalButton.setSelected(false);
        MetallicRoughnessButton.setSelected(false);
        emissiveButton.setSelected(false);
    }

}
