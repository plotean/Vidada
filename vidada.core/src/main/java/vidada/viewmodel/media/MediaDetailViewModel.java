package vidada.viewmodel.media;

import java.util.List;

import vidada.model.browser.BrowserMediaItem;
import vidada.model.browser.IBrowserItem;
import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;
import vidada.viewmodel.MediaViewModel;
import archimedesJ.data.events.CollectionEventArg;
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

	transient private final IObservableList<Tag> observableTags = new ObservableArrayList<Tag>();

	/**
	 * Creates a new media detail model form the given MediaData
	 * @param mediaData
	 */
	public MediaDetailViewModel(){
		super(null);
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

			if(model != null){
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
		observableTags.clear();
		if(getModel() != null && getModel().getData() != null)
			observableTags.addAll(getModel().getData().getTags());
	}


	/***************************************************************************
	 *                                                                         *
	 * Tag Management                                                          *
	 *                                                                         *
	 **************************************************************************/


	@Override
	public IObservableList<Tag> getTags() {
		return observableTags;
	}


	@Override
	public Tag createTag(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Tag> getAvailableTags() {
		// TODO Auto-generated method stub
		return null;
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
