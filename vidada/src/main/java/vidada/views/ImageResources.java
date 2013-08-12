package vidada.views;

import java.net.URL;

import javax.swing.Icon;

import archimedesJ.util.images.IconFactory;

/**
 * Holds all image resources, this way images are loaded just once and easily accessible
 * @author IsNull
 *
 */
public class ImageResources {

	private static ImageResources instance = new ImageResources();


	// main icons
	public static final Icon FOLDER_ICON_32 = getImage("/images/icons/light/light_Book.png"); //new ImageIcon(instance.getClass().getResource()); 
	public static final Icon UPDATELIB_ICON_32 = getImage("/images/icons/light/light_Display-down.png");
	public static final Icon TAG_ICON_32 = getImage("/images/icons/light/light_Tag.png");
	public static final Icon SETTINGS_ICON_32 = getImage("/images/icons/light/light_Engine.png");



	public static final Icon FOLDER_ICON_DARK = getImage("/images/icons/dark/dark_Book.png"); 

	public static final Icon DELETE_ICON_32 = getImage("/images/icons/dark/dark_Minus.png");
	public static final Icon ADD_ICON_32 = getImage("/images/icons/dark/dark_Plus.png");
	public static final Icon EDIT_ICON_32 = getImage("/images/icons/dark/dark_File-3.png");

	public static final Icon SEARCH_ICON_32 = getImage("/images/icons/dark/dark_Search.png");

	public static final Icon DB_ICON_32 = getImage("/images/mono-icons-32/db.png");
	public static final Icon MUSIC_ICON_32 = getImage("/images/mono-icons-32/melody32.png");

	public static final Icon LOGO_ICON= getImage("/images/icons/light/light_Book.png");





	private static Icon getImage(String path){

		URL imgURL = instance.getClass().getResource(path);

		if(imgURL != null)
			return IconFactory.instance().loadIcon(imgURL);
		else {
			System.out.println("cant find image @ " + path);
			return null;
		}

	}

}
