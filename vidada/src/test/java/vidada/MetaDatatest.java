package vidada;

import java.io.File;

import org.junit.Ignore;
import org.securityvision.metadata.MetaDataNotSupportedException;

import vidada.model.metadata.MediaMetaAttribute;
import vidada.model.metadata.MetaDataSupport;

public class MetaDatatest {

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

			System.out.println("written attribute. reading now:");

			String res = metaDataSupport.readMetaData(file.toURI(), MediaMetaAttribute.FileHash);
			System.out.println("meta data: " + res);

		} catch (MetaDataNotSupportedException e) {
			e.printStackTrace();
		}


	}

}
