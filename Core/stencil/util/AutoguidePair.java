package stencil.util;

import java.util.Arrays;

//final because it is immutable
public final class AutoguidePair<I,R> {
	private I[] input;
	private R[] result;
	
	public AutoguidePair(I[] input, R[] result) {
		this.input = input;
		this.result = result;
	}
	
	public I[] getInput() {return input;}
	public R[] getResult() {return result;}
	
	public String toString() {
		return Arrays.deepToString(input) + ":" + Arrays.deepToString(result);
	}
}
