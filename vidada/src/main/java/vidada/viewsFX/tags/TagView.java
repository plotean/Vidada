package vidada.viewsFX.tags;



import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import vidada.model.tags.TagState;
import vidada.viewmodel.tags.TagViewModel;
import vidada.viewsFX.util.IDataContext;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;

/**
 * A View representing a single Tag
 * @author IsNull
 *
 */
public class TagView extends HBox implements IDataContext<TagViewModel> {
	private final Label tagname;
	private final Circle tagCircle;

	private TagViewModel tag;

	//private CheckBox chkboxBox;

	public TagView(TagViewModel tag){
		this();
		setDataContext(tag);
	}

	public final static String TAGVIEW_ID = "tagview";

	public TagView(){

		setStateStyle(TagState.Allowed);
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


		this.addEventHandler(MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent me) {
				if(tag != null){
					tag.toggleState();
				}
			}});
	}

	@Override
	public TagViewModel getDataContext() {
		return this.tag;
	}


	@Override
	public void setDataContext(final TagViewModel tagvm) {
		if(tag != null)
			tag.getTagStateChangedEvent().remove(tagStateChangedListener);

		this.tag = tagvm;
		if(tagvm != null){
			tagvm.getTagStateChangedEvent().add(tagStateChangedListener);
		}
		updateTagView();
	}

	private void updateTagView(){
		if(tag != null){
			tagname.setText(tag.getName());
			setStateStyle(tag.getState()); // set the css style id

		}else {
			tagname.setText("<no datacontext>");
			setStateStyle(TagState.Allowed);
		}
	}

	private void setStateStyle(TagState state){
		String currentStyle = TAGVIEW_ID + "-"+ state.toString().toLowerCase();
		//System.out.println("set tag style: " + currentStyle);
		this.setId(currentStyle);
	}


	private final EventListenerEx<EventArgsG<TagViewModel>> tagStateChangedListener 
	= new EventListenerEx<EventArgsG<TagViewModel>>() {
		@Override
		public void eventOccured(Object sender, EventArgsG<TagViewModel> eventArgs) {
			updateTagView();
		}
	};




}
