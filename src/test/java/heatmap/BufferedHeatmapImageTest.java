package heatmap;

import org.junit.Assert;
import org.junit.Test;

public class BufferedHeatmapImageTest {

    public BufferedHeatmapImage getOneByOneImage() {
        int w = 1;
        int h = 1;

        return new BufferedHeatmapImage(w,h);
    }

    @Test
    public void testInitialColorIsTransparentBlue() {
        BufferedHeatmapImage img = getOneByOneImage();

        int rgb = img.getRGB(0,0);

        Assert.assertEquals(rgb, 0x000000FF);
    }

    @Test
    public void testAlphaIncreasesCorrectly() {
        BufferedHeatmapImage img = getOneByOneImage();

        img.updatePixel(0,0);

        int rgb = img.getRGB(0,0);

        Assert.assertEquals(rgb, 0x010000FF);

        for(int i = 1; i < 255; i++) {
            img.updatePixel(0,0);

        }

        rgb = img.getRGB(0,0);

        Assert.assertEquals(rgb, 0xFF0000FF);
    }
}