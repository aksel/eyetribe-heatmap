package heatmap;

import com.theeyetribe.clientsdk.GazeManager;
import com.theeyetribe.clientsdk.IGazeListener;
import com.theeyetribe.clientsdk.data.GazeData;

import javax.swing.SwingUtilities;

public class Run {

    static Frame frame;
    static ImageHandler imageHandler;

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

        //Create ImageHandler
        imageHandler = new ImageHandler();

        frame = new Frame(imageHandler.getImg());

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

            boolean success = imageHandler.updateImage(gX, gY);

            if (success) {
                frame.repaint();
            }
        }
    }
}