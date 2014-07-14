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
import javafx.stage.StageStyle;
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
import vidada.client.ThreadUtil;
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
import vidada.model.settings.VidadaDatabaseConfig;
import vidada.model.settings.VidadaInstanceConfig;
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
import vidada.viewsFX.SplashScreen;
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

    private SplashScreen splashScreen;
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
     * @param initStage
     * @throws Exception
     */
	@Override
	public void start(Stage initStage) throws Exception {

        splashScreen = new SplashScreen();
        splashScreen.show(initStage);


        this.primaryStage = new Stage(StageStyle.DECORATED);
        ImageIO.setUseCache(false);

        try {
            logger.info("Initializing...");

            VidadaInstanceConfig instance = initialize();

            if(instance != null){
                logger.info("Starting Vidada with instance configured...");
                new Thread(() -> {
                    if(startVidadaInstance(instance)) {
                        // Show MainFrame
                        ThreadUtil.runUIThread(() -> showMainUI());
                    }else{
                        // Shut down
                        ThreadUtil.runUIThread(() -> stop());
                    }
                }).start();
            }else{
                logger.warn("Vidada instance config is NULL!");
                stop();
            }

        } catch (Throwable e) {
            logger.error("Vidada start failed.", e);

            ThreadUtil.runUIThread(() -> {
                Dialogs.create()
                        .title("Vidada Error")
                        .masthead("Vidada encountered an Error and has stopped working.")
                        .showException(e);
                stop();
            });
        }
    }



	public void showMainUI(){

        logger.info("Creating primary UI...");
		try {
			primaryStage.setTitle("Vidada 2014");

			Node contentPane = new MainViewFx();
			StackPane root = new StackPane();
			root.getChildren().add(contentPane);
			primaryStage.setScene(new Scene(root, 1080, 800));
			primaryStage.show();

            splashScreen.hide();
            primaryStage.toFront();

		}catch(Throwable e){
            logger.error("Failed to create UI",e);
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

    /**
     * Start Vidada with the given config
     * @param instanceConfig
     * @return
     */
    private boolean startVidadaInstance(VidadaInstanceConfig instanceConfig){
        boolean success = false;

        logger.info("Starting Vidada instance...");

        IVidadaClient vidadaClient;
        if(instanceConfig.equals(VidadaInstanceConfig.LOCAL)){
            vidadaClient = createLocalServerAndClient();
        }else{
            vidadaClient = connectToRemoteVidadaInstance(instanceConfig);
        }
        if(vidadaClient != null){
            ServiceProvider.Resolve(IVidadaClientManager.class).addClient(vidadaClient);
            logger.info("Initialisation successful.");
            afterStartup();
            success = true;
        }
        return success;
    }

	private VidadaInstanceConfig configInstance(){

        List<VidadaInstanceConfig> allVidadaInstanceConfigs = new ArrayList<>(VidadaClientSettings.instance().getVidadaInstances());

        logger.info("Configuring Vidada Instance form found " + allVidadaInstanceConfigs.size() + " configurations.");


        if(allVidadaInstanceConfigs.size() > 1) {
            logger.info("Multiple instance configurations are available, user has to choose...");

            // Multiple instances to choose from
            Dialog dlg = new Dialog(null, "Vidada Instance Chooser");
            final ChooseVidadaInstanceView chooseView = new ChooseVidadaInstanceView(allVidadaInstanceConfigs);
            final AbstractAction actionChoose = new AbstractAction("Choose") {
                {
                    ButtonBar.setType(this, ButtonType.OK_DONE);
                }

                @Override
                public void execute(ActionEvent ae) {
                    Dialog dlg = (Dialog) ae.getSource();
                    VidadaInstanceConfig instance = chooseView.getDatabase();
                    VidadaClientSettings.instance().setCurrentInstnace(instance);
                    dlg.hide();
                }
            };
            dlg.setContent(chooseView);
            dlg.setMasthead("Choose to which Vidada Instance you want to connect.");
            dlg.getActions().addAll(actionChoose, Dialog.Actions.CANCEL);
            dlg.show();

        }else if(allVidadaInstanceConfigs.size() == 1){
            logger.info("Automatically configure the only available instance.");
            VidadaClientSettings.instance().setCurrentInstnace(allVidadaInstanceConfigs.get(0));
        }else if(allVidadaInstanceConfigs.size() == 0){
            logger.warn("No instance configuration found. Check your client settings!");
        }

        logger.debug("Vidada instance configured.");

		return VidadaClientSettings.instance().getCurrentInstance(); 
	}


	private void configLocalServerDatabase(){

		final VidadaServerSettings settings = VidadaServerSettings.instance();

		if(!settings.autoConfigDatabase()){

			logger.info("Vidada instance / Database must be selected by user.");

            ThreadUtil.runUIThreadWait(() -> {
                Dialog dlg = new Dialog(null, "Vidada-Server Database Chooser");
                final ChooseMediaDatabaseView chooseView = new ChooseMediaDatabaseView(settings.getAvaiableDatabases());
                final AbstractAction actionChoose = new AbstractAction("Choose") {
                    {
                        ButtonBar.setType(this, ButtonType.OK_DONE);
                    }
                    @Override
                    public void execute(ActionEvent ae) {
                        Dialog dlg = (Dialog) ae.getSource();
                        VidadaDatabaseConfig db = chooseView.getDatabase();
                        settings.setCurrentDBConfig(db);
                        dlg.hide();
                    }
                };
                dlg.setContent(chooseView);
                dlg.setMasthead("Choose the media database which you want to open.");
                dlg.getActions().addAll(actionChoose, Dialog.Actions.CANCEL);
                dlg.show();
            });


		}else{
            logger.info("Instance / Database automatically configured.");
		}
	}

	/**
	 * Initialize Vidada
	 */
	private VidadaInstanceConfig initialize() {

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

		return configInstance();
	}


	private IVidadaClient connectToRemoteVidadaInstance(VidadaInstanceConfig instance){
		IVidadaClient vidadaClient = null;
		try {
			URI serverUri = new URI(instance.getUri());
			vidadaClient = new RestVidadaClient(serverUri);
		} catch (URISyntaxException e) {
			logger.error(e);
		}
		return vidadaClient;
	}

	private IVidadaClient createLocalServerAndClient(){
		IVidadaClient vidadaClient = null;

		configLocalServerDatabase();

		VidadaDatabaseConfig dbconfig = VidadaServerSettings.instance().getCurrentDBConfig();

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
            logger.error(e);
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

    private void loadUserTagRelations(IVidadaServer localServer) {

        for (MediaLibrary library : localServer.getLibraryService().getAllLibraries()) {
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
