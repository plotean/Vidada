package vidada.model.browser;

import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;

public class TagServiceModelBinding {

	private final ITagService tagservice;
	private final TagStatesModel tagsStateModel;


	public static TagServiceModelBinding bind(ITagService tagservice,TagStatesModel tageStateModel){
		return new TagServiceModelBinding(tagservice, tageStateModel);
	}


	public TagServiceModelBinding(ITagService tagservice,TagStatesModel tageStateModel){
		this.tagservice = tagservice;
		this.tagsStateModel = tageStateModel;


		tagservice.getTagAddedEvent().add(new EventListenerEx<EventArgsG<Tag>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<Tag> eventArgs) {
				tagsStateModel.add(eventArgs.getValue());
			}
		});

		tagservice.getTagRemovedEvent().add(new EventListenerEx<EventArgsG<Tag>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<Tag> eventArgs) {
				tagsStateModel.remove(eventArgs.getValue());
			}
		});

		tagservice.getTagsChangedEvent().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				refreshModel();
			}
		});

		refreshModel();
	}

	public TagStatesModel getTagStatesModel(){
		return tagsStateModel;
	}


	private void refreshModel(){
		tagsStateModel.clear();
		tagsStateModel.addAll(tagservice.getAllTags());
	}

}
