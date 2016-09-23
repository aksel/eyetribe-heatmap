package heatmap;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;


public class BufferedHeatmapImage extends BufferedImage{
    public BufferedHeatmapImage(int x, int y) {
        super(x, y, BufferedHeatmapImage.TYPE_INT_RGB);

        Graphics2D graphics = createGraphics();
        graphics.setPaint ( new Color( Color.HSBtoRGB(0.66f, 1f, 0.01f)) );
        graphics.fillRect ( 0, 0, getWidth(), getHeight());
    }

    public void updatePixel(int x, int y) {
        int color = getRGB(x, y);

        int r = (color & 0xff0000) >>> 16;
        int g = (color & 0x00ff00) >>> 8;
        int b = color & 0x0000ff;

        float[] hsb = Color.RGBtoHSB(r,g,b, null);

        if (hsb[0] <= 0.004575163f) {
            return;
        }

        else if(hsb[0] >= 0.666 && hsb[2] < 1f) {
            hsb[2] += 0.01f;
        }

        else {
            hsb[0] -= 0.005f;
        }

        hsb[1] = 1f;

        color = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

        setRGB(x, y, color);
    }
}
