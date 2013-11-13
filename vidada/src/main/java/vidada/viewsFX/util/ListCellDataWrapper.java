package vidada.viewsFX.util;

import javafx.scene.Node;
import javafx.scene.control.ListCell;

public class ListCellDataWrapper<T> extends ListCell<T> {

	private final IDataContext<T> contextHolder;

	public ListCellDataWrapper(Node visual, IDataContext<T> contextHolder){
		this.contextHolder = contextHolder;
		setGraphic(visual);
	}

	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		contextHolder.setDataContext(item);
	}

	public static <T> ListCellDataWrapper<T> build(Node visual, IDataContext<T> contextHolder){
		return new ListCellDataWrapper<T>(visual, contextHolder);
	}
}
