package heatmap;

import com.theeyetribe.clientsdk.GazeManager;
import com.theeyetribe.clientsdk.IGazeListener;
import com.theeyetribe.clientsdk.data.GazeData;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Run {

    static Frame frame;
    static ImagePainter imagePainter;

    public static void main(String[] args) {
        System.out.println("EyeTribe Heatmap");

        //Set up GazeManager and Listener
        final GazeManager gm = GazeManager.getInstance();
        final GazeListener gazeListener = new GazeListener();
        gm.addGazeListener(gazeListener);

        if (!gm.activate()) {
            System.out.println("Could not activate!");
            System.exit(1);
        }

        //Create ImagePainter
        {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int w = gd.getDisplayMode().getWidth();
            int h = gd.getDisplayMode().getHeight();

            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            imagePainter = new ImagePainter(img);
        }

        frame = new Frame(imagePainter.getImg());

        //This is best practice, supposedly
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                gm.removeGazeListener(gazeListener);
                gm.deactivate();
            }
        });
    }

    private static class GazeListener implements IGazeListener
    {
        public void onGazeUpdate(GazeData gazeData)
        {
            int gX = (int) gazeData.rawCoordinates.x;
            int gY = (int) gazeData.rawCoordinates.y;

            boolean success = imagePainter.updateImage(gX, gY);

            if (success) {
                frame.repaint();
            }
        }
    }
}