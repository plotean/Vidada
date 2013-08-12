package vidada.model.security;

@SuppressWarnings("serial")
public class AuthenticationException extends Exception {

	public AuthenticationException(){
		super("Authentication was not possible.");
	}

}
