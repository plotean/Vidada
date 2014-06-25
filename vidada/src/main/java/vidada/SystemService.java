package vidada;

import archimedes.core.io.locations.ResourceLocation;
import vidada.model.system.ISystemService;
import vidada.util.WindowManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SystemService implements ISystemService{

	@Override
	public boolean open(ResourceLocation resource) {

		boolean success = false;

		if (resource.exists()) {
			try{
				File file = new File(resource.getUri());
				Desktop.getDesktop().open(file);
				success = true;
			}catch(IllegalArgumentException | IOException e){
				e.printStackTrace();
			}
		}

		return success;
	}

	@Override
	public void showResourceHome(ResourceLocation resource){
		if(resource == null) return;

		try{
			File file = new File(resource.getUri());
			WindowManager.Instance.openFileSystemWindow(file);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
	}

}
