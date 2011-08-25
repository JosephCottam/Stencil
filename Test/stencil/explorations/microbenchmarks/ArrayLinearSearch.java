package stencil.explorations.microbenchmarks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.pcollections.Empty;
import org.pcollections.PMap;


public class ArrayLinearSearch {
	private static final int ITERS = 10;
	private static final int SIZE = 10000;

	public static int findList(Double value, List<Double> values) {
		int i=0;
		for (Double listVal: values) {
			if (listVal==value) {return i;}
			i++;
		}
		return -1;
	}
	

	public static List initArrayList() {
		List<Double> values = new ArrayList();	
	
		for (int i=0; i< SIZE;i++) {
			values.add(Math.random());
		}
		return values;
	}

	
	public static List initLinkedList() {
		List<Double> values = new LinkedList();	
	
		for (int i=0; i< SIZE;i++) {
			values.add(Math.random());
		}
		return values;
	}
	
	public static int findArray(Double value, Double[] values) {
		for (int i=0; i<values.length; i++) {
			if (values[i]==value) {return i;}
		}
		return -1;
	}
 
	public static Double[] initArray() {
		Double[] values = new Double[SIZE];	
		for (int i=0; i< SIZE;i++) {
			values[i] = Math.random();
		}
		return values;
	}

	private static Map<Double, Double> initMap() {
		Map<Double, Double> m = new HashMap();
		for (int i=0; i< SIZE;i++) {
			m.put(Math.random(), Math.random());
		}
		return m;
	}

	private static Map<Double, Double> initPMap() {
		PMap<Double, Double> m = Empty.map();
		for (int i=0; i< SIZE;i++) {
			m = m.plus(Math.random(), Math.random());
		}
		return m;
	}

	
	public static void main(String[] args) {
		for (int i=0; i<ITERS; i++) {
			double buildStart = System.nanoTime();
			List<Double> values = initArrayList();
			double buildEnd = System.nanoTime();
			

			double start = System.nanoTime();
			for (int j=0; j< ITERS; j++) {
				double v= Math.random();
				findList(v,values);
			}
			double end = System.nanoTime();
			System.out.printf("ArrayList avg -- %1$s (ns) lookup; %2$s build\n", (end-start)/ITERS, (buildEnd-buildStart)/ITERS);			
		}

		
		for (int i=0; i<ITERS; i++) {
			double buildStart = System.nanoTime();
			List<Double> values = initLinkedList();
			double buildEnd = System.nanoTime();
			

			double start = System.nanoTime();
			for (int j=0; j< ITERS; j++) {
				double v= Math.random();
				findList(v,values);
			}
			double end = System.nanoTime();
			System.out.printf("LinkedList avg -- %1$s (ns) lookup; %2$s build\n", (end-start)/ITERS, (buildEnd-buildStart)/ITERS);			
		}
		
		for (int i=0; i< ITERS; i++) {
			double buildStart = System.nanoTime();
			Double[] values = initArray();
			double buildEnd = System.nanoTime();

			
			double start = System.nanoTime();
			for (int j=0; j< ITERS; j++) {
				double v= Math.random();
				findArray(v, values);
			}
			double end = System.nanoTime();
			System.out.printf("Array avg -- %1$s (ns) lookup; %2$s build\n", (end-start)/ITERS, (buildEnd-buildStart)/ITERS);
		}
		
		for (int i=0; i<ITERS; i++) {
			double buildStart = System.nanoTime();
			Map<Double, Double> values = initMap();
			double buildEnd = System.nanoTime();

			double start = System.nanoTime();
			for (int j=0; j< ITERS; j++) {
				double v= Math.random();
				values.get(v);
			}
			double end = System.nanoTime();
			System.out.printf("Map avg -- %1$s (ns) lookup; %2$s build\n", (end-start)/ITERS, (buildEnd-buildStart)/ITERS);			
		}
		
		for (int i=0; i<ITERS; i++) {
			double buildStart = System.nanoTime();
			Map<Double, Double> values = initPMap();
			double buildEnd = System.nanoTime();
			

			double start = System.nanoTime();
			for (int j=0; j< ITERS; j++) {
				double v= Math.random();
				values.get(v);
			}
			double end = System.nanoTime();
			System.out.printf("PMap avg -- %1$s (ns) lookup; %2$s build\n", (end-start)/ITERS, (buildEnd-buildStart)/ITERS);			
		}
	}


}
