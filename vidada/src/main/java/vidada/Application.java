package vidada;

import archimedes.core.images.IRawImageFactory;
import archimedes.core.images.viewer.IImageViewerService;
import archimedes.core.util.OSValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import vidada.handlers.IMediaHandler;
import vidada.images.RawImageFactoryFx;
import vidada.model.media.MediaLibrary;
import vidada.model.settings.MediaPlayerCommand;
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
import vidada.server.rest.VidadaRestServer;
import vidada.server.settings.VidadaServerSettings;
import vidada.services.IMediaPresenterService;
import vidada.services.ISelfUpdateService;
import vidada.services.ServiceProvider;
import vidada.viewsFX.MainViewFx;
import vidada.viewsFX.dialoges.ChooseMediaDatabaseView;
import vidada.viewsFX.dialoges.ChooseVidadaInstanceView;
import vidada.viewsFX.images.ImageOpenHandler;
import vidada.viewsFX.images.ImageViewerServiceFx;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Application extends  javafx.application.Application {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(Application.class.getName());
    private static IVidadaServer localserver;

    private Stage primaryStage;


    /***************************************************************************
     *                                                                         *
     * Primary Entry Point                                                     *
     *                                                                         *
     **************************************************************************/


	/**
	 * Primary entry point for this Application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {


		logger.info(System.getProperty("java.home"));
		logger.info(System.getProperty("java.vendor"));
		logger.info(System.getProperty("java.vendor.url"));
		logger.info(System.getProperty("java.version"));
        logger.info("Platform : " + OSValidator.getPlatformName());

		long maxBytes = Runtime.getRuntime().maxMemory();
        logger.info("Max memory: " + maxBytes / 1024 / 1024 + "MB");

		launch(args);
	}


    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    public Application(){
        super();
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the local server.
     * This might be null if no server is running.
     *
     * @return
     */
    public static IVidadaServer getLocalServer(){
        return localserver;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/


    /**
     * JavaFX start callback
     * @param primaryStage
     * @throws Exception
     */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		OSValidator.setForceHDPI(VidadaClientSettings.instance().isForceHDPIRender());

		ImageIO.setUseCache(false);

		try{
            logger.info("Initializing...");
			if(initialize()){
                logger.info("Initialisation successful.");
				afterStartup();

				// Show MainFrame
				showMainUI();
			}else {
                stop();
            }

		}catch(Throwable e){
            logger.error(e);

			Dialogs.create()
			.title("Vidada Erorr")
			.masthead("Vidada encountered an Error and has stopped working.")
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
            logger.error(e);
		}
	}

	@Override
	public void stop(){
		logger.info("Exiting Vidada.");
		Platform.exit();
		System.exit(0);
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/


	/**
	 * Occurs after the application has started up and is ready
	 */
	private void afterStartup() {

        registerMediaHandlers();

        if(getLocalServer() != null) {
            loadUserTagRelations(getLocalServer());
        }

	}

	private VidadaInstance configInstance(){

        logger.info("configuring Vidada Instance");

        List<VidadaInstance> allVidadaInstances = new ArrayList<>(VidadaClientSettings.instance().getVidadaInstances());

        if(allVidadaInstances.size() > 1) {
            // Multiple instances to choose from
            Dialog dlg = new Dialog(null, "Vidada Instance Chooser");
            final ChooseVidadaInstanceView chooseView = new ChooseVidadaInstanceView(allVidadaInstances);
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
        }else if(allVidadaInstances.size() == 1){
            // Only one instance, so we select it automatically
            VidadaClientSettings.instance().setCurrentInstnace(allVidadaInstances.get(0));
        }

		return VidadaClientSettings.instance().getCurrentInstance(); 
	}


	private void configLocalServerDatabase(){

		final VidadaServerSettings settings = VidadaServerSettings.instance();

		if(!settings.autoConfigDatabase()){

			logger.info("Vidada instance / Database must be selected by user.");

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
            logger.info("Instance / Database automatically configured.");
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
        logger.info("Loaded settings v" + VidadaClientSettings.instance().getSettingsVersion());

		// now we have to choose either to connect to a client or the local embedded vidada instance

		VidadaInstance instance = configInstance();

		if(instance != null){
			IVidadaClient vidadaClient;
			if(instance.equals(VidadaInstance.LOCAL)){
				vidadaClient = createLocalServerAndClient();
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

	private IVidadaClient createLocalServerAndClient(){
		IVidadaClient vidadaClient = null;

		configLocalServerDatabase();

		VidadaDatabase dbconfig = VidadaServerSettings.instance().getCurrentDBConfig();

		if(dbconfig == null){
            logger.info("No Database has been chosen - exiting now");
			return null;
		}

		try{
            logger.info("Setting up Vidada DAL...");

			IVidadaDALService vidadaDALService = DAL.build(new File(dbconfig.getDataBasePath()));
            logger.info("DAL Layer loaded successfully.");

            logger.info("Creating Vidada Server...");
			localserver = new VidadaServer(vidadaDALService);

			// Create a local client for the local server
			vidadaClient = new LocalVidadaClient(localserver);

            initializeWebServer(localserver);

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

    private static VidadaRestServer initializeWebServer(IVidadaServer server) {
        VidadaRestServer restServer = null;
        if (VidadaServerSettings.instance().isEnableNetworkSharing()) {

            logger.info("Creating VidadaRestServer Server.");

            try {
                restServer = new VidadaRestServer(server);
                logger.info("Starting VidadaRestServer...");
                restServer.start();
                logger.info("VidadaRestServer started!");
            } catch (Throwable e) {
                logger.error(e);
            }
        } else {
            logger.info("Network sharing is disabled.");
        }
        return restServer;
    }

    private void loadUserTagRelations(IVidadaServer localserver) {

        for (MediaLibrary library : localserver.getLibraryService().getAllLibraries()) {
            File def = library.getUserTagRelationDef();
            if (def.exists()) {

                // parse and merge it
                TagRelationDefinitionParser parser = new TagRelationDefinitionParser();
                try {
                    TagRelationDefinition relationDef = parser.parse(def);
                    logger.debug("TagRelationDefinition: " + relationDef);
                    relationDef.print();
                    getLocalServer().getTagService().mergeRelation(relationDef);
                } catch (IOException e) {
                    logger.error(e);
                } catch (ParseException e) {
                    logger.error(e);
                }
            } else {
                try {
                    def.getParentFile().mkdirs();
                    def.createNewFile();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }

    }

    private void registerMediaHandlers(){

        // register media play handlers
        IMediaPresenterService mediaPresenterService = ServiceProvider.Resolve(IMediaPresenterService.class);

        // register the internal image viewer

        IMediaHandler internalImageHandler = new ImageOpenHandler();
        mediaPresenterService.chainMediaHandler(internalImageHandler);

        // register external video players

        List<MediaPlayerCommand> externalPlayers =  Lists.newArrayList(VidadaClientSettings.instance().getExternalMediaPlayers());;
        Collections.reverse(externalPlayers);

        for(MediaPlayerCommand playerCommand : externalPlayers){
            IMediaHandler mediaHandler = new ExternalVideoProgramHandler(
                    playerCommand.getPlayerName(),
                    playerCommand.getCommand());
            mediaPresenterService.chainMediaHandler(mediaHandler);
        }
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
