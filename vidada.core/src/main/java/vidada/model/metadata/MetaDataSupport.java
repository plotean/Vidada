package vidada.model.metadata;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ch.securityvision.metadata.FileMetaDataSupportFactory;
import ch.securityvision.metadata.IFileMetaDataSupport;
import ch.securityvision.metadata.MetaDataNotSupportedException;
import ch.securityvision.metadata.MetadataIOException;

import java.io.File;
import java.net.URI;

/**
 * MetaData Support
 * Several file metadata helper methods
 * 
 * @author IsNull
 *
 */
public class MetaDataSupport {

    private final static Logger logger = LogManager.getLogger(MetaDataSupport.class.getName());

	private final IFileMetaDataSupport fileMetaDataSupport;

    /**
     * Creates a new MetaDataSupport instance
     * @throws MetaDataNotSupportedException
     */
	public MetaDataSupport() throws MetaDataNotSupportedException {
		fileMetaDataSupport = FileMetaDataSupportFactory.buildFileMetaSupport();
	}

	/**
	 * Does the given file (file-system where the file resides) support meta-data?
	 * @param uri
	 * @return
	 */
	public boolean isMetaDataSupported(URI uri){
		if(isFileUri(uri)){
			File file = new File(uri);
			return fileMetaDataSupport.isMetaDataSupported(file);
		}
		return false;
	}

	private static boolean isFileUri(URI uri){
		return uri.getScheme().indexOf("file") != -1;
	}

	/**
	 * Write the given attribute value to the given file
	 * @param uri
	 * @param attribute
	 * @param value
	 */
	public void writeMetaData(URI uri, MediaMetaAttribute attribute, String value){
        try {
            fileMetaDataSupport.writeAttribute(new File(uri), attribute.getAttributeName(), value);
        } catch (MetadataIOException e) {
            logger.error(e);
        }
    }

	/**
	 * Read the given attribute from the given files meta data
	 * @param uri
	 * @param attribute
	 * @return
	 */
	public String readMetaData(URI uri, MediaMetaAttribute attribute){
        try {
            return fileMetaDataSupport.readAttribute(new File(uri), attribute.getAttributeName());
        } catch (MetadataIOException e) {
            logger.error(e);
        }
        return null;
    }

}
