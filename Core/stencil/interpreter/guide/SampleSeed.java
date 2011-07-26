package stencil.interpreter.guide;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static stencil.interpreter.guide.SampleSeed.SeedType.*;

/**Report from the echo operator for the guide system.
 * This is combined with the guide specializer to create the
 * sample descriptor.
 **/
public class SampleSeed<T> implements Iterable<T> {
	public enum SeedType {CATEGORICAL, CONTINUOUS, MIXED}
	
	private final SeedType seedType;
	private final List<T> elements;
		
	public SampleSeed(SampleSeed cont, SampleSeed cat) {		
		if (cont.size() ==0) {seedType=CATEGORICAL; elements = cat.elements;}
		else if (cat.size() ==0) {seedType=CONTINUOUS; elements = cont.elements;}
		else {
			seedType = MIXED;
			elements = (List<T>) Arrays.asList(cont, cat);
		}
	}
	public SampleSeed(SeedType seedType, List<T> elements) {
		this.seedType = seedType;
		this.elements = elements;
	}

	public int size() {return elements.size();}

	public boolean isMixed() {return seedType == MIXED;}
	public boolean isCategorical() {return seedType == CATEGORICAL;}	
	public boolean isContinuous() {return seedType == CONTINUOUS;}
	
	public SampleSeed getCategorical() throws UnsupportedOperationException {
		if (seedType == CATEGORICAL){return this;}
		if (seedType == MIXED) {return (SampleSeed) elements.get(1);}
		throw new UnsupportedOperationException("Cannot return a categorical seed from this seed.");
	}
	
	public SampleSeed getContinuous() throws UnsupportedOperationException {
		if (seedType == CONTINUOUS){return this;}
		if (seedType == MIXED) {return (SampleSeed) elements.get(0);}
		throw new UnsupportedOperationException("Cannot return a continuous seed from this seed.");
	}
	
	public T get(int i) {return elements.get(i);}
	public Iterator<T> iterator() {return elements.iterator();}
}
