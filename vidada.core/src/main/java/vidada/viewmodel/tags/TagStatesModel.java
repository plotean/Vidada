package vidada.viewmodel.tags;

import java.util.ArrayList;
import java.util.List;

import vidada.model.tags.Tag;
import vidada.model.tags.TagState;
import vidada.viewmodel.ITagStatesVM;
import vidada.viewmodel.IVMFactory;
import vidada.viewmodel.ViewModelPool;
import archimedesJ.data.events.CollectionChangeType;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;

/**
 * Maps Tags to TagViewModels, additionally supports direct access for tag state
 * @author IsNull
 *
 */
public class TagStatesModel implements ITagStatesVM {


	transient private final ViewModelPool<Tag, TagViewModel> tagVmPool;



	private final EventHandlerEx<CollectionEventArg<Tag>> tagsChangedEvent = new EventHandlerEx<CollectionEventArg<Tag>>();
	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#getTagsChangedEvent()
	 */
	@Override
	public IEvent<CollectionEventArg<Tag>> getTagsChangedEvent() {return tagsChangedEvent;}


	private final EventHandlerEx<EventArgsG<TagViewModel>> tagStateChangedEvent 
	= new EventHandlerEx<EventArgsG<TagViewModel>>();

	// Raised when the state of a tag has been changed
	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#getTagStateChangedEvent()
	 */
	@Override
	public IEvent<EventArgsG<TagViewModel>> getTagStateChangedEvent() {return tagStateChangedEvent;}


	public TagStatesModel(IVMFactory<Tag, TagViewModel> factory) {
		tagVmPool = new ViewModelPool<Tag, TagViewModel>(factory);
	}

	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#clear()
	 */
	@Override
	public void clear(){
		if(!tagVmPool.isEmpty()){

			for (TagViewModel t : tagVmPool.getViewModels()) {
				t.getTagStateChangedEvent().remove(tagStateListener);
			}
			tagVmPool.clear();
			tagsChangedEvent.fireEvent(this, CollectionEventArg.Cleared);
		}
	}

	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#addAll(java.util.List)
	 */
	@Override
	public void addAll(List<Tag> newtags){
		for (Tag tag : newtags) {
			addInternal(tag);
		}
		tagsChangedEvent.fireEvent(this, new CollectionEventArg(CollectionChangeType.Added, newtags));
	}

	/**
	 * Adds the given tag to this model
	 * @param tag
	 */
	@Override
	public void add(Tag tag) {
		addInternal(tag);
		tagsChangedEvent.fireEvent(this, new CollectionEventArg(CollectionChangeType.Added, tag));
	}

	private void addInternal(Tag tag){
		TagViewModel vm = tagVmPool.viewModel(tag);
		vm.getTagStateChangedEvent().add(tagStateListener);
	}


	private final EventListenerEx<EventArgsG<TagViewModel>> tagStateListener 
	= new EventListenerEx<EventArgsG<TagViewModel>>() {

		@Override
		public void eventOccured(Object sender, EventArgsG<TagViewModel> eventArgs) {
			tagStateChangedEvent.fireEvent(this, eventArgs);
		}
	};

	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#remove(vidada.model.tags.Tag)
	 */
	@Override
	public void remove(Tag tag) {

		TagViewModel vmToRemove = tagVmPool.remove(tag);

		if(vmToRemove != null){
			vmToRemove.getTagStateChangedEvent().remove(tagStateListener);
			tagsChangedEvent.fireEvent(this, new CollectionEventArg(CollectionChangeType.Removed, tag));
		}
	}



	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#getTagsWithState(vidada.model.tags.TagFilterState)
	 */
	@Override
	public List<Tag> getTagsWithState(TagState state) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		for (TagViewModel t : tagVmPool.getViewModels()) {
			if(t.getState().equals(state))
				tags.add(t.getModel());
		}
		return tags;
	}

	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#setAllTagsState(vidada.model.tags.TagFilterState)
	 */
	@Override
	public void setAllTagsState(TagState state) {
		for (TagViewModel t : tagVmPool.getViewModels()) {
			if(!t.getState().equals(state)){
				t.setState(state);
			}
		}
	}

	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#setTagState(vidada.model.tags.Tag, vidada.model.tags.TagFilterState)
	 */
	@Override
	public void setTagState(Tag tag, TagState state) {
		TagViewModel vm = tagVmPool.viewModel(tag);
		if(!vm.getState().equals(state)){
			vm.setState(state);
		}
	}


	@Override
	public TagState getTagState(Tag tag) {
		TagViewModel vm = tagVmPool.viewModel(tag);
		if(vm != null){
			return vm.getState();
		}
		return TagState.None;
	}

	/* (non-Javadoc)
	 * @see vidada.model.browser.ITagStatesViewModel#getTagViewModels()
	 */
	@Override
	public Iterable<TagViewModel> getTagViewModels() {
		return tagVmPool.getViewModels();
	}


	@Override
	public TagViewModel getViewModel(Tag tag) {
		return tagVmPool.viewModel(tag);
	}

}
