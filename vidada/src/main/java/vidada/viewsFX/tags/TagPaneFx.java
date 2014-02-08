package vidada.viewsFX.tags;

import java.util.Iterator;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import vidada.model.tags.Tag;
import vidada.viewmodel.ITagStatesVMProvider;
import vidada.viewmodel.tags.TagViewModel;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.expressions.Predicate;


public class TagPaneFx extends BorderPane {

	private final FlowPane tagsView = new FlowPane();
	private final Insets tagMargrin = new Insets(5,5,0,0);
	private ITagStatesVMProvider tagsModel = null;
	private Predicate<TagViewModel> tagFilter = null;

	public TagPaneFx(){
		setTop(tagsView);
		tagsView.prefWidthProperty().bind(this.widthProperty());
		tagsView.prefHeightProperty().bind(this.heightProperty());
	}

	public TagPaneFx(ITagStatesVMProvider tagsModel){
		this();
		setDataContext(tagsModel);
	}


	public void setDataContext(ITagStatesVMProvider tagsModel){

		System.out.println("TagPaneFx DataContext := " + tagsModel);

		if(this.tagsModel != null){
			this.tagsModel.getTagsChangedEvent().remove(tagsChangedEventListener);
			this.tagsModel.getTagStateChangedEvent().remove(tagStateChangedListener);
		}

		this.tagsModel = tagsModel;

		if(this.tagsModel != null){
			this.tagsModel.getTagsChangedEvent().add(tagsChangedEventListener);
			this.tagsModel.getTagStateChangedEvent().add(tagStateChangedListener);
		}

		updateModelToView();
	}

	private final EventListenerEx<EventArgsG<TagViewModel>> tagStateChangedListener =
			new EventListenerEx<EventArgsG<TagViewModel>>() {
		@Override
		public void eventOccured(Object sender, EventArgsG<TagViewModel> eventArgs) {
			if(tagFilter != null){
				updateModelToView();
			}
		}
	};

	/**
	 * Set the filter
	 * @param tagFilter
	 */
	public void setFilter(Predicate<TagViewModel> tagFilter){
		this.tagFilter = tagFilter;
		updateModelToView();
	}

	EventListenerEx<CollectionEventArg<Tag>> tagsChangedEventListener = new EventListenerEx<CollectionEventArg<Tag>>() {
		@Override
		public void eventOccured(Object sender, CollectionEventArg<Tag> eventArgs) {
			switch (eventArgs.getType()) {

			case Added:
				for (Tag newTag : eventArgs.getItems()) {
					addTag(tagsModel.getViewModel(newTag));
				}
				break;

			case Removed:
				for (Tag newTag : eventArgs.getItems()) {
					removeTag(tagsModel.getViewModel(newTag));
				}
				break;


			case Invalidated:
			case Cleared:
				updateModelToView();
				break; 


			default:
				throw new NotImplementedException("CollectionEventArg Type " +eventArgs.getType()+ " unexpected");
			}

		}
	};

	/**
	 * Completely refreshes the view
	 */
	private void updateModelToView(){
		clearTags();
		if(tagsModel != null){
			System.out.println("TagPaneFx: updateModelToView: "+tagsModel.getTagViewModels());
			addTags(tagsModel.getTagViewModels());
		}else
			System.err.println("tagmodel is NULL");
	}




	private synchronized void addTags(Iterable<TagViewModel> tags){
		for (TagViewModel tag : tags) {
			addTag(tag);
		}
	}

	private synchronized void addTag(final TagViewModel tag){

		if(tagFilter == null || tagFilter.where(tag))
		{

			Platform.runLater(new Runnable() {
				@Override
				public void run() {

					TagView tview = new TagView(tag);
					FlowPane.setMargin(tview, tagMargrin);

					int pos = tagsView.getChildren().size();
					for (int i = 0; i < tagsView.getChildren().size(); i++) {
						TagView view = (TagView)tagsView.getChildren().get(i);
						int comp = view.getDataContext().compareTo(tag);
						//System.out.println("comp: " + comp + " -> " + view.getDataContext() + " : " + tag);

						if(comp > 0){
							// current tag is greater
							pos = i;
							break;
						}
					}

					tagsView.getChildren().add(pos, tview);
				}
			});

		}
	}

	private synchronized void removeTag(final TagViewModel tag){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Iterator<Node> childIterator = tagsView.getChildren().iterator();
				while (childIterator.hasNext()) {
					Node child = childIterator.next();
					if(child instanceof TagView){
						if(tag.equals(((TagView)child).getDataContext())){
							childIterator.remove();
							break;
						}
					}
				}
			}
		});
	}

	private synchronized void clearTags(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				tagsView.getChildren().clear();
			}
		});
	}


}
