package vidada.client.viewmodel.tags;

import archimedes.core.data.Toggler;
import archimedes.core.events.EventArgsG;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import vidada.client.viewmodel.IViewModel;
import vidada.model.tags.Tag;
import vidada.model.tags.TagState;

public class TagViewModel implements IViewModel<Tag>, Comparable<TagViewModel>{

	private Tag tag;
	private final Toggler<TagState> availableStates;

	private final EventHandlerEx<EventArgsG<TagViewModel>> tagStateChangedEvent = new EventHandlerEx<EventArgsG<TagViewModel>>();
	public IEvent<EventArgsG<TagViewModel>> getTagStateChangedEvent() { return tagStateChangedEvent; }

	public TagViewModel(Tag tag)
	{
		this(tag, TagState.Allowed, TagState.Required, TagState.Blocked);
	}

	/**
	 * Creates a new TagViewModel
	 * @param tag
	 * @param availableStates All available tag states
	 */
	public TagViewModel(Tag tag, TagState... availableStates){
		this.tag = tag;
		this.availableStates = new Toggler<TagState>(availableStates);
	}

	public TagState getState() { return availableStates.currentValue(); }

	public void setState(TagState state){
		availableStates.setCurrent(state);
		tagStateChangedEvent.fireEvent(this, EventArgsG.build(this));
	}

	public String getName(){
		return getModel().getName();
	}


	@Override
	public Tag getModel() {
		return tag;
	}

	@Override
	public void setModel(Tag model) {
		this.tag = model; throw new IllegalStateException();
	}

	@Override
	public String toString(){ return getName(); }

	public void toggleState() {
		setState(availableStates.next());
	}

	@Override
	public int compareTo(TagViewModel o) {
		if(getModel() == null) return -1;
		return getModel().compareTo(o.getModel());
	}


}
