package vidada.client.viewmodel.tags;

import vidada.client.viewmodel.IVMFactory;
import vidada.client.viewmodel.IViewModel;
import vidada.model.tags.Tag;
import vidada.model.tags.TagState;
import archimedesJ.data.Toggler;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;

public class TagViewModel implements IViewModel<Tag>, Comparable<TagViewModel>{

	public static final IVMFactory<Tag, TagViewModel> VMFactory = new IVMFactory<Tag, TagViewModel>() {
		@Override
		public TagViewModel create(Tag model) {
			return new TagViewModel(model);
		}
	};


	private Tag tag;
	private final Toggler<TagState> avaiableStates;

	private final EventHandlerEx<EventArgsG<TagViewModel>> tagStateChangedEvent = new EventHandlerEx<EventArgsG<TagViewModel>>();
	public IEvent<EventArgsG<TagViewModel>> getTagStateChangedEvent() { return tagStateChangedEvent; }

	public TagViewModel(Tag tag)
	{
		this(tag, TagState.Allowed, TagState.Required, TagState.Blocked);
	}

	/**
	 * 
	 * @param tag
	 * @param availableStates All available tag states
	 */
	public TagViewModel(Tag tag, TagState... availableStates){
		this.tag = tag;
		this.avaiableStates = new Toggler<TagState>(availableStates);
	}

	public TagState getState() { return avaiableStates.currentValue(); }

	public void setState(TagState state){
		avaiableStates.setCurrent(state);
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
		this.tag = model;
	}

	@Override
	public String toString(){ return getName(); }

	public void toggleState() {
		setState(avaiableStates.next());
	}

	@Override
	public int compareTo(TagViewModel o) {
		if(getModel() == null) return -1;
		return getModel().compareTo(o.getModel());
	}


}
