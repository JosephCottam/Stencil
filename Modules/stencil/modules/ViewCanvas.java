
package stencil.modules;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Registrations.Registration;
import stencil.display.CanvasTuple;
import stencil.display.ViewTuple;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.ApplyCanvas;
import stencil.module.util.ApplyView;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.types.Converter;
import stencil.util.DoubleDimension;

@Description("Screen/Canvas conversion transformations.")
@Module
public class ViewCanvas extends BasicModule {
	
	private static abstract class Base extends AbstractOperator.Statefull implements ApplyView, ApplyCanvas {
		protected Base(OperatorData opData) {super(opData);}
		private ViewTuple view;
		private CanvasTuple canvas;
		
		public void setView(ViewTuple view) {this.view = view;}
		public void setCanvas(CanvasTuple canvas) {this.canvas = canvas;}
		public ViewTuple view() {return view;}
		public CanvasTuple canvas() {return canvas;}
	}
	
	//Given an original registration and position, what would the X/Y be in the target registration
	@Operator
	@Facet(memUse="OPAQUE", prototype="(double X, double Y)", alias={"map","query"})
	public static double[] translateRegistration(Registration original, double x, double y, double width, double height, Registration target) {
		Point2D topLeft = Registrations.registrationToTopLeft(original, x, y, width, height);
		Point2D targetValue = Registrations.topLeftToRegistration(target, topLeft.getX(), topLeft.getY(), width, height);
		
		return new double[]{targetValue.getX(), targetValue.getY()};
	}
	

	@Operator
	public static class ScreenToCanvasPoint extends Base {
		public ScreenToCanvasPoint(OperatorData opData) {super(opData);}
	
		@Facet(memUse="OPAQUE", prototype="(double X, double Y)", alias={"map","query"})
		public double[] map(double x, double y) {
			Point2D p = view().viewToCanvas(new Point2D.Double(x, y));
			return new double[]{p.getX(), p.getY()};
		}
	}

	@Operator
	public static class ScreenToCanvasDimension extends Base {
		public ScreenToCanvasDimension(OperatorData opData) {super(opData);}

		@Facet(memUse="OPAQUE", prototype="(double W, double H)", alias={"map","query"})
		public double[] map(double width, double height) {
			Dimension2D p = view().viewToCanvas(new DoubleDimension( width, height));
			return new double[]{p.getWidth(), p.getHeight()};
		}
	}


	@Operator
	public static class CanvasToScreenPoint extends Base {
		public CanvasToScreenPoint(OperatorData opData) {super(opData);}

		@Facet(memUse="OPAQUE", prototype="(double X, double Y)", alias={"map","query"})
		public double[] map(double x, double y) {
			Point2D p = view().canvasToView(new Point2D.Double(x, y));
			return new double[]{p.getX(), p.getY()};
		}
	}

	@Operator
	public static class CanvasToScreenDimension extends Base {
		public CanvasToScreenDimension(OperatorData opData) {super(opData);}

		@Facet(memUse="OPAQUE", prototype="(double W, double H)", alias={"map","query"})
		public double[] mpa(double width, double height) {
			Dimension2D p = view().canvasToView(new DoubleDimension(width, height));
			return new double[]{p.getWidth(), p.getHeight()};
		}
	}

	/**Calculates the scale factor to keep values undistorted but all objects visible.
	 * If an illegal scale value appears (such as 0, NaN or Inf), the scale value returned is 1.
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 * @param canvasWidth
	 * @param canvasHeight
	 * @return
	 */
	@Operator
	public static class Zoom extends Base {
		public Zoom(OperatorData opData) {super(opData);}

		@Facet(memUse="OPAQUE", prototype="(double Zoom, double X, double Y, double W)", alias={"map","query"})
		public double[] map(double portalWidth, double portalHeight, double canvasWidth, double canvasHeight) {
			return ViewCanvas.zoomPadded(canvas(), portalWidth, portalHeight, canvasWidth, canvasHeight, 0);
		}
	}
	
	@Operator
	public static class ZoomFit extends Base {
		public ZoomFit(OperatorData opData) {super(opData);}
		@Facet(memUse="OPAQUE", prototype="(double Zoom, double X, double Y, double W)", alias={"map","query"})
		public double[] map(double pad) {
			
			return ViewCanvas.zoomPadded(canvas(), 
					Converter.toDouble(view().get("PORTAL_WIDTH")), 
					Converter.toDouble(view().get("PORTAL_HEIGHT")), 
					Converter.toDouble(canvas().get("W")), 
					Converter.toDouble(canvas().get("H")), 
					pad);
			
		}
	}
	
	/**Calculates a scale factor to keep values undistorted and all visible with a given amount of padding on all sides.
	 * Padding is specified in canvas pixels. 
	 * 
	 */
	@Operator
	public static class ZoomPadded extends Base {
		public ZoomPadded(OperatorData opData) {super(opData);}

		@Facet(memUse="OPAQUE", prototype="(double Zoom, double X, double Y, double W)", alias={"map","query"})
		public double[] map(double portalWidth, double portalHeight, double canvasWidth, double canvasHeight, double pad) {
			return ViewCanvas.zoomPadded(canvas(), portalWidth, portalHeight, canvasWidth, canvasHeight, pad);
		}
	}
	
	private static double[] zoomPadded(CanvasTuple global, double portalWidth, double portalHeight, double canvasWidth, double canvasHeight, double pad) {
		double x = global.getX() - pad;
		double y = global.getY() - pad;
		double zy = canvasHeight !=0?portalHeight/canvasHeight:1;
		double zx = canvasWidth !=0?portalWidth/canvasWidth:1;
		double min = Math.min(zx, zy);
		if (min ==0 || Double.isInfinite(min) || Double.isNaN(min)) {min =1;}

		if (min == zx) {
			double newCanvasHeight = canvasHeight/min;
			double newPortalHeight = portalHeight/min;
			y = global.getY() + (newPortalHeight - newCanvasHeight)/2;
		} else {
			double newCanvasWidth = canvasWidth/min;
			double newPortalWidth = portalWidth/min;
			x = global.getX() - (newPortalWidth - newCanvasWidth)/2;
		}
		
		return new double[]{min, x, y, canvasWidth};
	}
}