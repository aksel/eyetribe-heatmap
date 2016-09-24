package heatmap;

import java.awt.Color;
import java.awt.image.BufferedImage;


public class BufferedHeatmapImage extends BufferedImage{

    static final int A_MASK = 0xFF000000;
    static final int R_MASK = 0x00FF0000;
    static final int G_MASK = 0x0000FF00;
    static final int B_MASK = 0x000000FF;

    static final int OPAQUE_RED = 0xFFFF0000;

    static final float DECREASE_AMOUNT = 0.005f;

    public BufferedHeatmapImage(int x, int y) {
        super(x, y, BufferedHeatmapImage.TYPE_INT_ARGB);

        setInitialColor();
    }

    /**
     * Sets all pixels in image to a transparent blue color.
     */
    public void setInitialColor() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                setRGB(x,y, B_MASK);
            }
        }
    }

    /**
     * Updates the pixel at [x,y].
     * At first, the pixel will gradually turn opaque.
     * After it is completely opaque, the hue is instead decreased towards red.
     * When the pixel reaches a completely opaque red color, it is no longer changed.
     * @param x Coordinate
     * @param y Coordinate
     */
    public void updatePixel(int x, int y) {
        int color = getRGB(x, y);

        if (color == OPAQUE_RED) {
            return;
        }

        int a = (color & A_MASK) >> 24;

        //If pixel is not yet completely opaque, then increase alpha by 1.
        if ((a & 0xFF) < 255) {
            a++;
            //Remove old alpha value, insert new alpha value
            color = (color & ~A_MASK) | (a << 24);
        } else {
            color = decreaseHue(color);
        }

        setRGB(x, y, color);
    }

    /**
     * Decreases hue by DECREASE_AMOUNT, as long as the hue hasn't reached below DECREASE_AMOUNT.
     * @param color Color
     * @return Color with decreased hue.
     */
    private int decreaseHue(int color) {
        int r = (color & R_MASK) >> 16;
        int g = (color & G_MASK) >> 8;
        int b = (color & B_MASK);

        float[] hsb = Color.RGBtoHSB(r,g,b, null);

        if (hsb[0] <= DECREASE_AMOUNT) {
            return OPAQUE_RED;
        }

        hsb[0] -= DECREASE_AMOUNT;

        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }
}