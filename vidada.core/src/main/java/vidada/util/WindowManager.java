package vidada.util;

import java.io.File;
import java.io.IOException;

import archimedesJ.util.OSValidator;

public abstract class WindowManager {

	public static final WindowManager Instance = buildWindowManager();

	private WindowManager() { }

	/**
	 * Open the containing folder in the OS default file manager
	 * and select the given file.
	 * @param file
	 */
	public abstract void openFileSystemWindow(File file);


	private static WindowManager buildWindowManager() {
		WindowManager manager;

		if(OSValidator.isWindows()){
			manager = new WindowManagerWindows();
		}else if(OSValidator.isOSX()) {
			manager = new WindowManagerOSX();
		}else{
			manager = null;
		}

		return manager;
	}



	private static class WindowManagerWindows extends WindowManager
	{

		@Override
		public void openFileSystemWindow(File file) {
			try {
				Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}

	}

	private static class WindowManagerOSX extends WindowManager
	{

		@Override
		public void openFileSystemWindow(File file) {
			try {
				String[] args = new String[]{ "/usr/bin/open","-R", file.getAbsolutePath()};
				try {
					Runtime.getRuntime().exec(args).waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}  

		}

	}


}
