package vidada.model.security;

@SuppressWarnings("serial")
public class AuthenticationRequieredException extends Exception {

	public AuthenticationRequieredException(){
		super("Prior authentication is required in order to access this method.");
	}

}
