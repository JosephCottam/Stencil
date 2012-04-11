package stencil.modules.stencilUtil.range;

public class HybridCache extends CacheHelper {
	int offsetCountdown;
	
	public HybridCache(RangeDescriptor range) {
		super(range);
		if (range.relativeStart()) {throw new RuntimeException("Hybrid ranges must have absolute start points.  Recieved range " + range.toString());}
		offsetCountdown = range.getStart();

	}

	@Override
	public Object[][] update(Object... args) {
		if (offsetCountdown >0) {offsetCountdown--; return new Object[0][0];}
		values.add(args);			
		stateID.incrementAndGet();
		return examine();
	}

	@Override
	public Object[][] examine() {
		return values.subList(0, range.getEnd()).toArray(new Object[range.getEnd()][]);
	}

}
