package vidada;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import vidada.model.system.ISystemService;
import vidada.util.WindowManager;
import archimedesJ.io.locations.ResourceLocation;

public class SystemService implements ISystemService{

	@Override
	public boolean open(ResourceLocation resource) {

		boolean success = false;

		if (resource.exists()) {
			try{
				System.out.println("shell open: " + resource);
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
