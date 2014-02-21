package vidada.dal.hibernate.types;


/*
public class UriLocationType  implements CompositeUserType {

	@Override
	public String[] getPropertyNames() {
		// ORDER IS IMPORTANT!  it must match the order the columns are defined in the property mapping
		return new String[] { "uri", "creditals" };
	}

	@Override
	public Type[] getPropertyTypes() {
		return new Type[] { StringType.INSTANCE, CredentialType.INSTANCE };
	}

	public Class getReturnedClass() {
		return UniformLocation.class;
	}

	@Override
	public Object getPropertyValue(Object component, int propertyIndex) {
		if ( component == null ) {
			return null;
		}

		final UniformLocation location = (UniformLocation) component;
		switch ( propertyIndex ) {
		case 0: {
			return location.getUri().toString();
		}
		case 1: {
			return money.getCurrency();
		}
		default: {
			throw new HibernateException( "Invalid property index [" + propertyIndex + "]" );
		}
		}
	}

	@Override
	public void setPropertyValue(Object component, int propertyIndex, Object value) throws HibernateException {
		if ( component == null ) {
			return;
		}

		final UniformLocation location = (UniformLocation) component;
		switch ( propertyIndex ) {
		case 0: {
			location.setUri(new URI(value.toString()));
			break;
		}
		case 1: {
			location.setCreditals((Credentials)value);
			break;
		}
		default: {
			throw new HibernateException( "Invalid property index [" + propertyIndex + "]" );
		}
		}
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws SQLException {
		assert names.length == 2;
		BigDecimal amount = BigDecimalType.INSTANCE.get( names[0] ); // already handles null check
		Currency currency = CurrencyType.INSTANCE.get( names[1] ); // already handles null check
		return amount == null && currency == null
				? null
						: new Money( amount, currency );
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws SQLException {
		if ( value == null ) {
			BigDecimalType.INSTANCE.set( st, null, index );
			CurrencyType.INSTANCE.set( st, null, index+1 );
		}
		else {
			final Money money = (Money) value;
			BigDecimalType.INSTANCE.set( st, money.getAmount(), index );
			CurrencyType.INSTANCE.set( st, money.getCurrency(), index+1 );
		}
	}
}
 */