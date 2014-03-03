package vidada.viewsFX.mediabrowsers;

import impl.org.controlsfx.skin.GridViewSkin;

import java.lang.reflect.Field;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;

import com.sun.javafx.scene.control.skin.VirtualContainerBase;
import com.sun.javafx.scene.control.skin.VirtualFlow;

/**
 * Workaround / Hack to get information and change events about the current view port.
 * 
 * @author IsNull
 *
 */
public class GridViewViewPort {

	/***************************************************************************
	 *                                                                         *
	 * Static                                                                  *
	 *                                                                         *
	 **************************************************************************/


	private static Field flowField;

	static {
		try {
			flowField = VirtualContainerBase.class.getDeclaredField("flow");
			flowField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	private final GridView gridview;
	private VirtualFlow vf;
	private boolean registered;
	private IndexRange visibleCells = null;

	private final EventHandlerEx<EventArgs> viewPortItemsChanged = new EventHandlerEx<>();

	/**
	 * Raised when the items in the current viewport have been changed
	 * @return
	 */
	public IEvent<EventArgs> getViewPortItemsChanged(){ return viewPortItemsChanged; }


	public GridViewViewPort(GridView gridview){
		this.gridview = gridview;
	}

	/***************************************************************************
	 *                                                                         *
	 * Public methods                                                          *
	 *                                                                         *
	 **************************************************************************/



	/**
	 * Ensures that a scroll listener is added to the internal ScrollBar to track view port change events.
	 */
	public void ensureViewportChangedListener(){
		if(!registered){

			for (Node node: gridview.lookupAll(".scroll-bar")) {
				if (node instanceof ScrollBar) {
					System.out.println("found scrollbar!");
					registered = true;
					final ScrollBar bar = (ScrollBar) node;
					bar.valueProperty().addListener(new ChangeListener<Number>() {
						@Override public void changed(ObservableValue<? extends Number> value, Number oldValue, Number newValue) {
							//System.out.println(bar.getOrientation() + " " + newValue);
							updateVisibleCells();
						}
					});
				}
			}
		}
	}

	/**
	 * Gets the visible cell range (cached)
	 * @return
	 */
	public IndexRange getVisibleCellRange(){
		return visibleCells;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/



	/**
	 * Updates the visible cells
	 */
	private void updateVisibleCells(){
		IndexRange currentVisibleCells = this.fetchVisibleCellRange();

		if(!currentVisibleCells.equals(visibleCells))
		{
			visibleCells = currentVisibleCells;
			viewPortItemsChanged.fireEvent(this, EventArgs.Empty);
		}

		visibleCells = currentVisibleCells;
	}


	private VirtualFlow getVirtualFlow(){

		if(vf == null){
			GridViewSkin skin = (GridViewSkin)gridview.getSkin();
			VirtualContainerBase virtalContainer= skin;
			try {
				vf = (VirtualFlow)flowField.get(virtalContainer);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return vf;
	}


	/**
	 * Gets the visible cell range in this gridview viewport
	 * @param outRange
	 * @return
	 */
	private IndexRange fetchVisibleCellRange(){

		IndexRange outRange = null; 

		try {
			final VirtualFlow vf = getVirtualFlow();

			if(vf != null){
				final IndexedCell firstVisibleRow = vf.getFirstVisibleCell();
				final IndexedCell lastVisibleRow = vf.getLastVisibleCell();

				if(firstVisibleRow != null && lastVisibleRow != null){
					final ObservableList<Node> firsts = firstVisibleRow.getChildrenUnmodifiable();
					final GridCell firstVisibleCell =  firsts.size() > 0 ? (GridCell)firsts.get(0) : null;
					final ObservableList<Node> lasts = lastVisibleRow.getChildrenUnmodifiable();
					final GridCell lastVisibleCell = lasts.size() > 0 ? (GridCell)lasts.get(lasts.size()-1) : null;

					outRange = new IndexRange(
							firstVisibleCell != null ? firstVisibleCell.getIndex() : 0,
									lastVisibleCell != null ? lastVisibleCell.getIndex() : 0);
				}else{
					System.err.print("GridViewViewPort: firstVisibleRow := " + firstVisibleRow + ", lastVisibleRow := " + lastVisibleRow);
				}
			}else{
				System.err.println("GridViewViewPort: VirtualFlow := NULL!");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		if(outRange == null){
			outRange = IndexRange.Undefined;
			System.err.println("GridViewViewPort: outrange was NULL -> setting undefined");
		}

		return outRange;
	}





}
