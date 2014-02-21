package vidada.viewsFX.filters;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import org.controlsfx.control.textfield.TextFields;

import vidada.client.VidadaClientManager;
import vidada.client.services.ITagClientService;
import vidada.client.viewmodel.FilterModel;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;
import vidada.viewsFX.controls.TagItPanel;
import archimedesJ.util.Lists;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TextFieldType;

public class FilterViewFx extends BorderPane {

	private final FilterModel filtermodel;

	private final TagItPanel<Tag> tagPane;
	private final TextField searchText = TextFields.createSearchField();
	private final CheckBox chkreverse = new CheckBox("Reverse");
	private final ComboBox<MediaType> cboMediaType= new ComboBox<>();
	private final ComboBox<OrderProperty> cboOrder= new ComboBox<>();

	private final ITagClientService tagClientService = VidadaClientManager.instance().getTagClientService();


	public FilterViewFx(final FilterModel filtermodel){

		this.filtermodel = filtermodel;	
		tagPane = new TagItPanel<>();

		tagPane.setTagModelFactory(new Callback<String, Tag>() {
			@Override
			public Tag call(String text) {
				return tagClientService.getTag(text);
			}
		});

		tagPane.setSuggestionProvider(null);


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

		AquaFx.createTextFieldStyler().setType(TextFieldType.SEARCH).style(searchText); //.createButtonStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(buttonInstance);

		// register change events
		registerEventHandler();
	}

	private void registerEventHandler(){
		chkreverse.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				filtermodel.setReverse(chkreverse.isSelected());
			}
		});

		cboOrder.valueProperty().addListener(new ChangeListener<OrderProperty>() {
			@Override 
			public void changed(ObservableValue ov, OrderProperty t, OrderProperty t1) {                
				filtermodel.setOrder(cboOrder.getValue());               
			}    
		});

		searchText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				filtermodel.setQueryString(searchText.getText());
			}
		});

		cboMediaType.valueProperty().addListener(new ChangeListener<MediaType>() {
			@Override 
			public void changed(ObservableValue ov, MediaType t, MediaType t1) {                
				filtermodel.setMediaType(cboMediaType.getValue() != null ? cboMediaType.getValue() : MediaType.ANY);       
			}    
		});

		tagPane.getTags().addListener(new ListChangeListener<Tag>(){
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends Tag> changeEvent) {
				if(changeEvent.next()){
					if(changeEvent.wasAdded()){
						filtermodel.getRequiredTags().addAll(changeEvent.getAddedSubList());
					}
					if(changeEvent.wasRemoved()){
						filtermodel.getRequiredTags().removeAll(changeEvent.getRemoved());
					}
				}
			}
		});
	}
}
