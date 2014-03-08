package vidada;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import vidada.client.IVidadaClient;
import vidada.client.IVidadaClientManager;
import vidada.client.VidadaClientManager;
import vidada.client.local.LocalVidadaClient;
import vidada.dal.DAL;
import vidada.data.DatabaseConnectionException;
import vidada.images.RawImageFactoryFx;
import vidada.model.ServiceProvider;
import vidada.model.ServiceProvider.IServiceRegisterer;
import vidada.model.settings.VidadaClientSettings;
import vidada.model.settings.VidadaDatabase;
import vidada.model.system.ISystemService;
import vidada.selfupdate.SelfUpdateService;
import vidada.server.VidadaServer;
import vidada.server.dal.IVidadaDALService;
import vidada.server.settings.VidadaServerSettings;
import vidada.services.ISelfUpdateService;
import vidada.viewsFX.MainViewFx;
import vidada.viewsFX.dialoges.ChooseMediaDatabaseView;
import vidada.viewsFX.images.ImageViewerServiceFx;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.images.viewer.IImageViewerService;
import archimedesJ.services.ServiceLocator;
import archimedesJ.util.OSValidator;


public class Application extends  javafx.application.Application {



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

		launch(args);
	}

	private Stage primaryStage;
	private static IVidadaServer localserver;

	/**
	 * Gets the local server. 
	 * This might be null if no server is running.
	 * 
	 * @return
	 */
	public static IVidadaServer getLocalServer(){
		return localserver;
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		OSValidator.setForceHDPI(VidadaClientSettings.instance().isForceHDPIRender());

		ImageIO.setUseCache(false);

		try{
			System.out.println("initializing...");
			if(initialize()){
				System.out.println("initalized successfully.");
				afterStartup();

				// Show MainFrame
				showMainUI();
			}else
				stop();

		}catch(Exception e){
			e.printStackTrace();

			Dialogs.create()
			.title("Vidada Erorr")
			.masthead("Vidada encoutered an Error and has stopped working.")
			.showException(e);
		}
	}

	public void showMainUI(){
		try {
			primaryStage.setTitle("Vidada 2014");

			//AquaFx.style();

			Node contentPane = new MainViewFx();
			StackPane root = new StackPane();
			root.getChildren().add(contentPane);
			primaryStage.setScene(new Scene(root, 1080, 800));
			primaryStage.show();

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void stop(){
		System.out.println("MainFXContext.stop(): bye bye");
		Platform.exit();
		System.exit(0);
	}


	/**
	 * Occurs after the application has started up and is ready
	 */
	private void afterStartup() {

		/*
		DatabaseSettings settings = DataBaseSettingsManager.getSettings();

		if(settings.isNewDatabase()){

			DefaultDataCreator.createDefaultData();

			settings.setNewDatabase(false);
			DataBaseSettingsManager.persist(settings);
		}*/

		//IMediaLibraryManager libService = ServiceProvider.Resolve(IMediaService.class).getLocalMediaStore().getLibraryManager();

		//
		// Wizards
		//

		// TODO
		/*
		if(libService != null)
			if(libService.getAllLibraries().size() == 0){
				// no media libraries registered
				Action newLibAction = new AddNewMediaLibraryAction(MainFrame);
				newLibAction.actionPerformed(null);

				if(libService.getAllLibraries().size() != 0){
					Action updateMediaLibraryAction = new UpdateMediaLibraryAction(MainFrame); 
					updateMediaLibraryAction.actionPerformed(null);
				}
			}*/
	}

	private void configDatabase(){

		final VidadaServerSettings settings = VidadaServerSettings.instance();

		if(!settings.autoConfigDatabase()){

			System.out.println("database must be choosen by user:");

			Dialog dlg = new Dialog(null, "Vidada Database Chooser");
			final ChooseMediaDatabaseView chooseView = new ChooseMediaDatabaseView(settings.getAvaiableDatabases());
			final AbstractAction actionChoose = new AbstractAction("Choose") {
				{  
					ButtonBar.setType(this, ButtonType.OK_DONE); 
				}
				@Override
				public void execute(ActionEvent ae) {
					Dialog dlg = (Dialog) ae.getSource();
					VidadaDatabase db = chooseView.getDatabase();
					settings.setCurrentDBConfig(db);
					dlg.hide();
				}
			};
			dlg.setContent(chooseView);	
			dlg.setMasthead("Choose the media database which you want to open.");
			dlg.getActions().addAll(actionChoose, Dialog.Actions.CANCEL);
			dlg.show();

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
				locator.registerSingleton(IRawImageFactory.class, RawImageFactoryFx.class);
				locator.registerSingleton(IImageViewerService.class, ImageViewerServiceFx.class);
				locator.registerSingleton(ISelfUpdateService.class, SelfUpdateService.class); 

				locator.registerSingleton(IVidadaClientManager.class, VidadaClientManager.class);
			}
		});


		System.out.println("Loaded settings v" + VidadaClientSettings.instance().getSettingsVersion());


		configDatabase();


		if(VidadaServerSettings.instance().getCurrentDBConfig() == null){
			System.err.println("No Database has been choosen - exiting now");
			return false;
		}

		try{
			System.out.println("Settings up Vidada DAL...");
			IVidadaDALService vidadaDALService = DAL.build();
			System.out.println("DAL Layer loaded successfull.");

			System.out.println("Creating Vidada Server...");
			localserver = new VidadaServer(vidadaDALService);

			// Create a local client for the local server
			IVidadaClient localClient = new LocalVidadaClient(localserver);
			ServiceProvider.Resolve(IVidadaClientManager.class).addClient(localClient);

		}catch(DatabaseConnectionException e){
			e.printStackTrace();

			// notify user
			e.printStackTrace();
			Dialogs.create()
			.title("Vidada Erorr")
			.masthead("Vidada has trouble to access / connect to your database.")
			.showException(e);
		}
		return true;
	}

	/*
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
	};*/

}
