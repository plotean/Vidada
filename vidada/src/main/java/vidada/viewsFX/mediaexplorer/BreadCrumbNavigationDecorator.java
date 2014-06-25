package vidada.viewsFX.mediaexplorer;

import archimedes.core.io.locations.DirectoryLocation;
import javafx.scene.control.TreeItem;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.BreadCrumbBar;

/**
 * TODO Documentation
 */
public class BreadCrumbNavigationDecorator {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(BreadCrumbNavigationDecorator.class.getName());

    private final HomeLocationBreadCrumb home;
	private final BreadCrumbBar<LocationBreadCrumb> breadCrumbBar;
	private DirectoryLocation root;
	private DirectoryLocation directory;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new BreadCrumbNavigationDecorator
     * @param breadCrumbBar
     * @param home
     */
	public BreadCrumbNavigationDecorator(BreadCrumbBar<LocationBreadCrumb> breadCrumbBar, HomeLocationBreadCrumb home){
		this.home = home;
		this.breadCrumbBar = breadCrumbBar;

		updateModel();
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	public void setDirectory(DirectoryLocation home, DirectoryLocation directory){

        logger.debug("LocationBreadCrumbBarModel: " + directory);

		if(this.root == home && this.directory == directory) return;

		this.root = home;
		this.directory = directory;
		updateModel();
	}

	public DirectoryLocation getHomeDirectory(){
		return root;
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

	private void updateModel(){
		TreeItem<LocationBreadCrumb> targetNode = null;
		TreeItem<LocationBreadCrumb> current = null;

		if(root != null && directory != null){
			DirectoryLocation dir = directory;

			boolean foundHome = false;
			while(dir != null){

				TreeItem<LocationBreadCrumb> p = new TreeItem<>(new LocationBreadCrumb(dir));
				if(current != null) 
					p.getChildren().add(current);
				else
					targetNode = p;
				current = p;

				if(dir.equals(root)){
					foundHome = true;
					break;
				}
				dir = dir.getParent();
			}

			if(!foundHome){
				// Curently, only debug output
                logger.warn("LocationBreadCrumbBarModel could not backtrack from directory to root dir:");
                logger.warn("Root  : " + root);
                logger.warn("Target: " + directory);
			}
		}
		TreeItem<LocationBreadCrumb> homeNode = new TreeItem<LocationBreadCrumb>(home);
		if(current != null) homeNode.getChildren().add(current);
		if(targetNode == null) targetNode = homeNode;

		breadCrumbBar.setSelectedCrumb(targetNode);
	}

}
