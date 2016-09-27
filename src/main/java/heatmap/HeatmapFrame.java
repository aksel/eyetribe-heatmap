package heatmap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class HeatmapFrame extends JFrame {

    private JPopupMenu contextMenu;

    public HeatmapFrame(final BufferedImage img) {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setSize(new Dimension(img.getWidth(), img.getHeight()));

        createContextMenu();

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

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createContextMenu() {
        contextMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("Close");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new WindowEvent(HeatmapFrame.this, WindowEvent.WINDOW_CLOSING));
            }
        });

        contextMenu.add(menuItem);

        addMouseListener(new PopupListener());
    }

    class PopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            showContextMenu(e);
        }

        public void mouseReleased(MouseEvent e) {
            showContextMenu(e);
        }

        private void showContextMenu(MouseEvent e) {
            if (e.isPopupTrigger()) {
                contextMenu.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }
}
