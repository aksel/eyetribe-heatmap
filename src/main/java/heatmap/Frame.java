package heatmap;

import com.sun.awt.AWTUtilities;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Frame extends JFrame {

    private ImagePanel imagePanel;

    public Frame(BufferedImage img) {

        int width = img.getWidth();
        int height = img.getHeight();

        setTitle("EyeTribe Heatmap");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(false);
        setResizable(false);
        setFocusable(false);
        setUndecorated(true);

        AWTUtilities.setWindowOpaque(this, false);

        setLayout(new BorderLayout());
        setSize(new Dimension(width,height));
        setLocationRelativeTo(null);

        imagePanel = new ImagePanel(img);
        add(imagePanel, BorderLayout.CENTER);

        setVisible(true);

        setTransparent();
    }

    /**
     * http://stackoverflow.com/a/28772306
     */
    private void setTransparent() {
        WinDef.HWND hwnd = new WinDef.HWND();
        hwnd.setPointer(Native.getComponentPointer(this));

        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
    }
}
