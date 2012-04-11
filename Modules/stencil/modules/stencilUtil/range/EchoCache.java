package stencil.modules.stencilUtil.range;

/**When caching isn't actually required, this cache is used.**/
public class EchoCache extends CacheHelper {
	public EchoCache(RangeDescriptor range) {super(range);}

	@Override
	public Object[][] update(Object... vals) {return new Object[][]{vals};}

	@Override
	public Object[][] examine() {return new Object[][]{};}
}
