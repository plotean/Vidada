package vidada.model.browser;

import vidada.model.tags.Tag;
import vidada.model.tags.TagFilterState;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;

public class TagViewModel {

	private TagFilterState state = TagFilterState.Allowed;
	private final Tag tag;

	private final EventHandlerEx<EventArgsG<TagViewModel>> tagStateChangedEvent = new EventHandlerEx<EventArgsG<TagViewModel>>();
	public IEvent<EventArgsG<TagViewModel>> getTagStateChangedEvent() { return tagStateChangedEvent; }


	public TagViewModel(Tag tag){
		this.tag = tag;
	}

	public TagFilterState getState() { return state; }

	public void setState(TagFilterState state){
		this.state = state;
		tagStateChangedEvent.fireEvent(this, EventArgsG.build(this));
	}

	public String getName(){
		return getTag().getName();
	}


	public Tag getTag() {
		return tag;
	}

	@Override
	public String toString(){ return getName(); }


	public void toggleState() {

		switch (getState()) {

		case Allowed:
			setState(TagFilterState.Required);
			break;


		case Required:
			setState(TagFilterState.Blocked);
			break;


		case Blocked:
			setState(TagFilterState.Allowed);
			break;

		default:
			break;
		}

	}
}
