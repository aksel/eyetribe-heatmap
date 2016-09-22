package heatmap;

import com.theeyetribe.clientsdk.GazeManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Frame extends JFrame {

    private ImagePainter imagePainter;

    private GazeManager gazeManager;

    private JButton startCaptureButton;
    private JButton stopCaptureButton;

    /**
     * File path for file-chooser to start at.
     */
    private String lastFolder = "C:\\";

    public Frame() {
        setTitle("Heatmap");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        //Set L&F to system look and feel.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Slider.focus", UIManager.get("Slider.background"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initGazeManager();
        initImagePainter();

        add(createSettingsPanel(), BorderLayout.CENTER);

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Initialize the GazeManager. Add shutdown hook, that removes the listener, and deactivates it.
     */
    private void initGazeManager() {
        gazeManager = GazeManager.getInstance();

        //On shutdown, stop gazemanager and remove the listener.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                gazeManager.removeGazeListener(imagePainter);
                gazeManager.deactivate();
            }
        });

        //Do not use activateAsync. Thread has to wait until it has been activated.
        gazeManager.activate();
    }

    /**
     * Initializes ImagePainter.
     * It is initialized with the screen resolution gotten from the GazeManager.
     * This resets the current heatmap.
     */
    private void initImagePainter() {
        if (imagePainter == null) {
            imagePainter = new ImagePainter();
        }

        //stopCapture();
        imagePainter.initializeImage(gazeManager.getScreenResolutionWidth(), gazeManager.getScreenResolutionHeight());
        //startCapture();
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

            JSlider intensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
            intensitySlider.setMajorTickSpacing(10);
            intensitySlider.setMinorTickSpacing(5);
            intensitySlider.setPaintTicks(true);
            intensitySlider.setPaintLabels(true);
            intensitySlider.setToolTipText("This determines how quickly the color goes from blue to red.");

            //TODO: Add listener to slider

            intensityPanel.add(intensitySlider);

            settingsPanel.add(intensityPanel);
        }

        //Create capture buttons
        {
            JPanel captureButtonsPanel = new JPanel();
            captureButtonsPanel.setLayout(settingComponentLayout);
            captureButtonsPanel.setBorder(BorderFactory.createTitledBorder("Capture"));

            startCaptureButton = new JButton("Start");
            startCaptureButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    startCapture();
                }
            });

            stopCaptureButton = new JButton("Stop");
            stopCaptureButton.setEnabled(false);
            stopCaptureButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stopCapture();
                }
            });

            captureButtonsPanel.add(startCaptureButton);
            captureButtonsPanel.add(stopCaptureButton);

            settingsPanel.add(captureButtonsPanel);
        }

        //Create image panel
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
                    initImagePainter();
                }
            });

            imageSettingsPanel.add(saveButton);
            imageSettingsPanel.add(resetButton);

            settingsPanel.add(imageSettingsPanel);
        }

        return settingsPanel;
    }

    private void startCapture() {
        //If gazemanager is not activated, prompt user with warning.
        if (!gazeManager.isActivated()) {
            JOptionPane.showMessageDialog(this,
                    "Could not start capture.\nIs EyeTribe Server running?",
                    "Connection Failed",
                    JOptionPane.WARNING_MESSAGE);
        }

        else {
            gazeManager.addGazeListener(imagePainter);

            startCaptureButton.setEnabled(false);
            stopCaptureButton.setEnabled(true);
        }
    }

    private void stopCapture() {
        gazeManager.removeGazeListener(imagePainter);

        startCaptureButton.setEnabled(true);
        stopCaptureButton.setEnabled(false);
    }

    /**
     * Saves heatmap image to a user-specified path.
     *
     * This will stop the capture.
     * The user picks a filepath and name with a filechooser.
     * Images are always PNG.
     */
    private void saveImage() {
        stopCapture();

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

            if (!file.canWrite()) {
                JOptionPane.showMessageDialog(this,
                        "Cannot write file to: " + filePath,
                        "Error while saving",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JOptionPane.showMessageDialog(this,
                    "Heatmap saved to: " + filePath,
                    "Done Capturing",
                    JOptionPane.PLAIN_MESSAGE);

            lastFolder = filePath.substring(0, filePath.lastIndexOf('\\'));
        }
    }

    public static void main(String[] args) {
        System.out.println("EyeTribe Heatmap");
        new Frame();
    }
}
