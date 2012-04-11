package stencil.modules.stencilUtil.range;

import java.util.ArrayList;

/**Ranged caching based on stream start/end relative values.**/
public class RelativeCache extends CacheHelper {
	public RelativeCache(RangeDescriptor range) {
		super(range);
		
		if (range.getStart() < range.getEnd()) {throw new IllegalArgumentException("Range ends before it starts: " + range.toString());}
		values = new ArrayList(range.getStart());
	}

	@Override
	public Object[][] update(Object... args) {
		if (values.size() > range.getStart()) {values.remove(0);} //Range.start indicates the oldest value that needs to be remembered.  In an offset, this is the larger number
		values.add(args);
		stateID.incrementAndGet();
		return examine();
	}

	@Override
	public Object[][] examine() {
		//We can always start at 0, since that is the 'oldest' value, but we may
		//have an end that is not the end of the range we must remember
		//(e.g. the range arg was -10 to -5, just five items are returned but you need to remember 10)
		int endRange = values.size()-1 > range.getEnd() ? values.size()-1 - range.getEnd() : 0;
		Object[][] formals = values.subList(0, endRange).toArray(new Object[endRange][]);
		return formals;			
	}

}
