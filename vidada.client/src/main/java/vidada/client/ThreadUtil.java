package vidada.client;

import javafx.application.Platform;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility to run code in desired threads.
 */
public abstract class ThreadUtil {

    transient private static final Logger logger = LogManager.getLogger(ThreadUtil.class.getName());


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
     * @param code
     */
    public static void runUIThread(Runnable code){
        instance().runUIThreadInternal(code);
    }

    /**
     * Runs the the given code in the UI thread and waits (blocks) until it has completed.
     * @param code
     */
    public static void runUIThreadWait(Runnable code) { instance().runUIThreadWaitInternal(code);}

    /**
     * Runs the the given code in the UI thread.
     * If called form the UI thread, it will execute the code right away.
     *
     * @param code
     */
    public abstract void runUIThreadInternal(Runnable code);

    public abstract void runUIThreadWaitInternal(Runnable code);

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

        @Override
        public void runUIThreadWaitInternal(final Runnable code) {
            if (Platform.isFxApplicationThread()) {
                code.run();
            } else {
                final Lock lock = new ReentrantLock();
                final Condition condition = lock.newCondition();
                final ThrowableWrapper throwableWrapper = new ThrowableWrapper();
                lock.lock();
                try {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            lock.lock();
                            try {
                                code.run();
                            } catch (Throwable e) {
                                throwableWrapper.t = e;
                            } finally {
                                try {
                                    condition.signal();
                                } finally {
                                    lock.unlock();
                                }
                            }
                        }
                    });
                    condition.await();
                    if (throwableWrapper.t != null) {
                        throw new ExecutionException(throwableWrapper.t);
                    }
                } catch (InterruptedException e) {
                    logger.error(e);
                } catch (ExecutionException e) {
                    logger.error(e);
                } finally {
                    lock.unlock();
                }
            }
        }
    }


    private static class ThrowableWrapper {
        Throwable t;
    }


}
