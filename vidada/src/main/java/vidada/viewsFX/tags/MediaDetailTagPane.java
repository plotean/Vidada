package vidada.viewsFX.tags;

import impl.org.controlsfx.autocompletion.SuggestionProvider;

import java.util.Collection;

import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import vidada.model.tags.Tag;
import vidada.viewmodel.media.IMediaViewModel;
import vidada.viewsFX.bindings.ObservableListBindingFX;
import vidada.viewsFX.controls.TagItPanel;


public class MediaDetailTagPane extends BorderPane {

	private IMediaViewModel mediaViewModel;

	private final TagItPanel<Tag> currentTagsPanel;
	private final TagItPanel<Tag> avaiableTagsPanel;
	private SuggestionProvider<Tag> tagSuggestionProvider;

	private ObservableListBindingFX<Tag> binding;


	public MediaDetailTagPane() {
		currentTagsPanel = new TagItPanel<>();
		avaiableTagsPanel = new TagItPanel<>();

		currentTagsPanel.setTagModelFactory(new Callback<String, Tag>() {
			@Override
			public Tag call(String tagName) {
				if(mediaViewModel != null){
					return mediaViewModel.createTag(tagName);
				}else {
					System.err.println("Can not create tag since mediaViewModel = NULL!");
					return null;
				}
			}
		});


		//this.setCenter(avaiableTagsPanel);
		this.setCenter(currentTagsPanel);
	}


	public void setDataContext(IMediaViewModel mediaViewModel){

		this.mediaViewModel = mediaViewModel;

		if(binding != null) binding.unbind();

		currentTagsPanel.getTags().clear();

		if(mediaViewModel != null){
			currentTagsPanel.getTags().addAll(mediaViewModel.getTags());
			binding = ObservableListBindingFX.bind(currentTagsPanel.getTags(), mediaViewModel.getTags());

			Collection<Tag> availableTags = mediaViewModel.getAvailableTags();
			System.out.println(availableTags);
			tagSuggestionProvider = SuggestionProvider.create(availableTags);
			currentTagsPanel.setSuggestionProvider(tagSuggestionProvider);
		}
	}

}
