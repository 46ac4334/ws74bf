/**
 *
 */
package package18;

import java.awt.geom.PathIterator;
import java.util.function.Function;

/**
 * @author bakis
 *
 */
public class LinearToSemilogPathConverter
		implements Function<java.awt.geom.Path2D.Double, java.awt.geom.Path2D.Double> {

	private final double min;

	public LinearToSemilogPathConverter() {
		super();
		this.min = Double.NEGATIVE_INFINITY;
	}

	public LinearToSemilogPathConverter(final double min) {
		super();
		this.min = min;
	}

	@Override
	public java.awt.geom.Path2D.Double apply(final java.awt.geom.Path2D.Double t) {
		if (t == null) {
			return null;
		}
		if (t instanceof java.awt.geom.Path2D.Double) {
			try {
				final java.awt.geom.Path2D.Double result = new java.awt.geom.Path2D.Double();
				final PathIterator pathIterator = t.getPathIterator(null);
				while (!pathIterator.isDone()) {
					final double[] coords = new double[6];
					final int currentSegment = pathIterator.currentSegment(coords);

					switch (currentSegment) {
					case PathIterator.SEG_MOVETO:
						result.moveTo(coords[0], Math.log10(coords[1] < this.min ? this.min : coords[1]));
						break;
					case PathIterator.SEG_LINETO:
						result.lineTo(coords[0], Math.log10(coords[1] < this.min ? this.min : coords[1]));
						break;
					case PathIterator.SEG_QUADTO:
						result.quadTo(coords[0], Math.log10(coords[1] < this.min ? this.min : coords[1]), coords[2],
								Math.log10(coords[3] < this.min ? this.min : coords[3]));
						break;
					case PathIterator.SEG_CUBICTO:
						result.curveTo(coords[0], Math.log10(coords[1] < this.min ? this.min : coords[1]), coords[2],
								Math.log10(coords[3] < this.min ? this.min : coords[3]), coords[4],
								Math.log10(coords[5] < this.min ? this.min : coords[5]));
						break;
					case PathIterator.SEG_CLOSE:
						result.closePath();
						break;
					default:
						break;
					}
					pathIterator.next();
				}
				return result;
			} catch (final Exception e) {
				return t;
			}
		} else {
			return t;
		}
	}

}
