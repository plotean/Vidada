package vidada.viewsFX.mediabrowsers;

import impl.org.controlsfx.skin.GridViewSkin;

import java.lang.reflect.Field;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import com.sun.javafx.scene.control.skin.VirtualContainerBase;
import com.sun.javafx.scene.control.skin.VirtualFlow;

public class GridViewViewPort {

	private final GridView gridview;
	private VirtualFlow vf;

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

	public GridViewViewPort(GridView gridview){
		this.gridview = gridview;
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
	public CellRange getVisibleCellRange(){

		CellRange outRange = null; 

		try {
			final VirtualFlow vf = getVirtualFlow();

			if(vf != null){
				final IndexedCell firstVisibleRow = vf.getFirstVisibleCell();
				final IndexedCell lastVisibleRow = vf.getLastVisibleCell();

				if(firstVisibleRow != null && lastVisibleRow != null){
					GridCell firstVisibleCell = (GridCell)firstVisibleRow.getChildrenUnmodifiable().get(0);
					ObservableList<Node> lasts = lastVisibleRow.getChildrenUnmodifiable();
					GridCell lastVisibleCell = lasts.size() > 0 ? (GridCell)lasts.get(lasts.size()-1) : null;

					outRange = new CellRange(
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
			outRange = CellRange.Undefined;
			System.err.println("GridViewViewPort: outrange was NULL -> setting undefined");
		}

		return outRange;
	}





}
