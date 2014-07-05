package vidada.client;

import javafx.application.Platform;

/**
 * Utility to run code in desired threads.
 */
public abstract class ThreadUtil {

    private static ThreadUtil instance;

    public synchronized static ThreadUtil instance(){
        if(instance == null){
            instance = new ThreadUtilFx();
        }
        return instance;
    }

    /**
     * Runs the the given code in the UI thread.
     * If called form the UI thread, it will execute the code right away.
     * @param update
     */
    public static void runUIThread(Runnable update){
        instance().runUIThreadInternal(update);
    }

    /**
     * Runs the the given code in the UI thread.
     * If called form the UI thread, it will execute the code right away.
     *
     * @param update
     */
    public abstract void runUIThreadInternal(Runnable update);


    /**
     * JavaFX implementation
     */
    private static class ThreadUtilFx extends ThreadUtil {
        @Override
        public void runUIThreadInternal(Runnable update) {
            if(!Platform.isFxApplicationThread()){
                Platform.runLater(update);
            }else{
                update.run();
            }
        }
    }

}
