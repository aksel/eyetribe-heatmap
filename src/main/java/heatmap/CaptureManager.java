package heatmap;

import com.theeyetribe.clientsdk.GazeManager;
import com.theeyetribe.clientsdk.IGazeListener;

public class CaptureManager {

    private GazeManager gazeManager;
    private IGazeListener gazeListener;

    /**
     * Initialize the GazeManager. Add shutdown hook, that removes the listener, and deactivates manager.
     */
    public CaptureManager() {
        gazeManager = GazeManager.getInstance();

        //On shutdown, stop gazemanager and remove the listener.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                gazeManager.removeGazeListener(CaptureManager.this.gazeListener);
                gazeManager.deactivate();
            }
        });

        //Do not use activateAsync. Thread has to wait until it has been activated.
        gazeManager.activate();
    }

    /**
     * @return [width, height]
     */
    public int[] getScreenResolution() {
        int[] resolution = new int[2];
        resolution[0] = gazeManager.getScreenResolutionWidth();
        resolution[1] = gazeManager.getScreenResolutionHeight();
        return resolution;
    }

    public boolean startCapture() {
        if (!gazeManager.isActivated()) {
            return false;
        } else {
            gazeManager.addGazeListener(gazeListener);
            return true;
        }
    }

    public void stopCapture() {
        gazeManager.removeGazeListener(gazeListener);
    }

    public void setGazeListener(IGazeListener gazeListener) {
        this.gazeListener = gazeListener;
    }

    public boolean isActivated() {
        return gazeManager.isActivated();
    }

    public void reactivate() {
        gazeManager.activate();
    }
}