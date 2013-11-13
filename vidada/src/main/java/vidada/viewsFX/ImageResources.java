package vidada.viewsFX;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import archimedesJ.util.OSValidator;

public class ImageResources {

	public enum IconType
	{
		FOLDER_ICON_32("/images/icons/light/light_Book.png"),
		UPDATELIB_ICON_32("/images/icons/light/light_Display-down.png"),
		TAG_ICON_32("/images/icons/light/light_Tag.png"),
		SETTINGS_ICON_32("/images/icons/light/light_Engine.png"),

		FOLDER_ICON_DARK("/images/icons/dark/dark_Book.png"), 

		DELETE_ICON_32("/images/icons/dark/dark_Minus.png"),
		ADD_ICON_32("/images/icons/dark/dark_Plus.png"),
		EDIT_ICON_32("/images/icons/dark/dark_File-3.png"),

		SEARCH_ICON_32("/images/icons/dark/dark_Search.png"),

		DB_ICON_32("/images/mono-icons-32/db.png"),
		MUSIC_ICON_32("/images/mono-icons-32/melody32.png"),

		LOGO_ICON("/images/icons/light/light_Book.png");


		private final String resource;

		IconType(String resource){
			this.resource = resource;
		}

		public String getResource() {
			return this.resource;
		}

	}



	private static ImageResources instance = new ImageResources();

	private static final Pattern regexPattern = Pattern.compile("(\\.[a-z]+)");
	private static final String RETINA_RESOURCE_SUFFIX = "@2x";


	public static ImageView getImageView(IconType imageType){


		ImageView image = null;


		if(OSValidator.isHDPI()){
			Matcher matcher = regexPattern.matcher(imageType.getResource());

			String hdpiPath = matcher.replaceFirst(RETINA_RESOURCE_SUFFIX + "$1");

			InputStream inputStream = instance.getClass().getResourceAsStream(hdpiPath);
			if(inputStream != null){
				Image img = new Image(inputStream);
				image = new ImageView(img);
				image.setFitWidth(img.getWidth() / 2.0);
				image.setFitHeight(img.getHeight() / 2.0);
			}
		}

		if(image == null){
			InputStream inputStream = instance.getClass().getResourceAsStream(imageType.getResource());
			image = new ImageView(new Image(inputStream));
		}

		return image;
	}



}
