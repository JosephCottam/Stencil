package stencil.explorations.microbenchmarks;

import java.lang.reflect.*;

public class ReflectiveInvokeArgTypes {

	public static final int add(int v1, int v2) {return v1+v2;}

	public static void main(String[] args) throws Exception {
		Method m = ReflectiveInvokeArgTypes.class.getMethod("add", new Class[]{int.class, int.class});
		System.out.println(m.invoke(null, new Object[]{1,2}));
	}
	
}
