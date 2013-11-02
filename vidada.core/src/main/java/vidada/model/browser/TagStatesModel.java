package vidada.model.browser;

import java.util.ArrayList;
import java.util.List;

import vidada.model.tags.Tag;
import vidada.model.tags.TagFilterState;
import archimedesJ.data.events.CollectionChangeType;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;

public class TagStatesModel {

	List<TagViewModel> tagVMs = new ArrayList<TagViewModel>();

	private final EventHandlerEx<CollectionEventArg<Tag>> tagsChangedEvent = new EventHandlerEx<CollectionEventArg<Tag>>();
	/**
	 * Raised when the tag model has changed
	 */
	public IEvent<CollectionEventArg<Tag>> getTagsChangedEvent() {return tagsChangedEvent;}


	private final EventHandlerEx<EventArgsG<TagViewModel>> tagStateChangedEvent 
	= new EventHandlerEx<EventArgsG<TagViewModel>>();

	// Raised when the state of a tag has been changed
	public IEvent<EventArgsG<TagViewModel>> getTagStateChangedEvent() {return tagStateChangedEvent;}


	public TagStatesModel() {

	}

	/**
	 * Removes all tags from the model
	 */
	public void clear(){
		if(!tagVMs.isEmpty()){

			for (TagViewModel t : tagVMs) {
				t.getTagStateChangedEvent().remove(tagStateListener);
			}
			tagVMs.clear();
			tagsChangedEvent.fireEvent(this, CollectionEventArg.Invalidated);
		}
	}

	/**
	 * Adds all tags to the model
	 * @param newtags
	 */
	public void addAll(List<Tag> newtags){
		for (Tag tag : newtags) {
			addInternal(tag);
		}
		tagsChangedEvent.fireEvent(this, CollectionEventArg.Invalidated);
	}

	/**
	 * Adds the given tag to this model
	 * @param tag
	 */
	public void add(Tag tag) {
		addInternal(tag);
		tagsChangedEvent.fireEvent(this, new CollectionEventArg(CollectionChangeType.Added, tag));
	}

	private void addInternal(Tag tag){
		TagViewModel vm = new TagViewModel(tag);
		tagVMs.add(vm);

		vm.getTagStateChangedEvent().add(tagStateListener);
	}


	private final EventListenerEx<EventArgsG<TagViewModel>> tagStateListener 
	= new EventListenerEx<EventArgsG<TagViewModel>>() {

		@Override
		public void eventOccured(Object sender, EventArgsG<TagViewModel> eventArgs) {
			tagStateChangedEvent.fireEvent(this, eventArgs);
		}
	};

	/**
	 * Remove the given tag from the model
	 * @param tag
	 */
	public void remove(Tag tag) {

		TagViewModel vmToRemove = null;

		for (TagViewModel vm : tagVMs) {
			if(vm.getTag().equals(tag)){
				vmToRemove = vm;
				break;
			}
		}

		if(vmToRemove != null){
			removeInternal(vmToRemove);
			tagsChangedEvent.fireEvent(this, new CollectionEventArg(CollectionChangeType.Removed, tag));
		}
	}

	private void removeInternal(TagViewModel vm){
		tagVMs.remove(vm);
		vm.getTagStateChangedEvent().remove(tagStateListener);
	}


	/**
	 * Return all tags with the given state
	 * @param state
	 * @return
	 */
	public List<Tag> getTagsWithState(TagFilterState state) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		for (TagViewModel t : tagVMs) {
			if(t.getState().equals(state))
				tags.add(t.getTag());
		}
		return tags;
	}

	/**
	 * Sets the given state to all tags
	 * @param state
	 */
	public void setAllTagsState(TagFilterState state) {
		for (TagViewModel t : tagVMs) {
			if(!t.getState().equals(state)){
				t.setState(state);
			}
		}
	}

	/**
	 * Sets the given state to the given tag
	 * @param tag
	 * @param state
	 */
	public void setTagState(Tag tag, TagFilterState state) {
		for (TagViewModel t : tagVMs) {
			if(t.getTag().equals(tag)){
				if(!t.getState().equals(state)){
					t.setState(state);

				}
			}
		}
	}

	public Iterable<TagViewModel> getTagViewModels() {
		return tagVMs;
	}





}
