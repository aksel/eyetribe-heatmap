package heatmap;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Frame extends JFrame {

    private ImagePanel imagePanel;

    private GraphicsDevice[] graphicsDevices;

    //Heatmap settings components, in order of appearance
    private JPanel settingsPanel;
    private JComboBox<String> screenBox;
    private JSlider intensitySlider;
    private JButton captureButton;
    private JButton saveButton;

    public Frame(BufferedImage img) {

        graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

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

        //Initialize and add heatmap settings components.
        {
            settingsPanel = new JPanel();
            settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

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

                screenBox = new JComboBox<String>(screenNames);
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

                intensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
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

                captureButton = new JButton("Capture");
                saveButton = new JButton("Save");
                saveButton.setEnabled(false);

                //TODO: Add listener to buttons

                buttonsPanel.add(captureButton);
                buttonsPanel.add(saveButton);

                settingsPanel.add(buttonsPanel);
            }
        }

        add(settingsPanel, BorderLayout.CENTER);

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
