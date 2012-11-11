package stencil.modules.stencilUtil.range;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**Maintainer of Range caches according to various policies.*/
//TODO: Does this need a take-these-values-and-report-what-udpate-would-return-but-don't-make-changes method?
abstract class CacheHelper implements Cloneable {
	protected final RangeDescriptor range;
	protected final AtomicInteger stateID = new AtomicInteger();	//TODO: Does this need to be volatile?
	protected List<Object[]> values = new ArrayList();
	
	public CacheHelper(RangeDescriptor range) {
		if (range == null) {throw new IllegalArgumentException("Must provide a valid range.");}
		this.range=range;
	}

	/**Updates the collection held in the cache.
	 * Returns the cache after updates have been made.
	 ***/
	abstract public Object[][] update(Object... args);
	
	/**Report the current cache state.*/
	abstract public Object[][] examine();
	
	public CacheHelper viewpoint() {
		CacheHelper h;
		try {h = (CacheHelper) this.clone();}
		catch (CloneNotSupportedException e) {
			throw new Error("Error cloning cloneable CacheHelper...");
		}
		
		h.values = new ArrayList(values);
		return h;
	}
	
	
	public static CacheHelper make(RangeDescriptor range) {
		if (range.isSimple()) {return new EchoCache(range);}
		if (range.relativeStart() && range.relativeEnd()) {return new RelativeCache(range);}
		if (!range.relativeStart() && !range.relativeEnd()) {return new AbsoluteCache(range);}
		if (!range.relativeStart() && range.relativeEnd()) {return new HybridCache(range);}

		throw new IllegalArgumentException("No cache type defined for " + range.toString());
	}
	
	
	public static Object[] flatten(Object[][] unflattened) {
		ArrayList flat = new ArrayList();
		for (Object[] argSet: unflattened) {
			for (Object o:argSet) {
				flat.add(o);
			}
		}
		return flat.toArray();
	}
}
