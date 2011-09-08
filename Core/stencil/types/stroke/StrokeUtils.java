package stencil.types.stroke;

import java.awt.BasicStroke;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.parser.tree.StencilTree;
import stencil.types.Converter;
import stencil.types.stroke.StrokeTuple.Cap;
import stencil.types.stroke.StrokeTuple.Join;
import static stencil.types.stroke.StrokeTuple.*;

@Description("Create and modify strokes.  Strokes control non-color aspects of drawing.")
@Module
public final class StrokeUtils extends BasicModule {
	
	@Operator
	@Facet(memUse="FUNCTION", prototype="(self, width, join, cap, pattern, phase, limit)", alias={"map","query"})
	public static final java.awt.Stroke Width(BasicStroke s, float w) {return new BasicStroke(w, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(self, width, join, cap, pattern, phase, limit)", alias={"map","query"})
	public static final java.awt.Stroke Join(BasicStroke s, Join join) {return new BasicStroke(s.getLineWidth(), s.getEndCap(), join.v, s.getMiterLimit(), s.getDashArray(), s.getDashPhase());}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(self, width, join, cap, pattern, phase, limit)", alias={"map","query"})
	public static final java.awt.Stroke Cap(BasicStroke s, Cap cap) {return new BasicStroke(s.getLineWidth(), cap.v, s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());}
	
	@Operator
	@Facet(memUse="FUNCTION", prototype="(self, width, join, cap, pattern, phase, limit)", alias={"map","query"})
	public static final java.awt.Stroke Phase(BasicStroke s, float phase) {return new BasicStroke(s.getLineWidth(), s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), phase);}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(self, width, join, cap, pattern, phase, limit)", alias={"map","query"})
	public static final java.awt.Stroke Limit(BasicStroke s, float limit) {return new BasicStroke(s.getLineWidth(), s.getEndCap(), s.getLineJoin(), limit, s.getDashArray(), s.getDashPhase());}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(self, width, join, cap, pattern, phase, limit)", alias={"map","query"})
	public static final java.awt.Stroke Pattern(BasicStroke s, Object... input) {
		float[] pattern = Pattern.SOLID.mask;
		
		if (input.length ==1 && input[0] instanceof String) {
			pattern = Pattern.valueOf((String) input[0]).mask;
		} else if (input[0] instanceof StencilTree) {
			throw new Error("Complete Case");
		}
		
		return new BasicStroke(s.getLineWidth(), s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), pattern, s.getDashPhase());
	}
	
	/**The  format for strokes is "weight:pattern:cap";
	 * all other properties must be set with other methods.
	 */
	@Operator
	public static class Stroke extends AbstractOperator {
		public Stroke(OperatorData opData) {super(opData);}
		
		@Facet(memUse="FUNCTION", prototype="(self, width, join, cap, pattern, phase, limit)", alias={"map", "query"})
		public BasicStroke query(String arg) {return parse(arg);}
		
		public static BasicStroke parse(String arg) { 
			String[] parts = arg.split(":");
			
			float weight = Converter.toFloat(parts[0]);
			
			//Parse a pattern, if provided
			float[] pattern = Pattern.SOLID.mask;
			if (parts.length >1 && !parts[1].trim().equals("")) {
				String[] patternParts = parts[1].split("\\s*,\\s*");
				if (patternParts .length ==1) {					
					try {pattern = Pattern.valueOf(patternParts[0]).mask;}
					catch (Exception e) {}
				}

				if (patternParts.length >0 && pattern == null) {
					pattern = new float[patternParts .length];
					for (int i=0; i< patternParts.length ;i++) {
						pattern[i] = Converter.toFloat(patternParts [i]);
					}
				}
			} 

			//If a cap was provided, parse it
			Cap cap = DEFAULT_CAP;
			if (parts.length >2) {
				String part = parts[2];
				cap = Cap.valueOf(part.trim().toUpperCase());
			}
			
			return new BasicStroke(weight, cap.v, DEFAULT_JOIN.v, DEFAULT_LIMIT, pattern, 0f);
		}
	}
}