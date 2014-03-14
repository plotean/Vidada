package vidada.model.queries;


public class LiteralStringExpression extends LiteralValueExpression<String> {
	public LiteralStringExpression(String value) {
		super(value);
	}

	@Override
	public String code() {
		return "'" + getValue() + "'";
	}
}
