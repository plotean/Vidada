package vidada.viewsFX;

import archimedes.core.exceptions.NotSupportedException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

/**
 * Advanced FXML loader.
 */
public final class FXMLLoaderX {

    private static final Logger logger = LogManager.getLogger(FXMLLoaderX.class.getName());


    /**
     * This will load the given fxml file and store the controller of this node in the userData property.
     *
     * @param path Relative path to resource starting at "viewFX" Root
     * @return
     */
    public static Node load(String path){
        Node node = null;
        URL resource = FXMLLoaderX.class.getResource(path);
        if(resource != null){
            node = load(resource);
            if(node == null){
                throw new NotSupportedException("The fxml could not be loaded. fxml @ " + path);
            }
        }else {
            logger.error("Can not find relative path: " + path);
        }
        return node;
    }

    /**
     * This will load the given fxml file and store the controller of this node in the userData property.
     *
     * @param resource Absolute resource URL to fxml file
     * @return
     */
    public static Node load(URL resource){
        Node node = null;
        if(resource != null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                node = loader.load(resource.openStream());
                node.setUserData(loader.getController());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return node;
    }


}
