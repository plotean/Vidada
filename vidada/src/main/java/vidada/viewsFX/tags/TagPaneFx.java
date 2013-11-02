package vidada.viewsFX.tags;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import vidada.model.browser.TagStatesModel;
import vidada.model.browser.TagViewModel;
import vidada.model.tags.Tag;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventListenerEx;


public class TagPaneFx extends BorderPane {

	private final FlowPane tagsView = new FlowPane();

	private TagStatesModel tagsModel = null;

	public TagPaneFx(TagStatesModel tagsModel){

		setTop(tagsView);

		tagsView.prefWidthProperty().bind(this.widthProperty());
		tagsView.prefHeightProperty().bind(this.heightProperty());

		setDataContext(tagsModel);
	}


	public void setDataContext(TagStatesModel tagsModel){

		if(this.tagsModel != null){
			this.tagsModel.getTagsChangedEvent().remove(tagsChangedEventListener);
		}

		this.tagsModel = tagsModel;

		if(this.tagsModel != null){
			this.tagsModel.getTagsChangedEvent().add(tagsChangedEventListener);
		}
		updateModelToView();
	}

	EventListenerEx<CollectionEventArg<Tag>> tagsChangedEventListener = new EventListenerEx<CollectionEventArg<Tag>>() {
		@Override
		public void eventOccured(Object sender, CollectionEventArg<Tag> eventArgs) {
			updateModelToView();
		}
	};

	private void updateModelToView(){
		clearTags();
		if(tagsModel != null){
			addTags(tagsModel.getTagViewModels());
		}
	}



	Insets tagMargrin = new Insets(5,5,0,0);
	private void addTags(Iterable<TagViewModel> tags){
		for (TagViewModel tag : tags) {
			addTag(tag);
		}
	}

	private void addTag(TagViewModel tag){
		TagView tview = new TagView(tag);
		FlowPane.setMargin(tview, tagMargrin);
		tagsView.getChildren().add(tview);
	}

	private void clearTags(){
		tagsView.getChildren().clear();
	}


}
