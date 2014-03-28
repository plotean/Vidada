package vidada.server.settings;


import archimedes.core.crypto.KeyPad;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import vidada.model.entities.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Holds database specific settings. 
 * That is, each vidada database has its own distinct DatabaseSettings.
 * 
 * @author IsNull
 *
 */
@Entity
public class DatabaseSettings extends BaseEntity{

	private transient EventHandlerEx<EventArgs> PlaySoundDirectPlayChanged = new EventHandlerEx<EventArgs>();
	@Transient public IEvent<EventArgs> getPlaySoundDirectPlayChanged() {return PlaySoundDirectPlayChanged;}

	@Id
	@GeneratedValue
	private int id;

	private String name;
	private byte[] passwordHash;
	private byte[] cryptoBlock = KeyPad.generateKey(30);
	private boolean isNewDatabase;

	public DatabaseSettings(){
	}

	public int getId() {
		return id;
	}

	void setId(int id) {
		this.id = id;
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

	public boolean isNewDatabase() {
		return isNewDatabase;
	}

	public void setNewDatabase(boolean isNewDatabase) {
		this.isNewDatabase = isNewDatabase;
	}

}
