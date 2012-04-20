package stencil.adapters.java2D.render.mixins;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import stencil.WorkingDir;
import stencil.display.SchemaFieldDef;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;

public interface Imager {
	public static final double AUTO_SCALE = -1;

	public BufferedImage image(Tuple t, AffineTransform viewTrans);
	
	public class Const implements Imager {
		private final BufferedImage base;
		private final double width, height;
		
		public Const(String filename, double width, double height) {
			filename = WorkingDir.resolve(filename);
			base = Util.load(filename);
			this.width = width;
			this.height = height;
		}
		
		@Override
		public BufferedImage image(Tuple t, AffineTransform viewTrans) {
			return Util.scale(base, width, height, viewTrans);
		}
	}

	
	public class ConstImg implements Imager {
		private final BufferedImage base;
		private final int widthIdx, heightIdx;
		
		public ConstImg(String filename, int widthIdx, int heightIdx) {
			filename = WorkingDir.resolve(filename);
			base = Util.load(filename);
			this.widthIdx = widthIdx;
			this.heightIdx = heightIdx;
		}
		
		@Override
		public BufferedImage image(Tuple t, AffineTransform viewTrans) {
			double width = (Double) t.get(widthIdx);
			double height = (Double) t.get(heightIdx);
			return Util.scale(base, width, height, viewTrans);
		}
	}
	
	public class Variable implements Imager {
		private final int fileIdx;
		private final int widthIdx, heightIdx;

		public Variable(int fileIdx, int widthIdx, int heightIdx) {
			this.fileIdx = fileIdx;
			this.widthIdx = widthIdx;
			this.heightIdx = heightIdx;
		}
		
		@Override
		public BufferedImage image(Tuple t, AffineTransform viewTrans) {
			String filename = WorkingDir.resolve((String) t.get(fileIdx));
			BufferedImage base = Util.load(filename);
			double width = (Double) t.get(widthIdx);
			double height = (Double) t.get(heightIdx);
			return Util.scale(base, width, height, viewTrans);
		}
	}
	
	
	
	public static final class Util {

		public static double autoScale(double width, double height, BufferedImage base) {
			if (base == null) {return 1;} 								//Nothing to scale yet
			if (width == AUTO_SCALE && height == AUTO_SCALE) {return 1;}//No scale specified
			if (width == AUTO_SCALE) {return height/base.getHeight();}	//Scale width based on height specified
			if (height == AUTO_SCALE) {return width/base.getWidth();}	//Scale width based on height specified
			return 1;//Default scale factor
		}


		
		public static BufferedImage scale(BufferedImage base, double requestWidth, double requestHeight, AffineTransform viewTrans) {
			double width = requestWidth != AUTO_SCALE ? requestWidth : base.getWidth() * Util.autoScale(requestWidth, requestHeight, base);
			double height = requestHeight !=  AUTO_SCALE ? requestHeight : base.getHeight() * Util.autoScale(requestWidth, requestHeight, base);
			Point2D dim = new Point2D.Double(width, height);
			viewTrans.deltaTransform(dim, dim);
			width = dim.getX();
			height = dim.getY();
			
			if (base.getWidth() == requestWidth && base.getHeight() == requestHeight) {return base;}
			
			double sx = (width/base.getWidth());
			double sy = (height/base.getWidth());

			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(sx,sy), AffineTransformOp.TYPE_BICUBIC);
			BufferedImage display = op.createCompatibleDestImage(base,null);
			op.filter(base, display);
			return display;
		}
		
		public static BufferedImage load(String file) {
			String filename = WorkingDir.resolve(file);
			try {return javax.imageio.ImageIO.read(new File(filename));}
			catch (Exception e) {throw new RuntimeException("Error loading image: " + file);}
		}
		
		public static Imager instance(TuplePrototype<SchemaFieldDef> schema, int fileIdx, int widthIdx, int heightIdx) {
			assert fileIdx >= 0 && widthIdx >=0 && heightIdx >=0;
			
			SchemaFieldDef<String> fileDef = schema.get(fileIdx);
			SchemaFieldDef<Double> widthDef = schema.get(widthIdx);
			SchemaFieldDef<Double> heightDef = schema.get(heightIdx);

			if (fileDef.isConstant() && widthDef.isConstant() && heightDef.isConstant()) {
				return new Const(fileDef.defaultValue(), widthDef.defaultValue(), heightDef.defaultValue());
			} else if (fileDef.isConstant()) {
				return new ConstImg(fileDef.defaultValue(), widthIdx, heightIdx);
			} else {
				return new Variable(fileIdx, widthIdx, heightIdx);
			}
		}
		
	}
}
