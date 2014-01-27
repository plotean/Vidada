package vidada.model.tags;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import vidada.model.ServiceProvider;
import vidada.model.media.store.IMediaStore;
import vidada.model.media.store.IMediaStoreService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;


/**
 * Service which manages all Tags
 * @author IsNull
 *
 */
public class TagService implements ITagService {


	private IMediaStoreService mediaStoreService = ServiceProvider.Resolve(IMediaStoreService.class);


	private transient final EventHandlerEx<EventArgsG<Tag>> tagAddedEvent = new EventHandlerEx<EventArgsG<Tag>>();
	private transient final EventHandlerEx<EventArgsG<Tag>> tagRemovedEvent = new EventHandlerEx<EventArgsG<Tag>>();
	private transient final EventHandlerEx<EventArgs> tagsChanged = new EventHandlerEx<EventArgs>();

	@Override
	public IEvent<EventArgs> getTagsChangedEvent() { return tagsChanged; }
	@Override
	public IEvent<EventArgsG<Tag>> getTagAddedEvent() { return tagAddedEvent; }
	@Override
	public IEvent<EventArgsG<Tag>> getTagRemovedEvent() { return tagRemovedEvent; }



	public TagService(){

	}


	@Override
	public synchronized Collection<Tag> getAllTags(){

		Set<Tag> allTags = new HashSet<Tag>();
		Collection<IMediaStore> stores = mediaStoreService.getStores();

		for (IMediaStore store : stores) {
			Collection<Tag> storeTags = store.getAllUsedTags();
			allTags.addAll(storeTags);
		}

		return allTags;
	}



	@Override
	public void notifyTagsChanged() {
		tagsChanged.fireEvent(this, EventArgs.Empty);
	}

}
