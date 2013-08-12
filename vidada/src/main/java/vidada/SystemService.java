package vidada;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import archimedesJ.io.locations.ResourceLocation;
import vidada.model.system.ISystemService;
import vidada.util.WindowManager;

public class SystemService implements ISystemService{

	@Override
	public boolean open(ResourceLocation resource) {

		boolean success = false;

		if (resource.exists()) {
			try {
				try{
					System.out.println("shell open: " + resource);
					File file = new File(resource.getUri());
					Desktop.getDesktop().open(file);
					success = true;
				}catch(IllegalArgumentException e){
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return success;
	}

	@Override
	public void showResourceHome(ResourceLocation resource){
		try{
			File file = new File(resource.getUri());
			WindowManager.Instance.openFileSystemWindow(file);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
	}

}
