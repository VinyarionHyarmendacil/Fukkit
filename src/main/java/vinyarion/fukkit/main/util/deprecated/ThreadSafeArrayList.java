package vinyarion.fukkit.main.util.deprecated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import com.google.common.base.Throwables;

public class ThreadSafeArrayList<T> extends ArrayList<T> implements ThreadSafeList<T> {

	private final ArrayList<T> add = new ArrayList<T>();
	private final ArrayList<T> rem = new ArrayList<T>();
	private Thread current = Thread.currentThread();
	private Semaphore sem = new Semaphore(1);
	
	public ThreadSafeArrayList() {
		super();
	}
	
	public ThreadSafeArrayList(Collection<? extends T> c) {
		super(c);
	}
	
	public ThreadSafeArrayList(int initialCapacity) {
		super(initialCapacity);
	}
	
	public ThreadSafeArrayList<T> resolve() {
		for(T t : add) {
			this.add(t);
		}
		for(T t : add) {
			this.remove(t);
		}
		return this;
	}
	
	private void lock() {
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			Throwables.propagate(e);
		}
	}
	
	private void unlock() {
		sem.release();
	}
	
	public void add(int index, T element) {
		lock();
		super.add(index, element);
		unlock();
	}
	
	public boolean add(T e) {
		lock();
		boolean ret = super.add(e);
		unlock();
		return ret;
	}
	
	public boolean addAll(Collection<? extends T> c) {
		return super.addAll(c);
	}
	
	public boolean addAll(int index, Collection<? extends T> c) {
		return super.addAll(index, c);
	}
	
	public void clear() {
		super.clear();
	}
	
	public Object clone() {
		return super.clone();
	}
	
	public boolean contains(Object o) {
		return super.contains(o);
	}
	
	public void ensureCapacity(int minCapacity) {
		super.ensureCapacity(minCapacity);
	}
	
	public void forEach(Consumer<? super T> action) {
		super.forEach(action);
	}
	
	public T get(int index) {
		return super.get(index);
	}
	
	public int indexOf(Object o) {
		return super.indexOf(o);
	}
	
	public boolean isEmpty() {
		return super.isEmpty();
	}
	
	public Iterator<T> iterator() {
		return super.iterator();
	}
	
	public int lastIndexOf(Object o) {
		return super.lastIndexOf(o);
	}
	
	public ListIterator<T> listIterator() {
		return super.listIterator();
	}
	
	public ListIterator<T> listIterator(int index) {
		return super.listIterator(index);
	}
	
	public T remove(int index) {
		return super.remove(index);
	}
	
	public boolean remove(Object o) {
		return super.remove(o);
	}
	
	public boolean removeAll(Collection<?> c) {
		return super.removeAll(c);
	}
	
	public boolean removeIf(Predicate<? super T> filter) {
		return super.removeIf(filter);
	}
	
	protected void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
	}
	
	public void replaceAll(UnaryOperator<T> operator) {
		super.replaceAll(operator);
	}
	
	public boolean retainAll(Collection<?> c) {
		return super.retainAll(c);
	}
	
	public T set(int index, T element) {
		return super.set(index, element);
	}
	
	public int size() {
		return super.size();
	}
	
	public void sort(Comparator<? super T> c) {
		super.sort(c);
	}
	
	public Spliterator<T> spliterator() {
		return super.spliterator();
	}
	
	public List<T> subList(int fromIndex, int toIndex) {
		return super.subList(fromIndex, toIndex);
	}
	
	public Object[] toArray() {
		return super.toArray();
	}
	
	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		return super.toArray(a);
	}
	
	public void trimToSize() {
		lock();
		super.trimToSize();
		unlock();
	}
	
}
