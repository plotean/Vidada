package vidada;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.securityvision.metadata.MetaDataNotSupportedException;
import vidada.model.metadata.MediaMetaAttribute;
import vidada.model.metadata.MetaDataSupport;

import java.io.File;

public class MetaDatatest {

    private static final Logger logger = LogManager.getLogger(MetaDatatest.class.getName());


    /**
	 * @param args
	 */
	@Ignore
	public static void main(String[] args) {

		MetaDataSupport metaDataSupport;
		try {

			metaDataSupport = new MetaDataSupport();
			File file = new File("/Users/IsNull/Pictures/smoke-phun.png");
			metaDataSupport.writeMetaData(file.toURI(), MediaMetaAttribute.FileHash, "halleluhijjaaa");

            logger.info("written attribute. reading now:");

			String res = metaDataSupport.readMetaData(file.toURI(), MediaMetaAttribute.FileHash);
            logger.info("meta data: " + res);

		} catch (MetaDataNotSupportedException e) {
			e.printStackTrace();
		}


	}

}
