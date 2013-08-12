package vidada;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import vidada.commands.AddNewMediaLibraryAction;
import vidada.commands.UpdateMediaLibraryAction;
import vidada.data.DefaultDataCreator;
import vidada.data.DatabaseConnectionException;
import vidada.data.SessionManager;
import vidada.model.ServiceProvider;
import vidada.model.ServiceProvider.IServiceRegisterer;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.security.AuthenticationException;
import vidada.model.security.IPrivacyService;
import vidada.model.settings.DatabaseSettings;
import vidada.model.settings.GlobalSettings;
import vidada.model.settings.VidadaDatabase;
import vidada.model.system.ISystemService;
import vidada.views.MainFrame;
import vidada.views.VidadaSplash;
import vidada.views.dialoges.AuthenticateDialog;
import vidada.views.dialoges.ChooseMediaDatabase;
import archimedesJ.services.ServiceLocator;
import archimedesJ.util.ExceptionUtil;
import archimedesJ.util.FileSupport;
import archimedesJ.util.OSValidator;

import com.db4o.ObjectContainer;


public class Application {

	public static JFrame MainFrame;

	/**
	 * Primary entry point for this Application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		//
		// print some system infos
		//

		System.out.println(System.getProperty("java.home"));
		System.out.println(System.getProperty("java.vendor"));
		System.out.println(System.getProperty("java.vendor.url"));
		System.out.println(System.getProperty("java.version"));

		long maxBytes = Runtime.getRuntime().maxMemory();
		System.out.println("Max memory: " + maxBytes / 1024 / 1024 + "MB");

		OSValidator.setForceHDPI(GlobalSettings.getInstance().isForceHDPIRender());

		try{
			startApplication();
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, 
					e.getStackTrace(),
					"Vidada - " + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}




	static VidadaSplash customSplash;

	private static void startApplication(){

		System.out.println("starting application...");

		customSplash = new VidadaSplash();
		customSplash.setVisible(true);
		customSplash.setAlwaysOnTop(true);
		customSplash.toFront();
		//customSplash.requestFocus();
		customSplash.setAlwaysOnTop(false);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {

					// define visual appearance
					setTheme();

					// start up application
					new Application();

					if(customSplash!= null && customSplash.isVisible())
						customSplash.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the application.
	 */
	public Application() {
		System.out.println("initializing...");
		if(initialize()){
			System.out.println("initalized successfully.");
			afterStartup();
		}else
			System.exit(-1);
	}

	/**
	 * Occurs after the application has started up and is ready
	 */
	private void afterStartup() {

		DatabaseSettings settings = DatabaseSettings.getSettings();

		if(settings.isNewDatabase()){

			DefaultDataCreator.createDefaultData();

			settings.setNewDatabase(false);
			settings.persist();
		}

		IMediaLibraryService libService = ServiceProvider.Resolve(IMediaLibraryService.class);

		//
		// Wizards
		//

		if(libService != null)
			if(libService.getAllLibraries().size() == 0){
				// no media libraries registered
				Action newLibAction = new AddNewMediaLibraryAction(MainFrame);
				newLibAction.actionPerformed(null);

				if(libService.getAllLibraries().size() != 0){
					Action updateMediaLibraryAction = new UpdateMediaLibraryAction(MainFrame); 
					updateMediaLibraryAction.actionPerformed(null);
				}
			}
	}

	private void configDatabase(){

		GlobalSettings settings = GlobalSettings.getInstance();

		if(!settings.autoConfigDatabase()){

			System.out.println("database must be choosen by user:");

			ChooseMediaDatabase<VidadaDatabase> dbchooserDialog = 
					new ChooseMediaDatabase<VidadaDatabase>(Application.MainFrame, settings.getAvaiableDatabases());
			dbchooserDialog.setVisible(true);

			VidadaDatabase db = dbchooserDialog.getChoosenDB();
			settings.setCurrentDBConfig(db);
		}else{
			System.out.println("database automatically configured.");
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private boolean initialize() {

		ServiceProvider.getInstance().startup(new IServiceRegisterer(){
			@Override
			public void registerServices(ServiceLocator locator) {
				locator.registerSingleton(ISystemService.class, SystemService.class);
			}
		});


		ObjectContainer em = null;

		System.out.println("loaded settings v" + GlobalSettings.getInstance().getSettingsVersion());


		configDatabase();


		if(GlobalSettings.getInstance().getCurrentDBConfig() == null){
			System.err.println("No Database has been choosen - exiting now");
			return false;
		}


		try{
			System.out.println("setting up EntityManager...");
			em = SessionManager.getObjectContainer();
		}catch(DatabaseConnectionException e){
			e.printStackTrace();
			em = null;

			// notify user

			String exceptionDetail = ExceptionUtil.exeptionToUserErrorString(e);

			JOptionPane.showMessageDialog(null, 
					"Vidada has trouble to access your database. The database access layer has reported:" + FileSupport.NEWLINE +
					FileSupport.newlines(2) +
					exceptionDetail + FileSupport.newlines(2) +
					"Vidada quits now.",
					"Vidada - Database connection issue",
					JOptionPane.ERROR_MESSAGE);
		}

		if(em != null)
		{
			System.out.println("EM created sucessfully");

			//
			// EM is created successfully which indicates that we have a working db connection
			// hibernate has initialized
			//
			IPrivacyService privacyService = ServiceProvider.Resolve(IPrivacyService.class);

			if(privacyService == null) return false;

			if(privacyService.isProtected()){
				requestAuthentication(privacyService);
			}

			if(privacyService.isAuthenticated() || !privacyService.isProtected())
			{
				// Show MainFrame
				System.out.println("init application frame...");
				MainFrame = new MainFrame();
				MainFrame.setLocationRelativeTo(null);
				MainFrame.setAlwaysOnTop(true);
				MainFrame.toFront();
				MainFrame.requestFocus();
				MainFrame.setAlwaysOnTop(false);
				MainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

				return true;
			}else {
				System.err.println("Authentication failed. Quitting application now.");
			}
		}else{
			System.err.println("EntityManager could not be created.");
		}

		return false;
	}

	private void requestAuthentication(IPrivacyService privacyService){

		boolean success = false;
		AuthenticateDialog authDlg;

		while(!success) {
			authDlg = new AuthenticateDialog(customSplash);

			Point lPoint = authDlg.getLocation();
			lPoint.y = lPoint.y + (int)((float)customSplash.getHeight() / 1.5f);
			authDlg.setLocation(lPoint);

			authDlg.setVisible(true);

			if(!authDlg.isOk()) break;

			try {
				success = privacyService.authenticate(authDlg.getPassword());
			} catch (AuthenticationException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	static boolean useTheme = true;
	private static void setTheme(){

		if(useTheme && !OSValidator.isOSX()){
			try {
				// Default
				//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

				//Nimbus
				for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());


						UIDefaults defaults = UIManager.getLookAndFeelDefaults();
						defaults.put("Panel.background", Color.WHITE); // we want a white background as an overall design rule
						defaults.put("OptionPane.background", Color.WHITE); // dito
						defaults.put("ToolPane.background", Color.DARK_GRAY);

						break;
					}
				}
			} catch (Exception e) {
				// If Nimbus is not available, you can set the GUI to another look and feel.
			}
		}



	}

}
