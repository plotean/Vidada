package db4o.translators;

import com.db4o.ObjectContainer;
import com.db4o.config.ObjectConstructor;

public class TLoggerProxy implements ObjectConstructor{


	private ObjectConstructor oc;

	public TLoggerProxy(){
		this(null);
	}

	public TLoggerProxy(ObjectConstructor oc){
		this.oc = oc;
	}


	@Override
	public void onActivate(ObjectContainer arg0, Object arg1, Object arg2) {
		if(arg1 != null){
			System.out.println("TLoggerProxy: onActivate: " + arg1.getClass().getCanonicalName());
		}
		if(oc != null)
			this.oc.onActivate(arg0, arg1, arg2);
	}

	@Override
	public Object onStore(ObjectContainer arg0, Object obj) {
		if(obj != null){
			System.out.println("TLoggerProxy: storing: " + obj.getClass().getCanonicalName());
		}

		return (oc != null) ? this.oc.onStore(arg0, arg0) : (byte)0x00;
	}

	@Override
	public Class storedClass() {
		return (oc != null) ? this.oc.storedClass() : byte.class;
	}

	@Override
	public Object onInstantiate(ObjectContainer arg0, Object arg1) {
		if(arg1 != null){
			System.out.println("TLoggerProxy: storing: " + arg1.getClass().getCanonicalName());
		}
		return (oc != null) ? this.oc.onInstantiate(arg0, arg1) : null;
	}

}
