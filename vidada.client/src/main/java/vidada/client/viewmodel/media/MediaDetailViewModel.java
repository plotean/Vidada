package vidada.client.viewmodel.media;

import archimedes.core.data.events.CollectionEventArg;
import archimedes.core.data.observable.IObservableCollection;
import archimedes.core.data.observable.IObservableList;
import archimedes.core.data.observable.ObservableArrayList;
import archimedes.core.events.EventListenerEx;
import archimedes.core.exceptions.NotSupportedException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.client.model.browser.BrowserMediaItem;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.services.ITagClientService;
import vidada.client.viewmodel.MediaViewModel;
import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFactory;

import java.util.Collection;

/**
 * Represents a single media item
 * @author IsNull
 *
 */
public class MediaDetailViewModel extends MediaViewModel implements IMediaViewModel {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaDetailViewModel.class.getName());

	private final ITagClientService tagClientService;
	private final IObservableList<Tag> observableTags = new ObservableArrayList<Tag>();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

	/**
	 * Creates a new media detail model form the given MediaData
	 * @param tagService
	 */
	public MediaDetailViewModel(ITagClientService tagService){
		// Do not call any virtual methods here such as setModel!
		this.tagClientService = tagService;
	}

	private MediaItem previousModel = null;

	@Override
	public void setModel(IBrowserItem item) {
		if(item == null || item instanceof BrowserMediaItem){
			super.setModel(item);
			MediaItem model = (item != null) ? item.getData() : null;

			if(previousModel != null){
				model.getTags().removeBinding(observableTags);
				model.getTags().getChangeEvent().remove(tagsChangedListener);
			}

			updateObservableTags();

			if(model != null && observableTags != null){
				model.getTags().bindTwoWay(observableTags);
				model.getTags().getChangeEvent().add(tagsChangedListener);
			}

			previousModel = model;
		}else
			throw new NotSupportedException("Parameter model must be of type BrowserMediaItem");
	}

	transient private final EventListenerEx<CollectionEventArg<Tag>> tagsChangedListener = new EventListenerEx<CollectionEventArg<Tag>>() {
		@Override
		public void eventOccured(Object sender, CollectionEventArg<Tag> eventArgs) {
            logger.info("MediaDetailViewModel: Tags changed in model, persisting model now!");
			persist();
		}
	};



	private void updateObservableTags(){
		if(observableTags != null){
			observableTags.clear();
			if(getModel() != null && getModel().getData() != null)
				observableTags.addAll(getModel().getData().getTags());
		}
	}


	/***************************************************************************
	 *                                                                         *
	 * Tag Management                                                          *
	 *                                                                         *
	 **************************************************************************/


	@Override
	public IObservableCollection<Tag> getTags() {
		//TODO!!

		return getModel().getData().getTags(); //observableTags;
	}


	@Override
	public Tag createTag(String name) {
		return TagFactory.instance().createTag(name);
	}


	@Override
	public Collection<Tag> getAvailableTags() {
		return tagClientService.getUsedTags();
	}

	@Override
	public String toString(){
		return "MediaDetailViewModel:" + getModel();
	}

}
