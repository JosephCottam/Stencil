package stencil.explore.util;

import javax.swing.event.ListDataListener;
import javax.swing.event.EventListenerList;

import stencil.util.collections.ListSet;

public class ListModel<T> extends  ListSet<T> implements javax.swing.ListModel {
	protected EventListenerList listeners = new EventListenerList();
	
	@Override
	public Object getElementAt(int index) {return super.get(index);}
	@Override
	public int getSize() {return super.size();}

	public void addElement(T element) {super.add(element);}
	
	public void setElementAt(T element, int idx) {super.set(idx, element);}
	
	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(ListDataListener.class, l);
	}

}
