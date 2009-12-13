package stencil.tuple.prototype;

public final class SimpleFieldDef implements TupleFieldDef {
	private final String name;
	private final Class type;
	
	public SimpleFieldDef(String name, Class type) {
		this.name = name;
		this.type = type;
	}
	
	public String getFieldName() {return name;}
	public Class getFieldType() {return type;}
}
