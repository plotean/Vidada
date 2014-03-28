package vidada.viewsFX.util;

import archimedes.core.data.events.CollectionEventArg;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.exceptions.NotSupportedException;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import vidada.model.pagination.IDataProvider;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Simple adapter to make {@link IDataProvider<T>} compatible with the {@link ObservableList<T>}.
 * 
 * Only read operation are supported, since the {@link IDataProvider<T>} is a read only interface.
 * 
 * @author IsNull
 *
 * @param <T>
 */
public class ObservableListFXAdapter<T> implements IDataProvider<T>, ObservableList<T>{
	private final IDataProvider<T> source;

	private final EventHandlerEx<CollectionEventArg<T>> itemsChangedEvent = new EventHandlerEx<CollectionEventArg<T>>();
	@Override
	public IEvent<CollectionEventArg<T>> getItemsChangedEvent() { return itemsChangedEvent; }


	public ObservableListFXAdapter(IDataProvider<T> source){
		this.source = source;
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
	public T get(int index) {
		return source.get(index);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new VirtualListIterator();
	}

	/***************************************************************************
	 *                                                                         *
	 * Empty event listener                                                    *
	 *                                                                         *
	 **************************************************************************/

	@Override
	public void addListener(InvalidationListener listener) {
		System.out.println("VirtualListAdapter add InvalidationListener");
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		System.out.println("VirtualListAdapter remove InvalidationListener");
	}

	@Override
	public void addListener(ListChangeListener<? super T> arg0) {
		System.out.println("VirtualListAdapter add ListChangeListener");
	}

	@Override
	public void removeListener(ListChangeListener<? super T> arg0) {
		System.out.println("VirtualListAdapter remove ListChangeListener");
	}

	/***************************************************************************
	 *                                                                         *
	 * Not Supported dummy implementation                                      *
	 *                                                                         *
	 **************************************************************************/



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


	private class VirtualListIterator implements ListIterator<T> {

		int pos = 0;

		@Override
		public void forEachRemaining(Consumer<? super T> action) {
			throw new NotSupportedException();
		}

		@Override
		public boolean hasNext() {
			return size() > 0;
		}

		@Override
		public T next() {
			pos++;
			return get(pos);
		}

		@Override
		public boolean hasPrevious() {
			return pos > 0;
		}

		@Override
		public T previous() {
			pos--;
			return get(pos);
		}

		@Override
		public int nextIndex() {
			return pos + 1;
		}

		@Override
		public int previousIndex() {
			return pos - 1;
		}

		@Override
		public void remove() {
			throw new NotSupportedException();
		}

		@Override
		public void set(T e) {
			throw new NotSupportedException();

		}

		@Override
		public void add(T e) {
			throw new NotSupportedException();
		}

	}
}
