package com.akseltorgard.heatmap;

import com.theeyetribe.clientsdk.IGazeListener;
import com.theeyetribe.clientsdk.data.GazeData;

import java.awt.image.BufferedImage;

public class ImagePainter implements IGazeListener {

    final int AREA = 8;

    private BufferedHeatmapImage image;

    private CaptureManager captureManager;

    /**
     * Intensity from 1 to 10
     */
    private int intensity;

    public ImagePainter(CaptureManager captureManager) {
        this.captureManager = captureManager;
        captureManager.setGazeListener(this);
        initImage();
    }

    /**
     * Creates image with dimension of screen, and fills it with the starting color.
     */
    public void initImage() {
        int[] resolution = captureManager.getScreenResolution();
        image = new BufferedHeatmapImage(resolution[0], resolution[1]);
    }

    public void resetImage() {
        image.setInitialColor();
    }

    public boolean startCapture() {
        return captureManager.startCapture();
    }

    public void stopCapture() {
        captureManager.stopCapture();
    }

    public void updateImage(int gX, int gY) {

        //Coordinate [0,0] usually means eye-tracker couldn't track the eyes
        if (gX == 0 && gY == 0) {
            return;
        }

        int w = image.getWidth();
        int h = image.getHeight();

        //Check bounds
        if (gX < 0 || gX > w || gY < 0 || gY > h) {
            return;
        }

        for (int x = -AREA; x <= AREA; x++) {

            if (gX + x < 0) {
                continue;
            }

            if (gX + x > w) {
                break;
            }

            for (int y = -AREA; y <= AREA; y++) {

                if (gY + y < 0) {
                    continue;
                }

                if (gY + y > h) {
                    break;
                }

                image.updatePixel(gX + x, gY + y, intensity);
            }
        }
    }

    public void onGazeUpdate(GazeData gazeData) {
        int gX = (int) gazeData.rawCoordinates.x;
        int gY = (int) gazeData.rawCoordinates.y;

        updateImage(gX, gY);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
}
