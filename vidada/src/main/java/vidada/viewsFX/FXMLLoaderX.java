package vidada.viewsFX;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

/**
 * Advanced FXML loader.
 */
public final class FXMLLoaderX {

    /**
     * This will load the given fxml file and store the controller of this node in the userData property.
     *
     * @param path Relative path to resource starting at "viewFX" Root
     * @return
     */
    public static Node load(String path){
        URL resource = FXMLLoaderX.class.getResource(path);
        if(resource == null)
            System.err.println("Can not find relative path: " + path);
        return load(resource);
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
