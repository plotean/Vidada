package vidada.server;

import vidada.IVidadaServer;
import vidada.model.ServiceProvider;
import vidada.model.images.cache.crypto.CryptedCacheUtil;
import vidada.model.security.AuthenticationRequieredException;
import vidada.model.security.ICredentialManager;
import vidada.model.security.ICredentialManager.CredentialsChecker;
import vidada.model.settings.GlobalSettings;
import vidada.server.impl.IPrivacyService;
import vidada.server.impl.PrivacyService;
import vidada.server.services.MediaLibraryService;
import vidada.server.services.MediaService;
import vidada.server.services.TagService;
import vidada.server.services.ThumbnailService;
import vidada.services.IMediaLibraryService;
import vidada.services.IMediaService;
import vidada.services.ITagService;
import vidada.services.IThumbnailService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.io.locations.DirectoryLocation;
import archimedesJ.security.CredentialType;
import archimedesJ.security.Credentials;

public class VidadaServer implements IVidadaServer {

	private final IMediaLibraryService mediaLibraryService = new MediaLibraryService();
	private final IMediaService mediaService = new MediaService(mediaLibraryService);
	private final ITagService tagService = new TagService();
	private final IThumbnailService thumbnailService = new ThumbnailService();
	private final IPrivacyService privacyService = new PrivacyService();

	public VidadaServer(){
		// Etablish databas connection

		if(connectToDB()){
			// DefaultDataCreator
		}
	}


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
								.create(GlobalSettings.getInstance().getAbsoluteCachePath());

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
								.create(GlobalSettings.getInstance().getAbsoluteCachePath());

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
	public String getNameId() {
		return "vidada.local";
	}
}
