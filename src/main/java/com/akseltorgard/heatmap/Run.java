package com.akseltorgard.heatmap;

import javax.swing.*;

public class Run {

    public static void main(String[] args) {
        setSystemLookAndFeel();

        CaptureManager captureManager = getActivatedCaptureManager();

        if (captureManager == null) {
            System.exit(0);
        }

        new Frame(new ImagePainter(captureManager));
    }

    /**
     * Creates an instance of CaptureManager, that is not returned until it is connected to EyeTribe Server.
     * @return Activated CaptureManager, or null if user decided to quit.
     */
    private static CaptureManager getActivatedCaptureManager() {
        CaptureManager captureManager = new CaptureManager();

        String message = "Could not connect to EyeTribe Server.\n" +
                "Verify that it is running, and retry.";
        String[] options = {"Retry", "Quit"};

        //While captureManager is not activated, let user retry until connection is established.
        while (!captureManager.isActivated()) {
            int result = JOptionPane.showOptionDialog(null, message, "Error",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                    null, options, options[0]);

            switch (result) {
                case -1: case 1:
                    return null;

                case 0:
                    captureManager.reactivate();
            }
        }

        return captureManager;
    }

    private static void setSystemLookAndFeel() {
        //Set L&F to system look and feel.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Slider.focus", UIManager.get("Slider.background"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}