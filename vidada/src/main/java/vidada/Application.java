package vidada;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
import vidada.client.rest.RestVidadaClient;
import vidada.dal.DAL;
import vidada.data.DatabaseConnectionException;
import vidada.handlers.ExternalVideoProgramHandler;
import vidada.images.RawImageFactoryFx;
import vidada.model.media.MediaLibrary;
import vidada.model.settings.VidadaClientSettings;
import vidada.model.settings.VidadaDatabase;
import vidada.model.settings.VidadaInstance;
import vidada.model.system.ISystemService;
import vidada.model.tags.relations.TagRelationDefinition;
import vidada.model.tags.relations.TagRelationDefinitionParser;
import vidada.model.tags.relations.TagRelationDefinitionParser.ParseException;
import vidada.selfupdate.SelfUpdateService;
import vidada.server.VidadaServer;
import vidada.server.dal.IVidadaDALService;
import vidada.server.settings.VidadaServerSettings;
import vidada.services.IMediaPresenterService;
import vidada.services.ISelfUpdateService;
import vidada.services.ServiceProvider;
import vidada.viewsFX.MainViewFx;
import vidada.viewsFX.dialoges.ChooseMediaDatabaseView;
import vidada.viewsFX.dialoges.ChooseVidadaInstanceView;
import vidada.viewsFX.images.ImageViewerServiceFx;
import archimedesJ.images.IRawImageFactory;
import archimedesJ.images.viewer.IImageViewerService;
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

		// register media play handlers
		IMediaPresenterService mediaPresenterService = ServiceProvider.Resolve(IMediaPresenterService.class);

		String externalVideoPlayer = VidadaClientSettings.instance().getVideoPlayer();
		if(externalVideoPlayer != null && !externalVideoPlayer.isEmpty()){
			mediaPresenterService.chainMediaHandler(new ExternalVideoProgramHandler(externalVideoPlayer));
		}


		// read user tag definitions
		if(getLocalServer() != null){
			for (MediaLibrary library : getLocalServer().getLibraryService().getAllLibraries()) {
				File def = library.getUserTagRelationDef();
				if(def.exists()){

					// parse and merge it
					TagRelationDefinitionParser parser = new TagRelationDefinitionParser();
					try {
						TagRelationDefinition relationDef = parser.parse(def);
						System.out.println("TagRelationDefinition: " + relationDef);
						relationDef.print();
						getLocalServer().getTagService().mergeRelation(relationDef);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else{
					try {
						def.getParentFile().mkdirs();
						def.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}



	}

	private VidadaInstance configInstance(){

		System.out.println("configuring Vidada Instance");

		Dialog dlg = new Dialog(null, "Vidada Instance Chooser");
		final ChooseVidadaInstanceView chooseView =new ChooseVidadaInstanceView(VidadaClientSettings.instance().getVidadaInstances());
		final AbstractAction actionChoose = new AbstractAction("Choose") {
			{  
				ButtonBar.setType(this, ButtonType.OK_DONE); 
			}
			@Override
			public void execute(ActionEvent ae) {
				Dialog dlg = (Dialog) ae.getSource();
				VidadaInstance instance = chooseView.getDatabase();
				VidadaClientSettings.instance().setCurrentInstnace(instance);
				dlg.hide();
			}
		};
		dlg.setContent(chooseView);	
		dlg.setMasthead("Choose to which Vidada Instance you want to connect.");
		dlg.getActions().addAll(actionChoose, Dialog.Actions.CANCEL);
		dlg.show();

		return VidadaClientSettings.instance().getCurrentInstance(); 
	}


	private void configLocalServerDatabase(){

		final VidadaServerSettings settings = VidadaServerSettings.instance();

		if(!settings.autoConfigDatabase()){

			System.out.println("Vidada instance must be choosen by user:");

			Dialog dlg = new Dialog(null, "Vidada-Server Database Chooser");
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
	 * Initialize Vidada
	 */
	private boolean initialize() {

		// Register global services
		ServiceProvider.getInstance().startup(locator -> {
            locator.registerSingleton(ISystemService.class, SystemService.class);
            locator.registerSingleton(IRawImageFactory.class, RawImageFactoryFx.class);
            locator.registerSingleton(IImageViewerService.class, ImageViewerServiceFx.class);
            locator.registerSingleton(ISelfUpdateService.class, SelfUpdateService.class);

            locator.registerSingleton(IVidadaClientManager.class, VidadaClientManager.class);
        });
		System.out.println("Loaded settings v" + VidadaClientSettings.instance().getSettingsVersion());

		// now we have to choose either to connect to a client or the local embedded vidada instance

		VidadaInstance instance = configInstance();

		if(instance != null){
			IVidadaClient vidadaClient;
			if(instance.equals(VidadaInstance.LOCAL)){
				vidadaClient = createlocalServerAndClient();
			}else{
				vidadaClient = connectToRemoteVidadaInstance(instance);
			}

			if(vidadaClient != null){
				ServiceProvider.Resolve(IVidadaClientManager.class).addClient(vidadaClient);
				return true;
			}
		}
		return false;
	}


	private IVidadaClient connectToRemoteVidadaInstance(VidadaInstance instance){
		IVidadaClient vidadaClient = null;
		try {
			URI serverUri = new URI(instance.getUri());
			vidadaClient = new RestVidadaClient(serverUri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return vidadaClient;
	}

	private IVidadaClient createlocalServerAndClient(){
		IVidadaClient vidadaClient = null;

		configLocalServerDatabase();

		VidadaDatabase dbconfig = VidadaServerSettings.instance().getCurrentDBConfig();

		if(dbconfig == null){
			System.err.println("No Database has been chosen - exiting now");
			return null;
		}

		try{
			System.out.println("Settings up Vidada DAL...");

			IVidadaDALService vidadaDALService = DAL.build(new File(dbconfig.getDataBasePath()));
			System.out.println("DAL Layer loaded successfully.");

			System.out.println("Creating Vidada Server...");
			localserver = new VidadaServer(vidadaDALService);

			// Create a local client for the local server
			vidadaClient = new LocalVidadaClient(localserver);
		}catch(DatabaseConnectionException e){
			e.printStackTrace();

			// notify user
			e.printStackTrace();
			Dialogs.create()
			.title("Vidada Erorr")
			.masthead("Vidada has trouble to access / connect to your database.")
			.showException(e);
		}

		return vidadaClient;
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
