package heatmap;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageHandler {

    private BufferedImage img;

    public ImageHandler() {

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int w = gd.getDisplayMode().getWidth();
        int h = gd.getDisplayMode().getHeight();

        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D    graphics = img.createGraphics();

        graphics.setPaint ( new Color ( Color.HSBtoRGB(0.66f, 1f, 0.01f)) );
        graphics.fillRect ( 0, 0, w, h);
    }

    public BufferedImage getImg() {
        return img;
    }

    public boolean updateImage(int gX, int gY) {

        int area = 32;

        for (int x = -area; x <= area; x++) {
            for (int y = -area; y <= area; y++) {
                try {
                    int color = img.getRGB(gX+x, gY+y);

                    int r = (color & 0xff0000) >>> 16;
                    int g = (color & 0x00ff00) >>> 8;
                    int b = color & 0x0000ff;

                    float[] hsb = Color.RGBtoHSB(r,g,b, null);

                    //System.out.println(hsb[0] + ", " + hsb[1] + ", " + hsb[2]);

                    if (hsb[0] <= 0.004575163f) {
                        continue;
                    }

                    else if(hsb[0] >= 0.666 && hsb[2] < 1f) {
                        hsb[2] += 0.01f;
                    }

                    else {
                        hsb[0] -= 0.005f;
                        //hsb[2] = 1f;
                    }

                    hsb[1] = 1f;

                    color = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

                    img.setRGB(gX+x, gY+y, color);

                    /*
                    int a = color & 0xff000000;

                    color = color & 0xffffff;

                    if (color == 0xff0000) {
                        continue;
                    }

                    color <<= 1;

                    if (color < 0xff) {
                        color |= 1;
                    }

                    int intensity = 255 - (Math.abs(x) * Math.abs(y));

                    color |= a | (intensity << 24);
                    */

                    img.setRGB(gX+x, gY+y, color);
                } catch (Exception e) {
                    //System.err.println(e.getMessage());
                }
            }
        }

        return true;
    }
}
