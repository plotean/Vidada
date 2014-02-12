package vidada.viewmodel.media;

import java.util.Collection;

import vidada.model.browser.BrowserMediaItem;
import vidada.model.browser.IBrowserItem;
import vidada.model.media.MediaItem;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import vidada.viewmodel.MediaViewModel;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.data.observable.IObservableCollection;
import archimedesJ.data.observable.IObservableList;
import archimedesJ.data.observable.ObservableArrayList;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotSupportedException;

/**
 * Represents a single media item
 * @author IsNull
 *
 */
public class MediaDetailViewModel extends MediaViewModel implements IMediaViewModel {

	//transient private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	private final IObservableList<Tag> observableTags = new ObservableArrayList<Tag>();
	private final ITagService tagService;

	/**
	 * Creates a new media detail model form the given MediaData
	 * @param mediaData
	 */
	public MediaDetailViewModel(ITagService tagService){
		// Do not call any virtual methods here such as setModel!
		this.tagService = tagService;
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
		return tagService.getTag(name);
	}


	@Override
	public Collection<Tag> getAvailableTags() {
		return tagService.getUsedTags();
	}



	/***************************************************************************
	 *                                                                         *
	 * Persistence                                                             *
	 *                                                                         *
	 **************************************************************************/

	@Override
	public String toString(){
		return "MediaDetailViewModel:" + getModel();
	}

}
