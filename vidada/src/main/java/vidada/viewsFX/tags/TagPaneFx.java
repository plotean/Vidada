package vidada.viewsFX.tags;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import vidada.controller.tags.ITagsView;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFilterState;
import archimedesJ.util.Lists;


public class TagPaneFx extends BorderPane implements ITagsView {

	private FlowPane tagsView = new FlowPane();

	public TagPaneFx(){
		//ObservableList<Tag> items = FXCollections.observableArrayList();
		Tag[] myTags = new Tag[] { new Tag("Action"), new Tag("Comedy"), new Tag("Horror"), new Tag("Si-Fi"),new Tag("Perfect")};
		//items.addAll(myTags);

		setTop(tagsView);

		addTags(Lists.asNoNullList(myTags)); // TODO

		tagsView.prefWidthProperty().bind(this.widthProperty());
		tagsView.prefHeightProperty().bind(this.heightProperty());
	}

	@Override
	public List<Tag> getTagsWithState(TagFilterState checked) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	Insets tagMargrin = new Insets(5,5,0,0);
	private void addTags(Iterable<Tag> tags){
		for (Tag tag : tags) {
			TagView tview = new TagView(tag);
			FlowPane.setMargin(tview, tagMargrin);
			tagsView.getChildren().add(tview);
		}
	}

}
