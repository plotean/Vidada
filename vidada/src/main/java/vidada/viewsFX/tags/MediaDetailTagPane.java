package vidada.viewsFX.tags;

import javafx.scene.layout.BorderPane;
import vidada.model.tags.TagState;
import vidada.viewmodel.ITagStatesVMProvider;
import vidada.viewmodel.tags.TagViewModel;
import archimedesJ.expressions.Predicate;


public class MediaDetailTagPane extends BorderPane {

	private final TagPaneFx currentTagsPanel;
	private final TagPaneFx avaiableTagsPanel;


	public MediaDetailTagPane() {
		currentTagsPanel = new TagPaneFx();
		avaiableTagsPanel = new TagPaneFx();



		currentTagsPanel.setFilter(new Predicate<TagViewModel>() {
			@Override
			public boolean where(TagViewModel value) {
				return value.getState().equals(TagState.Required);
			}
		});

		avaiableTagsPanel.setFilter(new Predicate<TagViewModel>() {
			@Override
			public boolean where(TagViewModel value) {
				return value.getState().equals(TagState.Allowed);
			}
		});


		this.setTop(avaiableTagsPanel);
		this.setBottom(currentTagsPanel);
	}


	public void setDataContext(ITagStatesVMProvider tagsViewModel){
		currentTagsPanel.setDataContext(tagsViewModel);
		avaiableTagsPanel.setDataContext(tagsViewModel);
	}
}
