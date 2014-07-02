package vidada.viewsFX.filters;

import archimedes.core.util.Lists;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
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
import vidada.client.viewmodel.tags.TagViewModel;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFactory;
import vidada.model.tags.TagState;
import vidada.services.ServiceProvider;
import vidada.viewsFX.controls.TagItPanel;

import java.util.Collection;
import java.util.stream.Collectors;

public class FilterViewFx extends BorderPane {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

	private final FilterModel filtermodel;

	private final TagItPanel<TagViewModel> tagPane;
	private final TextField searchText = TextFields.createClearableTextField();
	private final CheckBox chkReverse = new CheckBox("Reverse");
	private final ComboBox<MediaType> cboMediaType= new ComboBox<>();
	private final ComboBox<OrderProperty> cboOrder= new ComboBox<>();

	private final ITagClientService tagClientService = ServiceProvider.Resolve(IVidadaClientManager.class).getActive().getTagClientService();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new filter view
     * @param filtermodel
     */
	public FilterViewFx(final FilterModel filtermodel){

		this.filtermodel = filtermodel;

        // Setup the TagItPanel
        tagPane = new TagItPanel<>();
        tagPane.setTagModelFactory(text -> {
            Tag tag = TagFactory.instance().createTag(text);
            return createVM(tag);
        });
        tagPane.setTagNodeFactory(model -> new StateTagControl(model));


		updateTagSuggestionProvider();

		HBox box = new HBox();

		this.setPadding(new Insets(10));

		searchText.setMinWidth(100);
		searchText.setPromptText("search...");

		box.getChildren().add(cboMediaType);
		box.getChildren().add(searchText);
		box.getChildren().add(cboOrder);
		box.getChildren().add(chkReverse);


		Insets margin = new Insets(5,5,10,0);
		HBox.setMargin(searchText, margin);
		HBox.setMargin(cboOrder, margin);
		HBox.setMargin(chkReverse, margin);
		HBox.setMargin(cboMediaType, margin);

		setTop(box);
		setCenter(tagPane);

		cboOrder.setPromptText("Define order...");
		cboOrder.setItems(FXCollections.observableList(Lists.asNoNullList(OrderProperty.values())));

		ObservableList<MediaType> mediaTypes = FXCollections.observableArrayList();
		mediaTypes.addAll(MediaType.ANY, MediaType.MOVIE, MediaType.IMAGE);

		cboMediaType.setPromptText("Define mediatype...");
		cboMediaType.setItems(mediaTypes);

		// register change events
		registerEventHandler();
	}

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Updates the tag suggestions
     */
	private void updateTagSuggestionProvider() {
        new Thread(() -> {
            // Since we call a client service we have to expect long delays over network
            Collection<TagViewModel> availableTags = tagClientService.getUsedTags().stream()
                    .map(x -> createVM(x))
                    .collect(Collectors.toList());
            SuggestionProvider<TagViewModel> tagSuggestionProvider = SuggestionProvider.create(availableTags);

            Platform.runLater(() -> tagPane.setSuggestionProvider(tagSuggestionProvider));
        }).start();
    }

	private void registerEventHandler() {
        chkReverse.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filtermodel.setReverse(chkReverse.isSelected());
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

        tagPane.getTags().addListener(
                (ListChangeListener.Change<? extends TagViewModel> changeEvent) -> updateModelTags());
    }

    /**
     * Updates the filtermodel tags according to the view state.
     */
    private void updateModelTags(){
        filtermodel.setAutoFireEnabled(false); // prevent unnecessary change events being fired

        // Update required tags
        filtermodel.getRequiredTags().clear();
        filtermodel.getRequiredTags().addAll(
                tagPane.getTags().stream()
                        .filter(x -> !x.getState().equals(TagState.Blocked))
                        .map(x -> x.getModel())
                        .collect(Collectors.toList()));

        // Update blocked tags
        filtermodel.getBlockedTags().clear();
        filtermodel.getBlockedTags().addAll(
                tagPane.getTags().stream()
                        .filter(x -> x.getState().equals(TagState.Blocked))
                        .map(x -> x.getModel())
                        .collect(Collectors.toList()));


        filtermodel.setAutoFireEnabled(true); // Re-Enable auto-fire and fire the change event
        filtermodel.fireFilterChanged();
    }

    private TagViewModel createVM(Tag tag){
        TagViewModel vm = null;
        if(tag != null) {
            vm = new TagViewModel(tag, TagState.Allowed, TagState.Blocked);
            vm.getTagStateChangedEvent().add((s, e) -> updateModelTags());
        }
        return vm;
    }

}
