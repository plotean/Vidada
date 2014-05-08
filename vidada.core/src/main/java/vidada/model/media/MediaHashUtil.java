package vidada.model.media;

import archimedes.core.data.hashing.FileHashAlgorythms;
import archimedes.core.data.hashing.IFileHashAlgorythm;
import archimedes.core.io.locations.ResourceLocation;
import org.securityvision.metadata.MetaDataNotSupportedException;
import vidada.model.metadata.MediaMetaAttribute;
import vidada.model.metadata.MetaDataSupport;
import vidada.model.settings.VidadaClientSettings;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides file-hash utility functions
 * @author IsNull
 *
 */
public class MediaHashUtil {

	private final IFileHashAlgorythm fileHashAlgorythm;

	private boolean forceUpdateMetaData = false;
	private static MediaHashUtil defaultMediaHashUtil;

	private MetaDataSupport metaDataSupport;

	/**
	 * Gets the default media hash util (singleton)
	 * @return
	 */
	public synchronized static MediaHashUtil getDefaultMediaHashUtil(){
		if(defaultMediaHashUtil == null)
		{
			defaultMediaHashUtil = new MediaHashUtil();
		}
		return defaultMediaHashUtil;
	}

	private MediaHashUtil(){
		this(FileHashAlgorythms.instance().getButtikscheHashAlgorythm());
	}

	public MediaHashUtil(IFileHashAlgorythm fileHashAlgorythm){
		this.fileHashAlgorythm = fileHashAlgorythm;

		// TODO Refactor this dependency away
		if(VidadaClientSettings.instance().isUsingMetaData())
		{
			try {
				metaDataSupport = new MetaDataSupport();
			} catch (MetaDataNotSupportedException e) {
				e.printStackTrace();
			}	
		}
	}

	/**
	 * Returns the file-hash of the given file.
	 * This method invoke may take some time depending on the file and hash algorithm.
	 * 
	 * Depending on the file system, file hashes can be stored in metadata which dramatically improves 
	 * performance.
	 * @param mediaPath
	 * @return
	 */
	public String retriveFileHash(ResourceLocation mediaPath){
		if(metaDataSupport != null && metaDataSupport.isMetaDataSupported(mediaPath.getUri()))
			return retriveFileHashMetaData(mediaPath);
		else
			return calculateHash(mediaPath);
	}

	/**
	 * Retrieves the file hash using meta-data. 
	 * If the hash is not yet stored in meta-data, it will be calculated and stored in meta-data.
	 * @param mediaPath
	 * @return
	 */
	public String retriveFileHashMetaData(ResourceLocation mediaPath){

		String hash = metaDataSupport.readMetaData(mediaPath.getUri(), MediaMetaAttribute.FileHash);

		if(hash == null || forceUpdateMetaData)
		{
			hash = calculateHash(mediaPath);
			metaDataSupport.writeMetaData(mediaPath.getUri(), MediaMetaAttribute.FileHash, hash);
			System.out.println("MediaHashUtil: hash calculated and saved in meta data: " + hash);
		}else{
			//System.out.println("MediaHashUtil: hash readed from metadata: " + hash);
		}

		return hash;
	}

	/**
	 * Calculate the hash for the given media path
	 * @param mediaPath
	 * @return
	 */
	private String calculateHash(ResourceLocation mediaPath){
		String hash = null;
		InputStream is = null;
		try{
			is = mediaPath.openInputStream();
			hash = fileHashAlgorythm.calculateHashString(is, mediaPath.length());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return hash;
	}



}
