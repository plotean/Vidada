package vidada.server.watcher;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class MediaWatcherService {

    private Thread watchService;
    private  WatchService watcher;

    public MediaWatcherService(){
        try {
            watcher = FileSystems.getDefault().newWatchService();


            Path dir = new File("").toPath();
            WatchKey key = dir.register(watcher,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY);

            watchService = new Thread(new WatchServiceTask());
            watchService.setDaemon(true);
            watchService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class WatchServiceTask implements Runnable {

        @Override
        public void run() {

            // TODO

        }
    }


}
