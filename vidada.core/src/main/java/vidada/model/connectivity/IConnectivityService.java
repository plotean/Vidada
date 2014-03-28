package vidada.model.connectivity;

import archimedes.core.services.IService;

import java.io.File;

public interface IConnectivityService extends IService{


	public abstract void exportMediaInfo(File exportDesitination);

	public void updateMediaDatas(File importTags);


}
