package stencil.tuple.prototype;

public class SimpleFieldDef<C> implements TupleFieldDef<C> {
	private final String name;
	private final Class type;
	private final C defaultValue;
	private final int hashCode;
	
	public SimpleFieldDef(String name, Class<C> type, C defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.hashCode = toString().hashCode();
	}
	
	public String name() {return name;}
	public Class type() {return type;}
	public C defaultValue() {return defaultValue;}
	
	public String toString() {return name + ":" + type.getSimpleName();}
	
	public TupleFieldDef rename(String name) {return new SimpleFieldDef(name, type, defaultValue);}
	
	
	public int hashCode() {return hashCode;}
	public boolean equals(Object other) {
		if (other == this) {return true;}
		if (!(other instanceof TupleFieldDef)) {return false;}
		TupleFieldDef def = (TupleFieldDef) other;
		
		
		return name.equals(def.name()) 
				&& type.equals(def.type()) 
				&& (defaultValue == def.defaultValue() 
						|| (defaultValue != null && defaultValue.equals(def.defaultValue()))); 
	}
}
