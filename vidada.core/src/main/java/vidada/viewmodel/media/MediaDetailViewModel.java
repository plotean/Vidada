package vidada.viewmodel.media;

import vidada.model.ServiceProvider;
import vidada.model.browser.BrowserMediaItem;
import vidada.model.browser.IBrowserItem;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;
import vidada.model.tags.TagState;
import vidada.viewmodel.ITagStatesVM;
import vidada.viewmodel.ITagStatesVMProvider;
import vidada.viewmodel.MediaViewModel;
import vidada.viewmodel.tags.TagViewModel;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotSupportedException;

/**
 * Represents a single media item
 * @author IsNull
 *
 */
public class MediaDetailViewModel extends MediaViewModel implements IMediaViewModel {

	transient private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	private volatile boolean ignoreUpdates = false;
	private final ITagStatesVM tagsVM;

	/**
	 * Creates a new media detail model form the given MediaData
	 * @param mediaData
	 */
	public MediaDetailViewModel(final ITagStatesVM tagsVM){
		super(null);

		assert tagsVM != null :  "tagsVM can not be null";

		this.tagsVM = tagsVM;
		tagsVM.getTagStateChangedEvent().add(tagStatelistener);
	}

	private MediaItem previousModel = null;

	@Override
	public void setModel(IBrowserItem item) {
		if(item == null || item instanceof BrowserMediaItem){
			super.setModel(item);
			MediaItem model = (item != null) ? item.getData() : null;

			if(previousModel != null){
				model.getTagAddedEvent().remove(tagAddedListener);
				model.getTagRemovedEvent().remove(tagRemovedListener);
			}

			if(model != null){
				model.getTagAddedEvent().add(tagAddedListener);
				model.getTagRemovedEvent().add(tagRemovedListener);
			}

			updateTagStates();

			previousModel = model;
		}else
			throw new NotSupportedException("Parameter model must be of type BrowserMediaItem");
	}

	private synchronized void updateTagStates(){

		ignoreUpdates = true;

		BrowserMediaItem item = getModel();
		if(item != null && item.getData() != null){
			MediaItem media = item.getData();
			System.out.println("update tag view: " + media.getTags());
			System.out.println("all tags: " + tagsVM.getTagViewModels());

			tagsVM.setAllTagsState(TagState.Allowed);

			for (Tag t : media.getTags()) {
				tagsVM.setTagState(t, TagState.Required);
			}
		}

		ignoreUpdates = false;
	}


	private final EventListenerEx<EventArgsG<Tag>> tagAddedListener = new EventListenerEx<EventArgsG<Tag>>() {
		@Override
		public void eventOccured(Object sender, EventArgsG<Tag> eventArgs) {
			tagsVM.setTagState(eventArgs.getValue(), TagState.Required);
		}
	};


	private final EventListenerEx<EventArgsG<Tag>> tagRemovedListener = new EventListenerEx<EventArgsG<Tag>>() {
		@Override
		public void eventOccured(Object sender, EventArgsG<Tag> eventArgs) {
			tagsVM.setTagState(eventArgs.getValue(), TagState.Allowed);
		}
	};


	private final EventListenerEx<EventArgsG<TagViewModel>> tagStatelistener = new EventListenerEx<EventArgsG<TagViewModel>>() {
		@Override
		public void eventOccured(Object sender, EventArgsG<TagViewModel> eventArgs) {


			BrowserMediaItem item = getModel();
			if(item == null) return;
			MediaItem media = item.getData();

			if(media == null || ignoreUpdates) return;

			TagViewModel tagVm = eventArgs.getValue();
			switch (tagVm.getState()) {
			case Allowed:
				media.removeTag(tagVm.getModel());
				media.persist();
				System.out.println("removing required tag " + tagVm.getModel() + " from " + media.getFilename());
				break;

			case Required:
				media.addTag(tagVm.getModel());
				media.persist();
				System.out.println("adding required tag " + tagVm.getModel() + " from " + media.getFilename());
				break;

			default:
				System.err.println("MediaViewModel: Unhandled tag state: " + tagVm.getState());
				break;
			}
		}
	};


	/* (non-Javadoc)
	 * @see vidada.views.media.IMediaDetailModel#persist()
	 */
	@Override
	public void persist(){
		mediaService.update(getModel().getData());
	}


	@Override
	public ITagStatesVMProvider getTagsVM() {
		return tagsVM;
	}


	@Override
	public String toString(){
		return "MediaDetailViewModel:" + getModel();
	}
}
