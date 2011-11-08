package stencil.modules.animate;


public interface Interpolator<T> {

	T step(float f);
	
	public static class Factory {
		public static Interpolator<?> get(Object a, Object b) {
			// TODO: make this cleaner and extensible
			Object o = (b != null ? b : a);
			if (o instanceof Integer) {
				return new IntInterpolator(a, b);
			} else if (o instanceof Long) {
				return new LongInterpolator(a, b);
			} else if (o instanceof Float) {
				return new FloatInterpolator(a, b);
			} else if (o instanceof Number) {
				return new DoubleInterpolator(a, b);
			}
		}
	}
	
	
	public static class IntInterpolator implements Interpolator<Integer> {
		public int a, b;
		public IntInterpolator(Object a, Object b) {
			this.a = (Integer)a;
			this.b = (Integer)b;
		}
		public Integer step(float f) {
			return a + ((int)(f*(b-a)));
		}
	}
	
	public static class LongInterpolator implements Interpolator<Long> {
		public long a, b;
		public LongInterpolator(Object a, Object b) {
			this.a = (Long)a;
			this.b = (Long)b;
		}
		public Long step(float f) {
			return a + ((long)(((double)f)*(b-a)));
		}
	}
	
	public static class FloatInterpolator implements Interpolator<Float> {
		public float a, b;
		public FloatInterpolator(Object a, Object b) {
			this.a = (Float)a;
			this.b = (Float)b;
		}
		public Float step(float f) {
			return a + f*(b-a);
		}
	}
	
	public static class DoubleInterpolator implements Interpolator<Double> {
		public double a, b;
		public DoubleInterpolator(Object a, Object b) {
			this.a = (Double)a;
			this.b = (Double)b;
		}
		public Double step(float f) {
			return a + f*(b-a);
		}
	}
}
