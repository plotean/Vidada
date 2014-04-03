package vidada.server;

import vidada.IVidadaServer;
import vidada.model.images.cache.crypto.CryptedCacheUtil;
import vidada.model.security.AuthenticationRequieredException;
import vidada.model.security.ICredentialManager;
import vidada.model.security.ICredentialManager.CredentialsChecker;
import vidada.server.dal.IVidadaDALService;
import vidada.server.impl.IPrivacyService;
import vidada.server.impl.PrivacyService;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IJobService;
import vidada.server.services.IMediaImportService;
import vidada.server.services.IMediaLibraryService;
import vidada.server.services.IMediaService;
import vidada.server.services.ITagService;
import vidada.server.services.IThumbnailService;
import vidada.server.services.MediaImportService;
import vidada.server.services.MediaLibraryService;
import vidada.server.services.MediaService;
import vidada.server.services.TagService;
import vidada.server.services.ThumbnailService;
import vidada.server.settings.DataBaseSettingsManager;
import vidada.server.settings.IDatabaseSettingsService;
import vidada.server.settings.VidadaServerSettings;
import vidada.services.ServiceProvider;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventListenerEx;
import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.security.CredentialType;
import archimedes.core.security.Credentials;

/**
 * Implements a Vidada Server
 * @author IsNull
 *
 */
public class VidadaServer implements IVidadaServer {


	/***************************************************************************
	 *                                                                         *
	 * Private Fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	private final IVidadaDALService dalService;

	private final IDatabaseSettingsService databaseSettingsService;
	private final IMediaLibraryService mediaLibraryService;
	private final IMediaService mediaService;
	private final ITagService tagService;
	private final IThumbnailService thumbnailService;
	private final IMediaImportService importService;
	private final IPrivacyService privacyService;



	private final IJobService jobService = null;// TODO



	/***************************************************************************
	 *                                                                         *
	 * Constructor                                                             *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates a new VidadaServer with the given DAL-Service.
	 * 
	 * @param dalService The DAL to use for persistence
	 */
	public VidadaServer(IVidadaDALService dalService){
		if(dalService == null)
			throw new IllegalArgumentException("dalService must not be NULL");
		this.dalService = dalService;

		databaseSettingsService = new DataBaseSettingsManager(this);
		mediaLibraryService = new MediaLibraryService(this);
		mediaService = new MediaService(this);
		tagService = new TagService(this);
		thumbnailService = new ThumbnailService(this);
		importService = new MediaImportService(this);
		privacyService = new PrivacyService(this);


		if(connectToDB()){
			// Create default data etc.
			// TODO
		}

		if(VidadaServerSettings.instance().isEnableNetworkSharing()){
			startNetworkSharing();
		}else {
			System.out.println("Network sharing is disabled.");
		}

	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Gets the DAL Service for this Vidada Server
	 * @return
	 */
	public IVidadaDALService getDalService(){
		return dalService;
	}

	public IDatabaseSettingsService getDatabaseSettingsService(){
		return databaseSettingsService;
	}


	public synchronized void startNetworkSharing(){
		startRestServer();
	}

	/***************************************************************************
	 *                                                                         *
	 * IVidadaServer API implementation                                        *
	 *                                                                         *
	 **************************************************************************/

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public IMediaService getMediaService() {
		return mediaService;
	}

	@Override
	public ITagService getTagService() {
		return tagService;
	}

	@Override
	public IThumbnailService getThumbnailService() {
		return thumbnailService;
	}

	@Override
	public IMediaLibraryService getLibraryService() {
		return mediaLibraryService;
	}

	@Override
	public IMediaImportService getImportService() {
		return importService;
	}

	@Override
	public IJobService getJobService() {
		throw new NotImplementedException();
	}

	@Override
	public String getNameId() {
		return "vidada.local"; 
	}


	/***************************************************************************
	 *                                                                         *
	 * Private Methods                                                         *
	 *                                                                         *
	 **************************************************************************/
	VidadaRestServer restServer;

	private synchronized void startRestServer(){
		if(restServer == null){
			System.out.println("Starting REST Server...");

			try{
				restServer = new VidadaRestServer(this);
				restServer.start();
			}catch(Throwable e){
				e.printStackTrace();
			}

		}else {
			System.err.println("startRestServer canceled, already running.");
		}
	}

	/**
	 * Connect to the database
	 * @return
	 */
	private boolean connectToDB(){

		//
		// EM is created successfully which indicates that we have a working db connection
		// hibernate has initialized
		//

		System.out.println("Checking user authentication...");

		registerProtectionHandler(privacyService);

		ICredentialManager credentialManager= ServiceProvider.Resolve(ICredentialManager.class);

		if(privacyService.isProtected()){
			System.out.println("Requesting user authentication for privacyService!");
			if(requestAuthentication(privacyService, credentialManager)){
				return true;
			}else {
				System.err.println("Autentification failed, aborting...");
				return false;
			}
		}else {
			System.out.println("No authentication necessary.");
			return true;
		}
	}


	private void registerProtectionHandler(final IPrivacyService privacyService){

		if(privacyService != null)
		{
			privacyService.getProtected().add(new EventListenerEx<EventArgs>() {
				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					try {
						final DirectoryLocation localCache = DirectoryLocation.Factory
								.create(VidadaServerSettings.instance().getAbsoluteCachePath());

						System.out.println("ServiceProvider: " + privacyService.getCredentials().toString());
						CryptedCacheUtil.encryptWithPassword(localCache, privacyService.getCredentials());
					} catch (AuthenticationRequieredException e) {
						e.printStackTrace();
					}
				}
			});


			privacyService.getProtectionRemoved().add(new EventListenerEx<EventArgs>() {

				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					try {
						final DirectoryLocation localCache = DirectoryLocation.Factory
								.create(VidadaServerSettings.instance().getAbsoluteCachePath());

						CryptedCacheUtil.removeEncryption( localCache, privacyService.getCredentials());
					} catch (AuthenticationRequieredException e) {
						e.printStackTrace();
					}
				}
			});
		}else
			System.err.println("CacheKeyProvider: IPrivacyService is not avaiable!");
	}

	/**
	 * Try to authenticate
	 * @param privacyService
	 * @param credentialManager
	 * @return
	 */
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
