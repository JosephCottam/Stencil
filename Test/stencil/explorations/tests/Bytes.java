package stencil.explorations.tests;

public class Bytes {
	public static void main(String[] args) throws Exception {
		for (int i=0;i<300; i++) {
			System.out.println(i + "\t" + Integer.valueOf(i).byteValue());
		}
	}
}
