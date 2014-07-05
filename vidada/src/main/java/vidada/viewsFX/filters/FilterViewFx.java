package vidada.viewsFX.filters;

import archimedes.core.util.Lists;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.TextFields;
import vidada.client.viewmodel.FilterViewModel;
import vidada.client.viewmodel.tags.TagViewModel;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.viewsFX.controls.TagItPanel;

import java.util.Collection;

public class FilterViewFx extends BorderPane {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

	private final FilterViewModel filterViewModel;

	private final TagItPanel<TagViewModel> tagPane;
	private final TextField searchText = TextFields.createClearableTextField();
	private final CheckBox chkReverse = new CheckBox("Reverse");
	private final ComboBox<MediaType> cboMediaType= new ComboBox<>();
	private final ComboBox<OrderProperty> cboOrder= new ComboBox<>();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new filter view
     * @param filterViewModel
     */
	public FilterViewFx(final FilterViewModel filterViewModel){

		this.filterViewModel = filterViewModel;

        // Setup the TagItPanel
        tagPane = new TagItPanel<>();
        tagPane.setTagModelFactory(text -> filterViewModel.createTag(text));
        tagPane.setTagNodeFactory(model -> new StateTagControl(model));

        // Bind the VM-Tags to the view
        Bindings.bindContentBidirectional(
                filterViewModel.getTags(),
                tagPane.getTags());

        filterViewModel.getAvailableTagsChanged().add((sender, args) -> {
            Platform.runLater(() -> onAvailableTagsChanged());
        });

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
	private void onAvailableTagsChanged() {
        Collection<TagViewModel> tags = filterViewModel.getAvailableTags();
        SuggestionProvider<TagViewModel> tagSuggestionProvider = SuggestionProvider.create(tags);
        tagPane.setSuggestionProvider(tagSuggestionProvider);
    }

	private void registerEventHandler() {
        chkReverse.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterViewModel.setReverse(chkReverse.isSelected());
        });

        cboOrder.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterViewModel.setOrder(cboOrder.getValue());
        });

        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filterViewModel.setQueryString(searchText.getText());
        });

        cboMediaType.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterViewModel.setMediaType(cboMediaType.getValue() != null ? cboMediaType.getValue() : MediaType.ANY);
        });
    }





}
