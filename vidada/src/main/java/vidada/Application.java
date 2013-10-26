package vidada;

import java.awt.Point;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import vidada.commands.AddNewMediaLibraryAction;
import vidada.commands.UpdateMediaLibraryAction;
import vidada.data.DatabaseConnectionException;
import vidada.data.DefaultDataCreator;
import vidada.data.SessionManager;
import vidada.images.RawImageFactoryFx;
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
import vidada.views.SwingMainContext;
import vidada.views.dialoges.AuthenticateDialog;
import vidada.views.dialoges.ChooseMediaDatabase;
import vidada.viewsFX.MainFXContext;
import archimedesJ.security.CredentialType;
import archimedesJ.security.Credentials;
import archimedesJ.services.ServiceLocator;
import archimedesJ.swing.util.SwingInvoker;
import archimedesJ.util.ExceptionUtil;
import archimedesJ.util.FileSupport;
import archimedesJ.util.OSValidator;

import com.db4o.ObjectContainer;


public class Application {



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


	transient private static final IMainUIContext mainUIContext = new SwingMainContext();

	private static void startApplication(){

		System.out.println("starting application main UI Thread");

		mainUIContext.showSplash();

		// start up application
		new Application();


	}


	/**
	 * Creates the application.
	 */
	public Application() {
		System.out.println("initializing...");
		if(initialize()){
			System.out.println("initalized successfully.");
			afterStartup();

			// Show MainFrame

			//mainUIContext.showMainUI();
			MainFXContext cgtx = new MainFXContext();
			cgtx.showMainUI();

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

	JFrame MainFrame = null;

	private void configDatabase(){

		GlobalSettings settings = GlobalSettings.getInstance();

		if(!settings.autoConfigDatabase()){

			System.out.println("database must be choosen by user:");

			ChooseMediaDatabase<VidadaDatabase> dbchooserDialog = 
					new ChooseMediaDatabase<VidadaDatabase>(MainFrame, settings.getAvaiableDatabases());
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
				locator.registerSingleton(RawImageFactory.class, RawImageFactoryFx.class);

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

				mainUIContext.hideSplash();


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



}
