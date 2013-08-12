package vidada.model.settings;


import java.util.List;

import vidada.data.SessionManager;
import vidada.model.entities.BaseEntity;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.exceptions.NotSupportedException;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/**
 * Holds database specific settings. 
 * That is, each vidada database has its own distinct DatabaseSettings.
 * 
 * @author IsNull
 *
 */
public class DatabaseSettings extends BaseEntity{


	private static DatabaseSettings settings;

	private transient EventHandlerEx<EventArgs> PlaySoundDirectPlayChanged = new EventHandlerEx<EventArgs>();

	public IEvent<EventArgs> getPlaySoundDirectPlayChanged() {return PlaySoundDirectPlayChanged;}

	/**
	 * Gets the application settings
	 * @return
	 */
	public synchronized static DatabaseSettings getSettings(){
		if(settings == null){
			settings = fetchSettings();
		}
		return settings; 
	}

	private synchronized static DatabaseSettings fetchSettings(){

		DatabaseSettings setting = null;

		ObjectContainer db = SessionManager.getObjectContainer();

		Query query = db.query();
		query.constrain(DatabaseSettings.class);
		List<DatabaseSettings> settings = query.execute();


		if(settings.isEmpty())
		{
			System.out.println("defalt settings created.");
			setting = getDefaultSettings();
			db.store(setting);
		}else{
			setting = settings.get(0);
		}

		if(setting == null)
			throw new NotSupportedException("DatabaseSettings could not be loaded");

		return setting; 
	}

	private static DatabaseSettings getDefaultSettings(){
		DatabaseSettings setting = new DatabaseSettings();
		setting.setIgnoreImages(false);
		setting.setNewDatabase(true);
		return setting;
	}

	/**
	 * Persist the current changes
	 */
	public void persist(){
		ObjectContainer db =	SessionManager.getObjectContainer();
		db.store(this);
	}


	private String name;
	private byte[] passwordHash;
	private boolean ignoreMovies;
	private boolean ignoreImages;
	private boolean isNewDatabase;
	private boolean playSoundDirectPlay;

	public DatabaseSettings(){
	}


	/**
	 * Gets the password (hash) of this database
	 * @return
	 */
	public byte[] getPasswordHash(){
		return passwordHash;
	}

	public void setPasswordHash(byte[] newPassHash){
		passwordHash = newPassHash;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIgnoreMovies() {
		return ignoreMovies;
	}

	public void setIgnoreMovies(boolean ignoreMovies) {
		this.ignoreMovies = ignoreMovies;
	}

	public boolean isIgnoreImages() {
		return ignoreImages;
	}

	public void setIgnoreImages(boolean ignoreImages) {
		this.ignoreImages = ignoreImages;
	}

	public boolean isNewDatabase() {
		return isNewDatabase;
	}

	public void setNewDatabase(boolean isNewDatabase) {
		this.isNewDatabase = isNewDatabase;
	}

	public boolean isPlaySoundDirectPlay() {
		return playSoundDirectPlay;
	}

	public void setPlaySoundDirectPlay(boolean playSoundDirectPlay) {
		this.playSoundDirectPlay = playSoundDirectPlay;
		PlaySoundDirectPlayChanged.fireEvent(this, EventArgs.Empty);
	}

}
