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

        int[] res = getScreenResolution();

        imagePainter = new ImagePainter();
        imagePainter.initializeImage(res[0], res[1]);

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
     * Gets screen resolution from GazeManager.
     * @return [width, height]
     */
    private int[] getScreenResolution() {
        int[] res = new int[2];
        res[0] = gazeManager.getScreenResolutionWidth();
        res[1] = gazeManager.getScreenResolutionHeight();

        return res;
    }

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        //Right-alignment, no gaps
        FlowLayout settingComponentLayout = new FlowLayout();
        settingComponentLayout.setHgap(0);
        settingComponentLayout.setVgap(0);
        settingComponentLayout.setAlignment(FlowLayout.RIGHT);

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
