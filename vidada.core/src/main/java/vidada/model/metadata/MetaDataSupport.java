package vidada.model.metadata;

import org.securityvision.metadata.FileMetaDataSupportFactory;
import org.securityvision.metadata.IFileMetaDataSupport;
import org.securityvision.metadata.MetaDataNotSupportedException;

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

	private final IFileMetaDataSupport fileMetaDataSupport;

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
		fileMetaDataSupport.writeAttribute(new File(uri), attribute.getAttributeName(), value);
	}

	/**
	 * Read the given attribute from the given files meta data
	 * @param uri
	 * @param attribute
	 * @return
	 */
	public String readMetaData(URI uri, MediaMetaAttribute attribute){
		return fileMetaDataSupport.readAttribute(new File(uri), attribute.getAttributeName());
	}

}
