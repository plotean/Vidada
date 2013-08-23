package vidada.model.connectivity;

import java.io.File;

import archimedesJ.services.IService;

public interface IConnectivityService extends IService{


	public abstract void exportMediaInfo(File exportDesitination);

	public void updateMediaDatas(File importTags);


}
