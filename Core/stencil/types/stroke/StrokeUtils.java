package stencil.types.stroke;

import java.awt.BasicStroke;

import stencil.module.operator.util.BasicProject;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.parser.tree.StencilNumber;
import stencil.types.Converter;
import stencil.types.stroke.StrokeTuple.Cap;
import stencil.types.stroke.StrokeTuple.Join;
import static stencil.types.stroke.StrokeTuple.*;

public final class StrokeUtils extends BasicModule {
	public static final java.awt.Stroke Width(BasicStroke s, float w) {return new BasicStroke(w, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());}
	public static final java.awt.Stroke Join(BasicStroke s, Join join) {return new BasicStroke(s.getLineWidth(), s.getEndCap(), join.v, s.getMiterLimit(), s.getDashArray(), s.getDashPhase());}
	public static final java.awt.Stroke Cap(BasicStroke s, Cap cap) {return new BasicStroke(s.getLineWidth(), cap.v, s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());}
	public static final java.awt.Stroke Phase(BasicStroke s, float phase) {return new BasicStroke(s.getLineWidth(), s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), phase);}
	public static final java.awt.Stroke Limit(BasicStroke s, float limit) {return new BasicStroke(s.getLineWidth(), s.getEndCap(), s.getLineJoin(), limit, s.getDashArray(), s.getDashPhase());}
	public static final java.awt.Stroke Pattern(BasicStroke s, Object... input) {
		float[] pattern = Pattern.SOLD.mask;
		
		if (input.length ==1 && input[0] instanceof String) {
			pattern = Pattern.valueOf((String) input[0]).mask;
		} else if (input[0] instanceof StencilNumber) {
			
		}
		
		
		return new BasicStroke(s.getLineWidth(), s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), pattern, s.getDashPhase());
	}
	
	/**The  format for strokes is "weight:pattern";
	 * all other properties must be set with other methods.
	 */
	public static class Stroke extends BasicProject {
		public Stroke(OperatorData opData) {super(opData);}
		public StrokeTuple query(double width) {return new StrokeTuple(new BasicStroke((float) width));}
		
		public BasicStroke argumentParser(String arg) {return parse(arg);}
		public static BasicStroke parse(String arg) { 
			String[] parts = arg.split(":");
			float weight = Converter.toFloat(parts[0]);
			float[] pattern = Pattern.SOLD.mask;
			if (parts.length >1) {
				parts = parts[1].split("\\s*,\\s*");
				if (parts.length ==1) {					
					try {pattern = Pattern.valueOf(parts[0]).mask;}
					catch (Exception e) {}
				}

				if (parts.length >0 && pattern == null) {
					pattern = new float[parts.length];
					for (int i=0; i< parts.length;i++) {
						pattern[i] = Converter.toFloat(parts[i]);
					}
				}
			}			
			return new BasicStroke(weight, DEFAULT_CAP.v, DEFAULT_JOIN.v, DEFAULT_LIMIT, pattern, 0f);
		}
	}
	
	public StrokeUtils(ModuleData md) {super(md);}
}