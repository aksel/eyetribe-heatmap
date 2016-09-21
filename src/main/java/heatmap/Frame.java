package heatmap;

import com.theeyetribe.clientsdk.GazeManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Frame extends JFrame {

    private ImagePainter imagePainter;
    private GazeManager gazeManager;

    private JButton startCaptureButton;
    private JButton stopCaptureButton;

    public Frame() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
        DisplayMode defaultDisplayMode = graphicsEnvironment.getDefaultScreenDevice().getDisplayMode();

        imagePainter = new ImagePainter();
        imagePainter.initializeImage(defaultDisplayMode.getWidth(), defaultDisplayMode.getHeight());

        //Set L&F to system look and feel.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Slider.focus", UIManager.get("Slider.background"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Heatmap");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        //Initialize and add heatmap settings components.
        {
            //Right-alignment, no gaps
            FlowLayout settingComponentLayout = new FlowLayout();
            settingComponentLayout.setHgap(0);
            settingComponentLayout.setVgap(0);
            settingComponentLayout.setAlignment(FlowLayout.RIGHT);

            //If there is more than one screen connected, let user choose which screen to record, via screenBox.
            if (graphicsDevices.length > 0) {
                JPanel screenPanel = new JPanel();
                screenPanel.setLayout(settingComponentLayout);
                screenPanel.setBorder(BorderFactory.createTitledBorder("Screen"));

                String[] screenNames = new String[graphicsDevices.length];
                for (int i = 0; i < graphicsDevices.length; i++) {
                    DisplayMode displayMode = graphicsDevices[0].getDisplayMode();
                    screenNames[i] = displayMode.getWidth() + "x" + displayMode.getHeight();
                }

                JComboBox<String> screenBox = new JComboBox<String>(screenNames);
                screenBox.setToolTipText("Choose which screen to record from.");

                //TODO: Add listener to screenbox

                screenPanel.add(screenBox);

                settingsPanel.add(screenPanel);
            }

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

            //Create buttons
            {
                JPanel buttonsPanel = new JPanel();

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

                JButton saveButton = new JButton("Save");
                saveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveImage();
                    }
                });

                buttonsPanel.add(startCaptureButton);
                buttonsPanel.add(stopCaptureButton);
                buttonsPanel.add(saveButton);

                settingsPanel.add(buttonsPanel);
            }
        }

        add(settingsPanel, BorderLayout.CENTER);

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startCapture() {

        //Initialize GazeManager, add ImagePainter as Listener.
        {
            gazeManager = GazeManager.getInstance();
            gazeManager.addGazeListener(imagePainter);

            //On shutdown, stop gazemanager and remove the listener.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run()
                {
                    gazeManager.removeGazeListener(imagePainter);
                    gazeManager.deactivate();
                }
            });
        }

        new Thread() {
            @Override
            public void run() {
                gazeManager.activate();
            }
        }.run();

        //If gazemanager is not activated, prompt user with warning.
        if (!gazeManager.isActivated()) {
            JOptionPane.showMessageDialog(this,
                    "Could not start capture.\nIs EyeTribe Server running?",
                    "Connection Failed",
                    JOptionPane.WARNING_MESSAGE);
        }

        else {
            startCaptureButton.setEnabled(false);
            stopCaptureButton.setEnabled(true);
        }
    }

    private void stopCapture() {
        startCaptureButton.setEnabled(true);
        stopCaptureButton.setEnabled(false);

        //Remove listener and deactivate, preparing it for reactivation.
        gazeManager.removeGazeListener(imagePainter);
        gazeManager.deactivate();
    }

    private void saveImage() {

        //TODO: Output path selection.

        BufferedImage image = imagePainter.getImage();

        try {
            ImageIO.write(image, "png", new File("img.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this,
                "Images saved to: img.png",
                "Done Capturing.",
                JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        System.out.println("EyeTribe Heatmap");
        new Frame();
    }
}
