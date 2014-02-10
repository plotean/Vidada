package vidada.viewsFX.tags;

import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import vidada.model.tags.Tag;
import vidada.viewmodel.media.IMediaViewModel;
import vidada.viewsFX.controls.TagItPanel;


public class MediaDetailTagPane extends BorderPane {

	private IMediaViewModel mediaViewModel;

	private final TagItPanel<Tag> currentTagsPanel;
	private final TagItPanel<Tag> avaiableTagsPanel;
	private SuggestionProvider<Tag> tagSuggestionProvider;


	public MediaDetailTagPane() {
		currentTagsPanel = new TagItPanel<>();
		avaiableTagsPanel = new TagItPanel();


		currentTagsPanel.setTagNodeFactory(new Callback<Tag, Node>() {
			@Override
			public Node call(Tag tagVM) {
				//TODO
				return null; //new TagView(tagVM);
			}
		});

		currentTagsPanel.setTagModelFactory(new Callback<String, Tag>() {
			@Override
			public Tag call(String tagName) {
				if(mediaViewModel != null){
					return mediaViewModel.createTag(tagName);
				}else {
					return null;
				}
			}
		});

		tagSuggestionProvider = SuggestionProvider.create((Tag)null);
		currentTagsPanel.setSuggestionProvider(tagSuggestionProvider);

		/*
		avaiableTagsPanel.setFilter(new Predicate<Tag>() {
			@Override
			public boolean where(TagViewModel value) {
				return value.getState().equals(TagState.Allowed);
			}
		});*/

		//this.setCenter(avaiableTagsPanel);
		this.setCenter(currentTagsPanel);
	}

	public void setDataContext(IMediaViewModel mediaViewModel){
		// TODO
		this.mediaViewModel = mediaViewModel;
		currentTagsPanel.getTags().clear();
		currentTagsPanel.getTags().addAll(mediaViewModel.getTags());

		//currentTagsPanel.setDataContext(tagsViewModel);
		//avaiableTagsPanel.setDataContext(tagsViewModel);
	}
}
