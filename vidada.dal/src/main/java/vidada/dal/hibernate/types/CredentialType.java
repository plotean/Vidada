package vidada.dal.hibernate.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import archimedesJ.security.Credentials;

public class CredentialType implements CompositeUserType {

	public final static CredentialType INSTANCE = new CredentialType();


	@Override
	public String[] getPropertyNames() {
		return new String[] { "domain", "username", "password" };
	}

	@Override
	public Type[] getPropertyTypes() {
		return new Type[] { StringType.INSTANCE, StringType.INSTANCE,StringType.INSTANCE};
	}

	@Override
	public Object getPropertyValue(Object component, int propertyIndex)
			throws HibernateException {
		if ( component == null ) {
			return null;
		}

		final Credentials credentials = (Credentials) component;
		switch ( propertyIndex ) {
		case 0: {
			return credentials.getDomain();
		}
		case 1: {
			return credentials.getUsername();
		}
		case 2: {
			return credentials.getPassword();
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

		final Credentials credentials = (Credentials) component;
		switch ( propertyIndex ) {
		case 0: {
			credentials.setDomain(value.toString());
			break;
		}
		case 1: {
			credentials.setUsername(value.toString());
			break;
		}
		case 2: {
			credentials.setPassword(value.toString());
			break;
		}
		default: {
			throw new HibernateException( "Invalid property index [" + propertyIndex + "]" );
		}
		}


	}

	@Override
	public Class returnedClass() {
		return Credentials.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
					throws HibernateException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Serializable disassemble(Object value, SessionImplementor session)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object assemble(Serializable cached, SessionImplementor session,
			Object owner) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}


}
