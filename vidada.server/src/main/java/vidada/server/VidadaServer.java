package vidada.server;

import vidada.IVidadaServer;
import vidada.model.ServiceProvider;
import vidada.model.security.ICredentialManager;
import vidada.model.security.ICredentialManager.CredentialsChecker;
import vidada.model.security.IPrivacyService;
import vidada.services.IMediaService;
import vidada.services.ITagService;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.security.CredentialType;
import archimedesJ.security.Credentials;

public class VidadaServer implements IVidadaServer {


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
			return true;
		}else {
			System.err.println("Authentication failed. Quitting application now.");
		}
		return false;
	}

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
		throw new NotImplementedException();
	}

	@Override
	public ITagService getTagService() {
		throw new NotImplementedException();
	}

	@Override
	public String getNameId() {
		return "vidada.local";
	}

}
