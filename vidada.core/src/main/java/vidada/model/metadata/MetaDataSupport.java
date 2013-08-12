package vidada.model.metadata;

import java.io.File;
import java.net.URI;

import archimedesJ.io.metadata.FileMetaDataSupportFactory;
import archimedesJ.io.metadata.IFileMetaDataSupport;
import archimedesJ.io.metadata.MetaDataNotSupportedException;

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
	 * @param file
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
	 * @param file
	 * @param attribute
	 * @param value
	 */
	public void writeMetaData(URI uri, MediaMetaAttribute attribute, String value){
		fileMetaDataSupport.writeAttribute(new File(uri), attribute.getAttributeName(), value);
	}

	/**
	 * Read the given attribute from the given files meta data
	 * @param file
	 * @param attribute
	 * @return
	 */
	public String readMetaData(URI uri, MediaMetaAttribute attribute){
		return fileMetaDataSupport.readAttribute(new File(uri), attribute.getAttributeName());
	}

}
