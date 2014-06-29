package vidada.model.media;

import archimedes.core.data.hashing.FileHashAlgorythms;
import archimedes.core.data.hashing.IFileHashAlgorythm;
import archimedes.core.io.locations.ResourceLocation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ch.securityvision.metadata.MetaDataNotSupportedException;
import vidada.model.metadata.MediaMetaAttribute;
import vidada.model.metadata.MetaDataSupport;
import vidada.model.settings.VidadaClientSettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Provides file-hash utility functions
 * @author IsNull
 *
 */
public class MediaHashUtil {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaHashUtil.class.getName());
    private static MediaHashUtil defaultMediaHashUtil;

    private final IFileHashAlgorythm fileHashAlgorythm;
	private static final boolean forceUpdateMetaData = false;
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

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Default Instance Constructor
     */
	private MediaHashUtil(){
		this(FileHashAlgorythms.instance().getButtikscheHashAlgorythm(),
             VidadaClientSettings.instance().isUsingMetaData()); // TODO Refactor this dependency away

    }

    /**
     * Creates a new MediaHashUtil
     * @param fileHashAlgorithm
     */
	public MediaHashUtil(IFileHashAlgorythm fileHashAlgorithm, boolean useMetaData){
		this.fileHashAlgorythm = fileHashAlgorithm;

		if(useMetaData)
		{
			try {
				metaDataSupport = new MetaDataSupport();
			} catch (MetaDataNotSupportedException e) {
                logger.error(e);
			}	
		}
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	/**
	 * Returns the file-hash of the given file.
	 * This method invoke may take some time depending on the file and hash algorithm.
	 * 
	 * Depending on the file system, file hashes can be stored in metadata which dramatically improves 
	 * performance.
	 * @param mediaPath
	 * @return
	 */
	public String retrieveFileHash(ResourceLocation mediaPath){
		if(metaDataSupport != null && metaDataSupport.isMetaDataSupported(mediaPath.getUri()))
			return retrieveFileHashMetaData(mediaPath);
		else
			return calculateHash(mediaPath);
	}

	/**
	 * Retrieves the file hash using meta-data. 
	 * If the hash is not yet stored in meta-data, it will be calculated and stored in meta-data.
	 * @param mediaPath
	 * @return
	 */
	public String retrieveFileHashMetaData(ResourceLocation mediaPath){

		String hash = null;

        if(!forceUpdateMetaData){
            hash = metaDataSupport.readMetaData(mediaPath.getUri(), MediaMetaAttribute.FileHash);
        }

        if(hash == null)
		{
            // We could not read the hash from meta-data, we have to recalculate it

			hash = calculateHash(mediaPath);
            if(hash != null){
                if(metaDataSupport.writeMetaData(mediaPath.getUri(), MediaMetaAttribute.FileHash, hash)){
                    logger.info("Hash recalculated and written to meta-data: " + hash);
                }else{
                    logger.warn("Could not write hash to meta-data attribute!");
                }
            }else{
                logger.error("Hash could not be calculated for " + mediaPath);
            }
		}else{
            logger.debug(String.format("Hash '%s' was retrieved from meta-data!", hash));
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
            logger.error(e);
		}finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
                    logger.error(e);
				}
		}
		return hash;
	}

}
