package stencil.adapters.java2D.util;

import java.util.concurrent.Callable;

import stencil.adapters.java2D.columnStore.Table;

/**Performs the "dynamic update" operation on a layer.*/
public final class SimpleUpdateTask implements Callable<Finisher> {
	private final Table table;

	public SimpleUpdateTask(Table table) {
		this.table = table;		
	}
	
	public Finisher call() {
		table.viewpoint().simpleUpdate();	//No guard ins need on the simpleUpdate call since it will return immediately if there is no work
		return UpdateTask.NO_WORK;		
	}	
}
