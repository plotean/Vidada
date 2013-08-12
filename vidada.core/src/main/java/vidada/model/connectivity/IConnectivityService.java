package vidada.model.connectivity;

import java.io.File;

public interface IConnectivityService {


	public abstract void exportMediaInfo(File exportDesitination);

	public void updateMediaDatas(File importTags);


}
