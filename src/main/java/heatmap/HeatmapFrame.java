package heatmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HeatmapFrame extends JFrame {

    public HeatmapFrame(final BufferedImage img) {
        setLayout(new BorderLayout());
        setSize(new Dimension(img.getWidth(), img.getHeight()));

        JPanel imagePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(img, 0, 0, null);

                try {
                    Thread.sleep(1000/15);
                    repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        imagePanel.setBackground(Color.BLACK);

        add(imagePanel,BorderLayout.CENTER);

        setVisible(true);
    }
}
