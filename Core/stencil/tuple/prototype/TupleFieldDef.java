package stencil.tuple.prototype;

public interface TupleFieldDef<C> {
	public String name();
	public Class<C> type();
	public C defaultValue();
	
	/**Return a field def that is identical except for the name.
	 * Must not mutate the current field def.
	 * @param newName
	 * @return
	 */
	public TupleFieldDef rename(String newName);
}