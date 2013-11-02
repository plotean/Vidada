package vidada.viewsFX.tags;



import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFilterState;
import vidada.viewsFX.util.IDataContext;

/**
 * A View representing a single Tag
 * @author IsNull
 *
 */
public class TagView extends HBox implements IDataContext<Tag> {
	private final Label tagname;
	private final Circle tagCircle;
	private TagFilterState state = TagFilterState.Allowed;
	//private CheckBox chkboxBox;

	public TagView(Tag tag){
		this();
		setDataContext(tag);
	}

	public TagView(){

		this.setId("tagview");
		this.getStylesheets().add("css/default_tagview.css");

		tagCircle = new Circle();
		tagCircle.setRadius(4);

		tagCircle.setId("tag-dot");
		//tagCircle.setFill(Color.LIGHTGREY);
		//tagCircle.setStroke(Color.web("#707070"));

		tagname = new Label("Cool");
		tagname.setAlignment(Pos.CENTER);


		this.setSpacing(5);
		this.setAlignment(Pos.CENTER);
		this.getChildren().addAll(tagCircle, tagname);
	}

	public TagFilterState getState() { return state; }

	@Override
	public void setDataContext(final Tag context) {
		if(context != null){
			if(context.getName() != null)
				tagname.setText(context.getName());
			else
				tagname.setText("<null>");
		}else {
			tagname.setText("<no datacontext>");
		}
	}

}
