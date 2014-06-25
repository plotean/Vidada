package vidada.viewsFX.tags;

import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.client.viewmodel.media.IMediaViewModel;
import vidada.model.tags.Tag;
import vidada.viewsFX.bindings.ObservableListBindingFX;
import vidada.viewsFX.controls.TagItPanel;

import java.util.Collection;


public class MediaDetailTagPane extends BorderPane {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaDetailTagPane.class.getName());

	private IMediaViewModel mediaViewModel;

	private final TagItPanel<Tag> currentTagsPanel;
	private final TagItPanel<Tag> avaiableTagsPanel;
	private SuggestionProvider<Tag> tagSuggestionProvider;

	private ObservableListBindingFX<Tag> binding;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaDetailTagPane
     */
	public MediaDetailTagPane() {
		currentTagsPanel = new TagItPanel<>();
		avaiableTagsPanel = new TagItPanel<>();

		currentTagsPanel.setTagModelFactory(tagName -> {
            if(mediaViewModel != null){
                return mediaViewModel.createTag(tagName);
            }else {
                logger.debug("Can not create tag since mediaViewModel = NULL!");
                return null;
            }
        });

		this.setCenter(currentTagsPanel);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

	public void setDataContext(IMediaViewModel mediaViewModel){

		this.mediaViewModel = mediaViewModel;

		if(binding != null) binding.unbind();

		currentTagsPanel.getTags().clear();

		if(mediaViewModel != null){
			currentTagsPanel.getTags().addAll(mediaViewModel.getTags());
			binding = ObservableListBindingFX.bind(currentTagsPanel.getTags(), mediaViewModel.getTags());

			Collection<Tag> availableTags = mediaViewModel.getAvailableTags();
			tagSuggestionProvider = SuggestionProvider.create(availableTags);
			currentTagsPanel.setSuggestionProvider(tagSuggestionProvider);
		}
	}

}
