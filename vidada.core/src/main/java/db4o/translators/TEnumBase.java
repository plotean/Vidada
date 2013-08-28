package db4o.translators;

import archimedesJ.enums.EnumConverter;

import com.db4o.ObjectContainer;
import com.db4o.config.ObjectConstructor;

/**
 * Base class for all enum constructors
 * @author IsNull
 *
 * @param <E>
 */
public abstract class TEnumBase<E extends Enum<E> & EnumConverter<E>> implements ObjectConstructor{

	@Override
	public final Object onInstantiate(ObjectContainer container, Object storedObject) {
		return onInstantiateEnum(container, (Byte)storedObject);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public final Object onStore(ObjectContainer container, Object applicationObject) {

		System.out.println("TEnumBase: store " + applicationObject.getClass().getCanonicalName());

		if(applicationObject instanceof EnumConverter){
			EnumConverter enumObj = (EnumConverter)applicationObject;
			return enumObj.convert();
		}
		return (byte)0x00;
	}

	@Override
	public final void onActivate(ObjectContainer container,
			Object applicationObject,
			Object storedObject) {
		// do nothing
	}


	public abstract E onInstantiateEnum(ObjectContainer container, byte storedObject);

	@Override
	public abstract Class storedClass();


}