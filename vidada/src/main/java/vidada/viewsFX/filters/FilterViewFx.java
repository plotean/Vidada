package vidada.viewsFX.filters;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import vidada.model.browser.FilterModel;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.viewsFX.tags.TagPaneFx;
import archimedesJ.util.Lists;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TextFieldType;

public class FilterViewFx extends BorderPane {

	private final FilterModel filtermodel;

	private final TagPaneFx tagPane;
	private final TextField searchText = new TextField();
	private final CheckBox chkreverse = new CheckBox("Reverse");
	private final ComboBox<MediaType> cboMediaType= new ComboBox<>();
	private final ComboBox<OrderProperty> cboOrder= new ComboBox<>();


	public FilterViewFx(final FilterModel filtermodel){

		this.filtermodel = filtermodel;	
		tagPane = new TagPaneFx(this.filtermodel.getTagStatesModel());

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


		// register event handlers

		chkreverse.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				filtermodel.setReverse(chkreverse.isSelected());
			}
		});


		cboOrder.setPromptText("Define order...");
		cboOrder.setItems(FXCollections.observableList(Lists.asNoNullList(OrderProperty.values())));
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


		cboMediaType.setPromptText("Define mediatype...");
		cboMediaType.setItems(FXCollections.observableList(Lists.asNoNullList(MediaType.values())));
		cboMediaType.valueProperty().addListener(new ChangeListener<MediaType>() {
			@Override 
			public void changed(ObservableValue ov, MediaType t, MediaType t1) {                
				filtermodel.setMediaType(cboMediaType.getValue() != null ? cboMediaType.getValue() : MediaType.ANY);       
			}    
		});

		AquaFx.createTextFieldStyler().setType(TextFieldType.SEARCH).style(searchText); //.createButtonStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(buttonInstance);
	}




}
