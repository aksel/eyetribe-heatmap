package heatmap;

import com.theeyetribe.clientsdk.IGazeListener;
import com.theeyetribe.clientsdk.data.GazeData;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImagePainter implements IGazeListener {

    final int AREA = 32;

    private BufferedHeatmapImage image;

    private CaptureManager captureManager;

    public ImagePainter() {
        captureManager = new CaptureManager(this);
        initImage();
    }

    /**
     * Creates image with dimension of screen, and fills it with the starting color.
     */
    public void initImage() {
        int[] resolution = captureManager.getScreenResolution();
        image = new BufferedHeatmapImage(resolution[0], resolution[1]);
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

                image.updatePixel(gX + x, gY + y);
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
}
