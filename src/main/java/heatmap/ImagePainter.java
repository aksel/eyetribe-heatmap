package heatmap;

import com.theeyetribe.clientsdk.IGazeListener;
import com.theeyetribe.clientsdk.data.GazeData;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImagePainter implements IGazeListener {

    final int AREA = 32;

    private BufferedImage image;

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
        image = new BufferedImage(resolution[0], resolution[1], BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();
        graphics.setPaint ( new Color( Color.HSBtoRGB(0.66f, 1f, 0.01f)) );
        graphics.fillRect ( 0, 0, image.getWidth(), image.getHeight());
    }

    public boolean startCapture() {
        return captureManager.startCapture();
    }

    public void stopCapture() {
        captureManager.stopCapture();
    }

    public void updateImage(int gX, int gY) {

        //TODO: Fix hue and brightness magic number and float nonsense.

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

                int color = image.getRGB(gX+x, gY+y);

                int r = (color & 0xff0000) >>> 16;
                int g = (color & 0x00ff00) >>> 8;
                int b = color & 0x0000ff;

                float[] hsb = Color.RGBtoHSB(r,g,b, null);

                if (hsb[0] <= 0.004575163f) {
                    continue;
                }

                else if(hsb[0] >= 0.666 && hsb[2] < 1f) {
                    hsb[2] += 0.01f;
                }

                else {
                    hsb[0] -= 0.005f;
                }

                hsb[1] = 1f;

                color = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

                image.setRGB(gX+x, gY+y, color);
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
