package vidada.server;

import archimedes.core.events.EventArgs;
import archimedes.core.events.EventListenerEx;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.security.AuthenticationRequiredException;
import archimedes.core.security.CredentialType;
import archimedes.core.security.Credentials;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.IVidadaServer;
import vidada.model.images.cache.crypto.CryptedCacheUtil;
import vidada.model.security.ICredentialManager;
import vidada.server.dal.IVidadaDALService;
import vidada.server.impl.IPrivacyService;
import vidada.server.impl.PrivacyService;
import vidada.server.services.*;
import vidada.server.settings.DataBaseSettingsManager;
import vidada.server.settings.IDatabaseSettingsService;
import vidada.server.settings.VidadaServerSettings;
import vidada.services.ServiceProvider;

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

    private static final Logger logger = LogManager.getLogger(VidadaServer.class.getName());


    private final IVidadaDALService dalService;

	private final IDatabaseSettingsService databaseSettingsService;
	private final IMediaLibraryService mediaLibraryService;
	private final IMediaService mediaService;
	private final ITagService tagService;
	private final IThumbnailService thumbnailService;
	private final IMediaImportService importService;
	private final IPrivacyService privacyService;
	private final IJobService jobService;
    private final IUserService userService;


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
        jobService = new JobService(this);
        userService = new UserService(this);

		if(connectToDB()){
			// Create default data etc.
			// TODO
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
        return jobService;
	}

    @Override
    public IUserService getUserService() {  return userService; }

    @Override
	public String getNameId() {
		return "vidada.local"; 
	}


	/***************************************************************************
	 *                                                                         *
	 * Private Methods                                                         *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Connect to the database
	 * @return
	 */
	private boolean connectToDB(){

		//
		// EM is created successfully which indicates that we have a working db connection
		// hibernate has initialized
		//

        logger.info("Checking user authentication...");

		registerProtectionHandler(privacyService);

		ICredentialManager credentialManager= ServiceProvider.Resolve(ICredentialManager.class);

		if(privacyService.isProtected()){
            logger.info("Requesting user authentication for privacyService!");
			if(requestAuthentication(privacyService, credentialManager)){
				return true;
			}else {
                logger.info("Autentification failed, aborting...");
				return false;
			}
		}else {
            logger.info("No authentication necessary.");
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

                        logger.info("ServiceProvider: " + privacyService.getCredentials().toString());
						CryptedCacheUtil.encryptWithPassword(localCache, privacyService.getCredentials());
					} catch (AuthenticationRequiredException e) {
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
					} catch (AuthenticationRequiredException e) {
						e.printStackTrace();
					}
				}
			});
		}else
            logger.error("CacheKeyProvider: IPrivacyService is not available!");
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
                credentials -> privacyService.authenticate(credentials),
					false);

		return validCredentials != null;
	}


}
