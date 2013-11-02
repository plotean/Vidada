package vidada.viewsFX.filters;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import vidada.controller.filters.IFilterView;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFilterState;
import vidada.viewsFX.tags.TagPaneFx;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.util.Lists;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TextFieldType;

public class FilterViewFx extends BorderPane implements IFilterView{

	private final TagPaneFx tagPane = new TagPaneFx();
	private final TextField searchText = new TextField();
	private final CheckBox chkreverse = new CheckBox("Reverse");
	private final ComboBox<MediaType> cboMediaType= new ComboBox<>();
	private final ComboBox<OrderProperty> cboOrder= new ComboBox<>();

	private final EventHandlerEx<EventArgs> filterChangedEvent = new EventHandlerEx<>();

	@Override
	public IEvent<EventArgs> getFilterChangedEvent() {
		return filterChangedEvent;
	}



	public FilterViewFx(){

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
		cboOrder.valueProperty().addListener(new ChangeListener<OrderProperty>() {
			@Override 
			public void changed(ObservableValue ov, OrderProperty t, OrderProperty t1) {                
				onFilterChanged();               
			}    
		});

		searchText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				onFilterChanged();      
			}
		});


		cboMediaType.setPromptText("Define mediatype...");
		cboMediaType.setItems(FXCollections.observableList(Lists.asNoNullList(MediaType.values())));
		cboMediaType.valueProperty().addListener(new ChangeListener<MediaType>() {
			@Override 
			public void changed(ObservableValue ov, MediaType t, MediaType t1) {                
				onFilterChanged();               
			}    
		});

		AquaFx.createTextFieldStyler().setType(TextFieldType.SEARCH).style(searchText); //.createButtonStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(buttonInstance);
	}

	private void onFilterChanged(){
		filterChangedEvent.fireEvent(this, EventArgs.Empty);
	}


	@Override
	public MediaType getSelectedMediaType() {
		return cboMediaType.getValue() != null ? cboMediaType.getValue() : MediaType.ANY;			
	}

	@Override
	public List<Tag> getTagsWithState(TagFilterState state) {
		return tagPane.getTagsWithState(state);
	}

	@Override
	public OrderProperty getSelectedOrder() {
		return cboOrder.getValue();
	}

	@Override
	public boolean isReverseOrder() {
		return chkreverse.isSelected();
	}

	@Override
	public String getQueryString() {
		return searchText.getText();
	}

	@Override
	public boolean isOnlyShowAvaiable() {
		// TODO Auto-generated method stub
		return false;
	}


}
