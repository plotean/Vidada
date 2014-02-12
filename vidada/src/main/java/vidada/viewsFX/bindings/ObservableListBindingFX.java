package vidada.viewsFX.bindings;

import java.util.Collection;

import javafx.collections.ObservableList;
import archimedesJ.data.observable.binding.BindingContext;
import archimedesJ.data.observable.binding.IBindingContext;

/**
 * Represents a one way binding between a observable list and a other collection
 *
 * @param <T>
 */
public class ObservableListBindingFX<T> {


	public static <T> ObservableListBindingFX<T> bind(ObservableList<T> source, Collection<T> target){
		ObservableListBindingFX<T> test = new ObservableListBindingFX<T>(source, target);
		test.bind();
		return test;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private Fields                                                          *
	 *                                                                         *
	 **************************************************************************/
	private final IBindingContext context = new BindingContext();
	private final ObservableList<T> source;
	private final Collection<T> target;


	private BindingListenerFX<T> sourceListener = null;

	/***************************************************************************
	 *                                                                         *
	 * Constructor                                                             *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates a binding between the two collections.
	 * 
	 * The binding is one-directional and synchronizes (changes) only the target.
	 * 
	 * @param source
	 * @param target
	 * @param bidirectional
	 */
	public ObservableListBindingFX(ObservableList<T> source, Collection<T> target){
		this.source = source;
		this.target = target;
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	public ObservableList<T> getSource(){
		return source;
	}

	public Collection<T> getTarget(){
		return target;
	}

	/**
	 * Creates the binding
	 */
	public void bind(){
		unbind();
		source.addListener(sourceListener = new BindingListenerFX<T>(context, target));
	}

	/**
	 * Removes the binding
	 */
	public void unbind(){
		if(sourceListener != null)
		{
			source.removeListener(sourceListener);
			sourceListener = null;
		}
	}

	protected IBindingContext getContext(){
		return context;
	}
}
