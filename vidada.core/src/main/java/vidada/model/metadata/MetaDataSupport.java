package vidada.model.metadata;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ch.securityvision.metadata.FileMetaDataSupportFactory;
import ch.securityvision.metadata.IFileMetaDataSupport;
import ch.securityvision.metadata.MetaDataNotSupportedException;
import ch.securityvision.metadata.MetadataIOException;

import java.io.File;
import java.net.URI;
import java.util.List;

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
    public boolean writeMetaData(URI uri, MediaMetaAttribute attribute, String value){
        boolean success = false;
        File file = new File(uri);
        try {
            fileMetaDataSupport.writeAttribute(file, attribute.getAttributeName(), value);
            success = true;
        } catch (MetadataIOException e) {
            logger.error(e);
            if(!file.canWrite()){
                logger.warn("Can not write meta-data since file is read-only: " + file);
            }
        }
        return success;
    }

    /**
     * Read the given attribute from the given files meta data.
     *
     * If the attribute does not exists or when it could not be retrieved,
     * NULL is returned.
     *
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

    /**
     * Lists all meta-data attributes of the given file
     * @param uri
     * @return
     */
    public List<String> listAllAttributes(URI uri){
        try {
            return fileMetaDataSupport.listAttributes(new File(uri));
        } catch (MetadataIOException e) {
            logger.error(e);
        }
        return null;
    }

}
