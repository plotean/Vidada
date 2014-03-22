package vidada.viewsFX.filters;

import archimedesJ.util.Lists;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.TextFields;
import vidada.client.IVidadaClientManager;
import vidada.client.services.ITagClientService;
import vidada.client.viewmodel.FilterModel;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFactory;
import vidada.services.ServiceProvider;
import vidada.viewsFX.controls.TagItPanel;

import java.util.Collection;

public class FilterViewFx extends BorderPane {

	private final FilterModel filtermodel;

	private final TagItPanel<Tag> tagPane;
	private final TextField searchText = TextFields.createSearchField();
	private final CheckBox chkreverse = new CheckBox("Reverse");
	private final ComboBox<MediaType> cboMediaType= new ComboBox<>();
	private final ComboBox<OrderProperty> cboOrder= new ComboBox<>();

	private final ITagClientService tagClientService = ServiceProvider.Resolve(IVidadaClientManager.class).getActive().getTagClientService();


	public FilterViewFx(final FilterModel filtermodel){

		this.filtermodel = filtermodel;	
		tagPane = new TagItPanel<>();

		tagPane.setTagModelFactory(text -> TagFactory.instance().createTag(text));

		updateTagSuggestionProvider();

		HBox box = new HBox();

		this.setPadding(new Insets(10));

		searchText.setMinWidth(100);
		searchText.setPromptText("search...");

		box.getChildren().add(cboMediaType);
		box.getChildren().add(searchText);
		box.getChildren().add(cboOrder);
		box.getChildren().add(chkreverse);


		Insets margrin = new Insets(5,5,10,0);
		HBox.setMargin(searchText, margrin);
		HBox.setMargin(cboOrder, margrin);
		HBox.setMargin(chkreverse, margrin);
		HBox.setMargin(cboMediaType, margrin);

		setTop(box);
		setCenter(tagPane);

		cboOrder.setPromptText("Define order...");
		cboOrder.setItems(FXCollections.observableList(Lists.asNoNullList(OrderProperty.values())));

		ObservableList<MediaType> mediaTypes = FXCollections.observableArrayList();
		mediaTypes.addAll(MediaType.ANY, MediaType.MOVIE, MediaType.IMAGE);

		cboMediaType.setPromptText("Define mediatype...");
		cboMediaType.setItems(mediaTypes);

		//AquaFx.createTextFieldStyler().setType(TextFieldType.SEARCH).style(searchText); //.createButtonStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(buttonInstance);

		// register change events
		registerEventHandler();
	}

	private void updateTagSuggestionProvider(){
		Collection<Tag> availableTags = tagClientService.getUsedTags();
		SuggestionProvider<Tag> tagSuggestionProvider = SuggestionProvider.create(availableTags);
		tagPane.setSuggestionProvider(tagSuggestionProvider);
	}

	private void registerEventHandler(){
		chkreverse.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filtermodel.setReverse(chkreverse.isSelected());
        });

		cboOrder.valueProperty().addListener((observable, oldValue, newValue) -> {
            filtermodel.setOrder(cboOrder.getValue());
        });

		searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filtermodel.setQueryString(searchText.getText());
        });

		cboMediaType.valueProperty().addListener((observable, oldValue, newValue) -> {
            filtermodel.setMediaType(cboMediaType.getValue() != null ? cboMediaType.getValue() : MediaType.ANY);
        });

		tagPane.getTags().addListener((ListChangeListener.Change<? extends Tag> changeEvent) -> {
            if(changeEvent.next()){
                if(changeEvent.wasAdded()){
                    filtermodel.getRequiredTags().addAll(changeEvent.getAddedSubList());
                }
                if(changeEvent.wasRemoved()){
                    filtermodel.getRequiredTags().removeAll(changeEvent.getRemoved());
                }
            }
        });
	}
}
