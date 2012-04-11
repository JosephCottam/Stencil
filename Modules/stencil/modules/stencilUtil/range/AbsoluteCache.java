package stencil.modules.stencilUtil.range;

import java.util.ArrayList;

/**Stream caching based on absolute range positions.*/
public class AbsoluteCache extends CacheHelper {
	private boolean trimmed = false;
	private int offsetCountdown;

	
	public AbsoluteCache(RangeDescriptor range) {
		super(range);
		if (range.getStart() > range.getEnd()) {throw new IllegalArgumentException("Range ends before it starts: " + range.toString());}

		values = new ArrayList(range.getEnd());
		offsetCountdown = range.getStart();
	}

	@Override
	public Object[][] update(Object... args) {
		if (offsetCountdown >0) {offsetCountdown--; return new Object[0][0];}
		
		if (values.size() < range.getEnd()) {
			values.add(args);
		} else if (!trimmed) {
			//A range with absolute indices on both start and end will eventually become a constant...
			//so we trim it to that constant value
			values = values.subList(0, range.getEnd()); //The endpoint is exclusive, but range is 1-based, so it all works out!
			trimmed = true;
		}
		stateID.incrementAndGet();
		return examine();

	}

	@Override
	public Object[][] examine() {
		return values.toArray(new Object[values.size()][]);
	}

}
