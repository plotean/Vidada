package vidada.model.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;

import archimedesJ.util.FileSupport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Base class for Json based settings
 * @author IsNull
 *
 */
public class JsonSettings {

	private transient File settingsPath = null;
	private String settingsVersion = "1.0";

	protected JsonSettings() { }


	public String getSettingsVersion(){
		return settingsVersion;
	}

	protected void setSettingsVersion(String version){
		settingsVersion = version;
	}






	protected void setPath(File path){
		this.settingsPath = path;
	}

	public void persist() {

		assert settingsPath != null : "You must set the Settings path before calling persist()!";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gson.toJson(this);
		try {
			FileSupport.writeToFile(settingsPath, jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public static Object loadSettings(File path, Type type){
		Object settings = null;

		try {
			String json = FileSupport.readFileToString(path);

			Gson gson = new GsonBuilder().create();

			settings = gson.fromJson(json, type);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return settings;
	}


}
