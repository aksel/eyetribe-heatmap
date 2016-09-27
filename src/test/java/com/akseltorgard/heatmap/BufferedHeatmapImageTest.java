package com.akseltorgard.heatmap;

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

        Assert.assertEquals(0x000000FF, rgb);
    }

    @Test
    public void testAlphaIncreasesCorrectly() {
        BufferedHeatmapImage img = getOneByOneImage();

        img.updatePixel(0,0,1);

        int rgb = img.getRGB(0,0);

        Assert.assertEquals(0x010000FF, rgb);

        for(int i = 1; i < 255; i++) {
            img.updatePixel(0,0,1);

        }

        rgb = img.getRGB(0,0);

        //After 255 updatePixel calls, the pixel should be a completely opaque blue
        Assert.assertEquals(0xFF0000FF, rgb);
    }

    @Test
    public void testColorReachesRed() {
        BufferedHeatmapImage img = getOneByOneImage();

        for(int i = 1; i < 400; i++) {
            img.updatePixel(0,0,5);
        }

        Assert.assertEquals(0xFFFF0000, img.getRGB(0,0));
    }
}