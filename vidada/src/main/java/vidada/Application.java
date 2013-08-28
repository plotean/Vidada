package vidada;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import vidada.commands.AddNewMediaLibraryAction;
import vidada.commands.UpdateMediaLibraryAction;
import vidada.data.DatabaseConnectionException;
import vidada.data.DefaultDataCreator;
import vidada.data.SessionManager;
import vidada.images.RawImageFactoryAwt;
import vidada.model.ServiceProvider;
import vidada.model.ServiceProvider.IServiceRegisterer;
import vidada.model.images.RawImageFactory;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.security.ICredentialManager;
import vidada.model.security.ICredentialManager.CredentialsChecker;
import vidada.model.security.ICredentialManager.CredentialsProvider;
import vidada.model.security.IPrivacyService;
import vidada.model.settings.DatabaseSettings;
import vidada.model.settings.GlobalSettings;
import vidada.model.settings.VidadaDatabase;
import vidada.model.system.ISystemService;
import vidada.views.MainFrame;
import vidada.views.VidadaSplash;
import vidada.views.dialoges.AuthenticateDialog;
import vidada.views.dialoges.ChooseMediaDatabase;
import archimedesJ.security.CredentialType;
import archimedesJ.security.Credentials;
import archimedesJ.services.ServiceLocator;
import archimedesJ.swing.util.SwingInvoker;
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

		ImageIO.setUseCache(false);

		try{
			startApplication();
		}catch(Exception e){
			e.printStackTrace();

			JOptionPane.showMessageDialog(null, 
					e.getStackTrace(),
					"Vidada - " + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}




	static VidadaSplash customSplash;

	private static void startApplication(){

		System.out.println("starting application main UI Thread");

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {

					// create splash
					customSplash = new VidadaSplash();
					customSplash.setVisible(true);
					customSplash.setAlwaysOnTop(true);
					customSplash.toFront();
					customSplash.setAlwaysOnTop(false);

					// define visual appearance
					setTheme();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// start up application
		new Application();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(customSplash!= null && customSplash.isVisible())
					customSplash.setVisible(false);
			}
		});
	}


	/**
	 * Creates the application.
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
				locator.registerSingleton(RawImageFactory.class, RawImageFactoryAwt.class);

				ICredentialManager credentialManager = locator.resolve(ICredentialManager.class);
				credentialManager.register(authProvider);
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

			e.printStackTrace();

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
			System.out.println("EM created sucessfully.");

			//
			// EM is created successfully which indicates that we have a working db connection
			// hibernate has initialized
			//

			System.out.println("Checking user authentication...");

			IPrivacyService privacyService = ServiceProvider.Resolve(IPrivacyService.class);
			ICredentialManager credentialManager= ServiceProvider.Resolve(ICredentialManager.class);

			if(privacyService == null) return false;

			if(privacyService.isProtected()){
				System.out.println("Requesting user authentication for privacyService!");
				if(!requestAuthentication(privacyService, credentialManager)){
					System.err.println("Autentification failed, aborting...");
					return false;
				}
			}else {
				System.out.println("No authentication necessary.");
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






	private final CredentialsProvider authProvider = new CredentialsProvider() {
		@Override
		public Credentials authenticate(String domain, String description, CredentialType type) {

			System.out.println("CredentialsProvider->authenticate");

			final CountDownLatch latch = new CountDownLatch(1);
			final AuthenticateDialog authDlg = new AuthenticateDialog(null, type, description);

			System.out.println("invoking async auth dialog...");

			SwingInvoker.invokeLater(new Runnable() {
				@Override
				public void run() {
					Point lPoint = authDlg.getLocation();
					//lPoint.y = lPoint.y + (int)((float)MainFrame.getHeight() / 1.5f);
					authDlg.setLocation(lPoint);
					System.out.println("showing dialog");
					authDlg.setVisible(true);

					System.out.println("dialog done!");
					latch.countDown();
				}
			});

			try {
				System.out.println("waiting for latch...");
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return authDlg.isOk() 
					? authDlg.getCredentials()
							: null;
		}
	};




	private boolean requestAuthentication(final IPrivacyService privacyService, ICredentialManager credentialManager){

		Credentials validCredentials = credentialManager.requestAuthentication(
				"vidada.core",
				"Please enter the Database password:",
				CredentialType.PasswordOnly,
				new CredentialsChecker(){
					@Override
					public boolean check(Credentials credentials) {
						return privacyService.authenticate(credentials);
					}},
					false);

		return validCredentials != null;
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
