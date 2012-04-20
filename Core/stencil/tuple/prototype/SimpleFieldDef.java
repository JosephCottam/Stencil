package stencil.tuple.prototype;

public class SimpleFieldDef<C> implements TupleFieldDef<C> {
	private final String name;
	private final Class type;
	private final C defaultValue;
	private Integer hashCode;
	
	public SimpleFieldDef(String name, Class<C> type, C defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String name() {return name;}
	@Override
	public Class type() {return type;}
	@Override
	public C defaultValue() {return defaultValue;}
	
	@Override
	public String toString() {return name + ":" + type.getSimpleName();}
	
	@Override
	public TupleFieldDef rename(String name) {return new SimpleFieldDef(name, type, defaultValue);}
	
	
	@Override
	public int hashCode() {
		if (hashCode == null) {hashCode = toString().hashCode();}
		return hashCode;
	}
	@Override
	public boolean equals(Object other) {
		if (other == this) {return true;}
		if (!(other instanceof TupleFieldDef)) {return false;}
		TupleFieldDef def = (TupleFieldDef) other;
		if (hashCode != other.hashCode()) {return false;}
		
		return name.equals(def.name()) 
				&& type.equals(def.type()) 
				&& (defaultValue == def.defaultValue() 
						|| (defaultValue != null && defaultValue.equals(def.defaultValue()))); 
	}
}
