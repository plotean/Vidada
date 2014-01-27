package vidada.model.settings;


import vidada.data.ISingleEntityRepository;
import vidada.data.db4o.SingleEntityRepositoryDb4o;
import vidada.model.entities.BaseEntity;
import archimedesJ.crypto.KeyPad;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;

/**
 * Holds database specific settings. 
 * That is, each vidada database has its own distinct DatabaseSettings.
 * 
 * @author IsNull
 *
 */
public class DatabaseSettings extends BaseEntity{

	transient private final static ISingleEntityRepository<DatabaseSettings> repository =
			new SingleEntityRepositoryDb4o<DatabaseSettings>(DatabaseSettings.class){
		@Override
		protected DatabaseSettings createDefault() {
			DatabaseSettings setting = new DatabaseSettings();
			setting.setIgnoreImages(false);
			setting.setNewDatabase(true);
			return setting;
		}
	};

	private transient EventHandlerEx<EventArgs> PlaySoundDirectPlayChanged = new EventHandlerEx<EventArgs>();

	public IEvent<EventArgs> getPlaySoundDirectPlayChanged() {return PlaySoundDirectPlayChanged;}

	/**
	 * Gets the application settings
	 * @return
	 */
	public synchronized static DatabaseSettings getSettings(){
		return repository.get(); 
	}


	/**
	 * Persist the current changes
	 */
	public void persist(){
		repository.update(this);
	}


	private String name;
	private byte[] passwordHash;
	private byte[] cryptoBlock = KeyPad.generateKey(30);
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

	public byte[] getCryptoBlock() {
		return cryptoBlock;
	}

	public void setCryptoBlock(byte[] cryptoBlock) {
		this.cryptoBlock = cryptoBlock;
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
