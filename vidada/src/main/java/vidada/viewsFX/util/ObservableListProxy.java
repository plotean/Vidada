package vidada.viewsFX.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Proxy for ObservableList
 * @author IsNull
 *
 * @param <T>
 */
public class ObservableListProxy<T> implements ObservableList<T> {

	private final ObservableList<T> original;


	public ObservableListProxy(ObservableList<T>  original){
		if(original == null) throw new IllegalArgumentException("Parameter original must not be NULL!");
		this.original = original;
	}

	@Override
	public int size() {
		return original.size();
	}

	@Override
	public boolean isEmpty() {
		return original.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return original.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return original.iterator();
	}

	@Override
	public Object[] toArray() {
		return original.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return original.toArray(a);
	}

	@Override
	public boolean add(T e) {
		return original.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return original.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return original.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return original.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return original.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return original.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return original.retainAll(c);
	}

	@Override
	public void clear() {
		original.clear();
	}

	@Override
	public T get(int index) {
		return original.get(index);
	}

	@Override
	public T set(int index, T element) {
		return original.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		original.add(index, element);
	}

	@Override
	public T remove(int index) {
		return original.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return original.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return original.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return original.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return original.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return original.subList(fromIndex, toIndex);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		original.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		original.removeListener(listener);
	}

	@Override
	public boolean addAll(T... items) {
		return original.addAll(items);
	}

	@Override
	public void addListener(ListChangeListener<? super T> listener) {
		original.addListener(listener);
	}

	@Override
	public void remove(int start, int end) {
		original.remove(start, end);
	}

	@Override
	public boolean removeAll(T... items) {
		return original.removeAll(original);
	}

	@Override
	public void removeListener(ListChangeListener<? super T> listener) {
		original.removeListener(listener);

	}

	@Override
	public boolean retainAll(T... items) {
		return original.retainAll(original);
	}

	@Override
	public boolean setAll(T... items) {
		return original.setAll(items);
	}

	@Override
	public boolean setAll(Collection<? extends T> items) {
		return original.setAll(items);
	}

}
