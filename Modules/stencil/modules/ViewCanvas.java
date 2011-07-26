
package stencil.modules;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import stencil.adapters.general.Registrations;
import stencil.adapters.general.Registrations.Registration;
import stencil.display.CanvasTuple;

import stencil.module.util.BasicModule;
import stencil.module.util.ann.*;
import stencil.display.Display;
import stencil.util.DoubleDimension;

@Description("Screen/Canvas conversion transformations.")
@Module
public class ViewCanvas extends BasicModule {
	
	//Given an original registration and position, what would the X/Y be in the target registration
	@Operator
	@Facet(memUse="OPAQUE", prototype="(double X, double Y)", alias={"map","query"})
	public static double[] translateRegistration(Registration original, double x, double y, double width, double height, Registration target) {
		Point2D topLeft = Registrations.registrationToTopLeft(original, x, y, width, height);
		Point2D targetValue = Registrations.topLeftToRegistration(target, topLeft.getX(), topLeft.getY(), width, height);
		
		return new double[]{targetValue.getX(), targetValue.getY()};
	}
	
	
	@Operator
	@Facet(memUse="OPAQUE", prototype="(double X, double Y)", alias={"map","query"})
	public static double[] screenToCanvasPoint(double x, double y) {
		Point2D p = Display.view.viewToCanvas(new Point2D.Double(x, y));
		return new double[]{p.getX(), p.getY()};
	}

	@Operator
	@Facet(memUse="OPAQUE", prototype="(double Width, double Height)", alias={"map","query"})
	public static double[] screenToCanvasDimension(double width, double height) {
		Dimension2D p = Display.view.viewToCanvas(new DoubleDimension( width, height));
		return new double[]{p.getWidth(), p.getHeight()};
	}


	@Operator
	@Facet(memUse="OPAQUE", prototype="(double X, double Y)", alias={"map","query"})
	public static double[] canvasToScreenPoint(double x, double y) {
		Point2D p = Display.view.canvasToView(new Point2D.Double(x, y));
		return new double[]{p.getX(), p.getY()};
	}

	@Operator
	@Facet(memUse="OPAQUE", prototype="(double Width, double Height)", alias={"map","query"})
	public static double[] canvasToScreenDimension(double width, double height) {
		Dimension2D p = Display.view.canvasToView(new DoubleDimension(width, height));
		return new double[]{p.getWidth(), p.getHeight()};
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
	@Facet(memUse="OPAQUE", prototype="(double Zoom, double X, double Y, double Width)", alias={"map","query"})
	public static double[] zoom(double portalWidth, double portalHeight, double canvasWidth, double canvasHeight) {
		return zoomPadded(portalWidth, portalHeight, canvasWidth, canvasHeight, 0);
	}
	
	/**Calculates a scale factor to keep values undistorted and all visible with a given amount of padding on all sides.
	 * Padding is specified in canvas pixels. 
	 * 
	 */
	@Operator
	@Facet(memUse="OPAQUE", prototype="(double Zoom, double X, double Y, double Width)", alias={"map","query"})
	public static double[] zoomPadded(double portalWidth, double portalHeight, double canvasWidth, double canvasHeight, double pad) {
		CanvasTuple global = Display.canvas;
		
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