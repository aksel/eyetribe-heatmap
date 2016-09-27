package com.akseltorgard.heatmap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Frame extends JFrame {

    private ImagePainter imagePainter;

    private JSlider intensitySlider;

    /**
     * File path for file-chooser to start at.
     */
    private String lastFolder = "C:\\";

    public Frame(ImagePainter imagePainter) {
        setTitle("Heatmap");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        createMenuBar();
        add(createSettingsPanel(), BorderLayout.CENTER);

        this.imagePainter = imagePainter;
        imagePainter.setIntensity(intensitySlider.getValue());

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("View");
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem("Display Heatmap Frame");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayHeatmapFrame();
            }
        });
        menu.add(menuItem);

        setJMenuBar(menuBar);
    }

    private void displayHeatmapFrame() {
        BufferedImage image = imagePainter.getImage();
        new HeatmapFrame(image);
    }

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        //Right-alignment, no gaps
        FlowLayout settingComponentLayout = new FlowLayout();
        settingComponentLayout.setHgap(0);
        settingComponentLayout.setVgap(0);
        settingComponentLayout.setAlignment(FlowLayout.CENTER);

        //Create intensity slider.
        {
            JPanel intensityPanel = new JPanel();
            intensityPanel.setLayout(settingComponentLayout);
            intensityPanel.setBorder(BorderFactory.createTitledBorder("Intensity"));

            intensitySlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
            intensitySlider.setMajorTickSpacing(1);
            intensitySlider.setMinorTickSpacing(1);
            intensitySlider.setPaintTicks(true);
            intensitySlider.setPaintLabels(true);
            intensitySlider.setToolTipText("This determines how quickly the color goes from blue to red.");

            intensitySlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider)e.getSource();
                    imagePainter.setIntensity(source.getValue());
                }
            });

            intensityPanel.add(intensitySlider);

            settingsPanel.add(intensityPanel);
        }

        //Create capture buttons
        {
            JPanel captureButtonsPanel = new JPanel();
            captureButtonsPanel.setLayout(settingComponentLayout);
            captureButtonsPanel.setBorder(BorderFactory.createTitledBorder("Capture"));

            final JButton startCaptureButton = new JButton("Start");
            final JButton stopCaptureButton = new JButton("Stop");

            startCaptureButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //If the imagePainter couldn't start the capture, prompt user.
                    if (!imagePainter.startCapture()) {
                        JOptionPane.showMessageDialog(Frame.this,
                                "Could not start capture.\nIs EyeTribe Server running?",
                                "Connection Failed",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        startCaptureButton.setEnabled(false);
                        stopCaptureButton.setEnabled(true);
                    }
                }
            });

            stopCaptureButton.setEnabled(false);
            stopCaptureButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    imagePainter.stopCapture();
                    startCaptureButton.setEnabled(true);
                    stopCaptureButton.setEnabled(false);
                }
            });

            captureButtonsPanel.add(startCaptureButton);
            captureButtonsPanel.add(stopCaptureButton);

            settingsPanel.add(captureButtonsPanel);
        }

        //Create image buttons panel
        {
            JPanel imageSettingsPanel = new JPanel();
            imageSettingsPanel.setLayout(settingComponentLayout);
            imageSettingsPanel.setBorder(BorderFactory.createTitledBorder("Image"));

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveImage();
                }
            });

            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    imagePainter.resetImage();
                }
            });

            imageSettingsPanel.add(saveButton);
            imageSettingsPanel.add(resetButton);

            settingsPanel.add(imageSettingsPanel);
        }

        return settingsPanel;
    }

    /**
     * Saves heatmap image to a user-specified path.
     *
     * This will stop the capture.
     * The user picks a filepath and name with a filechooser.
     * Images are always PNG.
     */
    private void saveImage() {
        imagePainter.stopCapture();

        BufferedImage image = imagePainter.getImage();

        //Create filechooser, that only lets you choose PNG images.
        JFileChooser fc = new JFileChooser(lastFolder);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                final String name = f.getName();
                return name.endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "*.png";
            }
        });

        int result = fc.showDialog(Frame.this, "Save");

        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fc.getSelectedFile().getAbsolutePath();

            if (!filePath.endsWith(".png")) {
                filePath += ".png";
            }

            File file = new File(filePath);

            try {
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Could not write to: : " + filePath,
                        "Error while saving",
                        JOptionPane.ERROR_MESSAGE);
            }

            JOptionPane.showMessageDialog(this,
                    "Heatmap saved to: " + filePath,
                    "Done Capturing",
                    JOptionPane.PLAIN_MESSAGE);

            lastFolder = filePath.substring(0, filePath.lastIndexOf('\\'));
        }
    }
}
