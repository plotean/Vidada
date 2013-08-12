package vidada.model.media;

import java.io.File;
import java.util.Map;

public class MediaImportMatrix {

	private final Map<String, MediaItem> existingMediaData;
	private final Map<File, String> fileContentMap;


	public MediaImportMatrix(Map<String, MediaItem> existingMediaData, Map<File, String> fileContentMap){
		this.existingMediaData = existingMediaData;
		this.fileContentMap = fileContentMap;
	}


	public void process(){



	}








}
