package vidada.viewmodel.tags;

import vidada.model.tags.ITagService;
import vidada.viewmodel.ITagStatesVM;

public class TagServiceModelBinding {

	private final ITagService tagservice;
	private final ITagStatesVM tagsStateModel;


	public static TagServiceModelBinding bind(ITagService tagservice, ITagStatesVM tageStateModel){
		return new TagServiceModelBinding(tagservice, tageStateModel);
	}


	public TagServiceModelBinding(ITagService tagservice, ITagStatesVM tageStateModel){
		this.tagservice = tagservice;
		this.tagsStateModel = tageStateModel;

		/*
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
		});*/

		refreshModel();
	}

	public ITagStatesVM getTagStatesModel(){
		return tagsStateModel;
	}


	private void refreshModel(){
		tagsStateModel.clear();
		tagsStateModel.addAll(tagservice.getUsedTags());
	}

}
