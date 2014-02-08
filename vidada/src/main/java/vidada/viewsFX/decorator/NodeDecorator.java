package vidada.viewsFX.decorator;

import java.util.List;
import java.util.Set;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.EventDispatchChain;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.SnapshotResult;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

import com.sun.javafx.accessible.providers.AccessibleProvider;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;


/**
 * NodeDecorator is the base class for all Node Decorators.
 * This implementation redirects all calls to the inner Node and acts as a transparent proxy.
 * @author pascal.buettiker
 *
 */
public class NodeDecorator<T extends Node> extends Node {

	private T inner;

	/**
	 * Create a new NodeDecorator
	 * @param inner
	 */
	public NodeDecorator(T inner){
		setInner(inner);
	}

	/**
	 * Gets the inner Node
	 * @return
	 */
	public T getInner() {
		return inner;
	}

	protected void setInner(T inner){
		this.inner = inner;
	}


	@Override
	public final BaseBounds impl_computeGeomBounds(BaseBounds arg0, BaseTransform arg1) {
		return getInner().impl_computeGeomBounds(arg0, arg1);
	}

	@Override
	protected final NGNode impl_createPeer() {
		return getInner().impl_getPeer();
	}

	@Override
	public final Object impl_processMXNode(MXNodeAlgorithm arg0, MXNodeAlgorithmContext arg1) {
		return getInner().impl_processMXNode(arg0, arg1);
	}


	@Override
	protected final boolean impl_computeContains(double arg0, double arg1) {
		// will never be called
		return false;
	}


	//
	// All public methods are redirected to inner
	//

	@Override
	public EventDispatchChain buildEventDispatchChain(EventDispatchChain arg0) {
		return getInner().buildEventDispatchChain(arg0);
	}

	@Override
	public double computeAreaInScreen() {
		return getInner().computeAreaInScreen();
	}

	@Override
	public boolean contains(double arg0, double arg1) {
		return getInner().contains(arg0, arg1);
	}

	@Override
	public boolean contains(Point2D arg0) {
		return getInner().contains(arg0);
	}

	@Override
	public double getBaselineOffset() {
		return getInner().getBaselineOffset();
	}

	@Override
	public Orientation getContentBias() {
		return getInner().getContentBias();
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getInner().getCssMetaData();
	}

	@Override
	public Styleable getStyleableParent() {
		return getInner().getStyleableParent();
	}

	@Override
	public String getTypeSelector() {
		return getInner().getTypeSelector();
	}

	@Override
	public Object getUserData() {
		return getInner().getUserData();
	}

	@Override
	public boolean hasProperties() {
		return getInner().hasProperties();
	}

	@Override
	public AccessibleProvider impl_getAccessible() {
		return getInner().impl_getAccessible();
	}

	@Override
	public <P extends NGNode> P impl_getPeer() {
		return getInner().impl_getPeer();
	}

	@Override
	public boolean impl_hasTransforms() {
		return getInner().impl_hasTransforms();
	}

	@Override
	public void impl_transformsChanged() {
		getInner().impl_transformsChanged();
	}

	@Override
	public void impl_updatePeer() {
		getInner().impl_updatePeer();
	}

	@Override
	public boolean intersects(Bounds arg0) {
		return getInner().intersects(arg0);
	}

	@Override
	public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
		return getInner().intersects(arg0, arg1, arg2, arg3);
	}

	@Override
	public boolean isResizable() {
		return getInner().isResizable();
	}

	@Override
	public Bounds localToParent(Bounds arg0) {
		return getInner().localToParent(arg0);
	}

	@Override
	public Point3D localToParent(double arg0, double arg1, double arg2) {
		return getInner().localToParent(arg0, arg1, arg2);
	}

	@Override
	public Point2D localToParent(double arg0, double arg1) {
		return getInner().localToParent(arg0, arg1);
	}

	@Override
	public Point2D localToParent(Point2D arg0) {
		return getInner().localToParent(arg0);
	}

	@Override
	public Point3D localToParent(Point3D arg0) {
		return getInner().localToParent(arg0);
	}

	@Override
	public Bounds localToScene(Bounds arg0) {
		return getInner().localToScene(arg0);
	}

	@Override
	public Point3D localToScene(double arg0, double arg1, double arg2) {
		return getInner().localToScene(arg0, arg1, arg2);
	}

	@Override
	public Point2D localToScene(double arg0, double arg1) {
		return getInner().localToScene(arg0, arg1);
	}

	@Override
	public Point2D localToScene(Point2D arg0) {
		return getInner().localToScene(arg0);
	}

	@Override
	public Point3D localToScene(Point3D arg0) {
		return getInner().localToScene(arg0);
	}

	@Override
	public Bounds localToScreen(Bounds arg0) {
		return getInner().localToScreen(arg0);
	}

	@Override
	public Point2D localToScreen(double arg0, double arg1, double arg2) {
		return getInner().localToScreen(arg0, arg1, arg2);
	}

	@Override
	public Point2D localToScreen(double arg0, double arg1) {
		return getInner().localToScreen(arg0, arg1);
	}

	@Override
	public Point2D localToScreen(Point2D arg0) {
		return getInner().localToScreen(arg0);
	}

	@Override
	public Point2D localToScreen(Point3D arg0) {
		return getInner().localToScreen(arg0);
	}

	@Override
	public Node lookup(String arg0) {
		return getInner().lookup(arg0);
	}

	@Override
	public Set<Node> lookupAll(String arg0) {
		return getInner().lookupAll(arg0);
	}

	@Override
	public double maxHeight(double arg0) {
		return getInner().maxHeight(arg0);
	}

	@Override
	public double maxWidth(double arg0) {
		return getInner().maxWidth(arg0);
	}

	@Override
	public double minHeight(double arg0) {
		return getInner().minHeight(arg0);
	}

	@Override
	public double minWidth(double arg0) {
		return getInner().minWidth(arg0);
	}

	@Override
	public Bounds parentToLocal(Bounds arg0) {
		return getInner().parentToLocal(arg0);
	}

	@Override
	public Point3D parentToLocal(double arg0, double arg1, double arg2) {
		return getInner().parentToLocal(arg0, arg1, arg2);
	}

	@Override
	public Point2D parentToLocal(double arg0, double arg1) {
		return getInner().parentToLocal(arg0, arg1);
	}

	@Override
	public Point2D parentToLocal(Point2D arg0) {
		return getInner().parentToLocal(arg0);
	}

	@Override
	public Point3D parentToLocal(Point3D arg0) {
		return getInner().parentToLocal(arg0);
	}

	@Override
	public double prefHeight(double arg0) {
		return getInner().prefHeight(arg0);
	}

	@Override
	public double prefWidth(double arg0) {
		return getInner().prefWidth(arg0);
	}

	@Override
	public void relocate(double arg0, double arg1) {
		getInner().relocate(arg0, arg1);
	}

	@Override
	public void requestFocus() {
		getInner().requestFocus();
	}

	@Override
	public void resize(double arg0, double arg1) {
		getInner().resize(arg0, arg1);
	}

	@Override
	public void resizeRelocate(double arg0, double arg1, double arg2,
			double arg3) {
		getInner().resizeRelocate(arg0, arg1, arg2, arg3);
	}

	@Override
	public Bounds sceneToLocal(Bounds arg0) {
		return getInner().sceneToLocal(arg0);
	}

	@Override
	public Point3D sceneToLocal(double arg0, double arg1, double arg2) {
		return getInner().sceneToLocal(arg0, arg1, arg2);
	}

	@Override
	public Point2D sceneToLocal(double arg0, double arg1) {
		return getInner().sceneToLocal(arg0, arg1);
	}

	@Override
	public Point2D sceneToLocal(Point2D arg0) {
		return getInner().sceneToLocal(arg0);
	}

	@Override
	public Point3D sceneToLocal(Point3D arg0) {
		return getInner().sceneToLocal(arg0);
	}

	@Override
	public Bounds screenToLocal(Bounds arg0) {
		return getInner().screenToLocal(arg0);
	}

	@Override
	public Point2D screenToLocal(double arg0, double arg1) {
		return getInner().screenToLocal(arg0, arg1);
	}

	@Override
	public Point2D screenToLocal(Point2D arg0) {
		return getInner().screenToLocal(arg0);
	}

	@Override
	public void setUserData(Object arg0) {
		getInner().setUserData(arg0);
	}

	@Override
	public void snapshot(Callback<SnapshotResult, Void> arg0,
			SnapshotParameters arg1, WritableImage arg2) {
		getInner().snapshot(arg0, arg1, arg2);
	}

	@Override
	public WritableImage snapshot(SnapshotParameters arg0, WritableImage arg1) {
		return getInner().snapshot(arg0, arg1);
	}

	@Override
	public Dragboard startDragAndDrop(TransferMode... arg0) {
		return getInner().startDragAndDrop(arg0);
	}

	@Override
	public void startFullDrag() {
		getInner().startFullDrag();
	}

	@Override
	public void toBack() {
		getInner().toBack();
	}

	@Override
	public void toFront() {
		getInner().toFront();
	}

	@Override
	public String toString() {
		return getInner().toString();
	}

	@Override
	public boolean usesMirroring() {
		return getInner().usesMirroring();
	}

	@Override
	public int hashCode() {
		return getInner().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return getInner().equals(obj);
	}

}
