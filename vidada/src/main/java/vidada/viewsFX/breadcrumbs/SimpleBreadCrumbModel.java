package vidada.viewsFX.breadcrumbs;


public class SimpleBreadCrumbModel implements IBreadCrumbModel {

	private final String name;

	public SimpleBreadCrumbModel(String name){
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
