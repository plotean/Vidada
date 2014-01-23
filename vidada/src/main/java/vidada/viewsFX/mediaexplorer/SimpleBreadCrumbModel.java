package vidada.viewsFX.mediaexplorer;


/**
 * Simple implementation of {@link IBreadCrumbModel}
 * @author IsNull
 *
 */
public class SimpleBreadCrumbModel {

	private final String name;

	public SimpleBreadCrumbModel(String name){
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	@Override
	public String toString(){
		return getName();
	}
}
