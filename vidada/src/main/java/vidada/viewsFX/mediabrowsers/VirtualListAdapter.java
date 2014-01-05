package vidada.viewsFX.mediabrowsers;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import vidada.model.browser.IBrowserItem;
import vidada.model.browser.IListProvider;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.exceptions.NotSupportedException;

/**
 * Virtual data source adapter
 * 
 * 
 * @author IsNull
 *
 * @param <T>
 * @param <ST>
 */
public class VirtualListAdapter<T, ST> implements IListProvider<T>, ObservableList<T> {

	public static interface ITransform<T,ST> { T transform(ST source); }

	private final ITransform<T,ST> transformer;
	private final IListProvider<ST> source;

	private final EventHandlerEx<CollectionEventArg<IBrowserItem>> mediasChangedEvent = new EventHandlerEx<CollectionEventArg<IBrowserItem>>();

	@Override
	public IEvent<CollectionEventArg<IBrowserItem>> getItemsChangedEvent() { return mediasChangedEvent; }


	public VirtualListAdapter(IListProvider<ST> source, ITransform<T,ST> transformer){
		this.source = source;
		this.transformer = transformer;
	}

	@Override
	public void addListener(InvalidationListener listener) {
	}

	@Override
	public void removeListener(InvalidationListener listener) {
	}

	@Override
	public void addListener(ListChangeListener<? super T> arg0) {

	}

	@Override
	public void removeListener(ListChangeListener<? super T> arg0) {

	}


	@Override
	public int size() {
		return source.size();
	}

	@Override
	public boolean isEmpty() {
		return source.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		throw new NotSupportedException();
	}

	@Override
	public Iterator<T> iterator() {
		throw new NotSupportedException();
	}

	@Override
	public Object[] toArray() {
		throw new NotSupportedException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new NotSupportedException();
	}

	@Override
	public boolean add(T e) {
		throw new NotSupportedException();
	}

	@Override
	public boolean remove(Object o) {
		throw new NotSupportedException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new NotSupportedException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new NotSupportedException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new NotSupportedException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new NotSupportedException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new NotSupportedException();
	}

	@Override
	public void replaceAll(UnaryOperator<T> operator) {
		throw new NotSupportedException();
	}

	@Override
	public void sort(Comparator<? super T> c) {
		throw new NotSupportedException();
	}

	@Override
	public void clear() {
		throw new NotSupportedException();
	}

	@Override
	public T get(int index) {
		return transformer.transform(source.get(index));
	}

	@Override
	public T set(int index, T element) {
		throw new NotSupportedException();
	}

	@Override
	public void add(int index, T element) {
		throw new NotSupportedException();
	}

	@Override
	public T remove(int index) {
		throw new NotSupportedException();
	}

	@Override
	public int indexOf(Object o) {
		throw new NotSupportedException();
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new NotSupportedException();
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new NotSupportedException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new NotSupportedException();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new NotSupportedException();
	}

	@Override
	public Spliterator<T> spliterator() {
		throw new NotSupportedException();
	}

	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		throw new NotSupportedException();
	}

	@Override
	public Stream<T> stream() {
		throw new NotSupportedException();
	}

	@Override
	public Stream<T> parallelStream() {
		throw new NotSupportedException();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		throw new NotSupportedException();
	}


	@Override
	public boolean addAll(T... arg0) {
		throw new NotSupportedException();
	}



	@Override
	public FilteredList<T> filtered(Predicate<T> arg0) {
		throw new NotSupportedException();
	}

	@Override
	public void remove(int arg0, int arg1) {
		throw new NotSupportedException();
	}

	@Override
	public boolean removeAll(T... arg0) {
		throw new NotSupportedException();
	}



	@Override
	public boolean retainAll(T... arg0) {
		throw new NotSupportedException();
	}

	@Override
	public boolean setAll(T... arg0) {
		throw new NotSupportedException();
	}

	@Override
	public boolean setAll(Collection<? extends T> arg0) {
		throw new NotSupportedException();
	}

	@Override
	public SortedList<T> sorted() {
		throw new NotSupportedException();
	}

	@Override
	public SortedList<T> sorted(Comparator<T> arg0) {
		throw new NotSupportedException();
	}





}
