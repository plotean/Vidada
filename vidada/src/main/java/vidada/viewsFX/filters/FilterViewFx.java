package vidada.viewsFX.filters;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
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

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TextFieldType;

public class FilterViewFx extends BorderPane implements IFilterView{

	private final TagPaneFx tagPane = new TagPaneFx();
	private final TextField searchText = new TextField();
	private final CheckBox chkreverse = new CheckBox("Reverse");

	private final EventHandlerEx<EventArgs> filterChangedEvent = new EventHandlerEx<>();

	@Override
	public IEvent<EventArgs> getFilterChangedEvent() {
		return filterChangedEvent;
	}



	public FilterViewFx(){

		HBox box = new HBox();

		this.setPadding(new Insets(10));

		searchText.setMinWidth(100);
		box.getChildren().add(searchText);
		box.getChildren().add(chkreverse);

		Insets margrin = new Insets(5,5,10,0);
		HBox.setMargin(searchText, margrin);
		HBox.setMargin(chkreverse, margrin);

		setTop(box);

		setCenter(tagPane);

		AquaFx.createTextFieldStyler().setType(TextFieldType.SEARCH).style(searchText); //.createButtonStyler().setSizeVariant(ControlSizeVariant.REGULAR).style(buttonInstance);
	}



	@Override
	public MediaType getSelectedMediaType() {
		// TODO Auto-generated method stub
		return MediaType.ANY;
	}

	@Override
	public List<Tag> getTagsWithState(TagFilterState state) {
		return tagPane.getTagsWithState(state);
	}

	@Override
	public OrderProperty getSelectedOrder() {
		// TODO Auto-generated method stub
		return OrderProperty.FILENAME;
	}

	@Override
	public boolean isReverseOrder() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return searchText.getText();
	}

	@Override
	public boolean isOnlyShowAvaiable() {
		// TODO Auto-generated method stub
		return false;
	}


}
