package package18;

import java.awt.Color;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

public class StaticMethods {

	public static class XFG {
		public double f = Double.NaN;
		public double[] g = null;
		public double[] x = null;
	}

	/**
	 * Gray scale color space
	 */
	private static final ICC_ColorSpace csGray = new ICC_ColorSpace(ICC_Profile.getInstance(ColorSpace.CS_GRAY));

	/**
	 * @param xs the array to be tested
	 * @return <code>true</code> iff all elements of the argument are finite.
	 */
	public static boolean allFinite(final double[] xs) {
		for (final double d : xs) {
			if (Double.isFinite(d)) {
			} else {
				return false;
			}
		}
		return true;
	}

	public static boolean allFinite(final double[][] xss) {
		for (final double[] xs : xss) {
			if (!StaticMethods.allFinite(xs)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Calculates the area of a triangle, given the coordinates of the vertices.
	 *
	 * @param v1 The coordinates of the first vertex. The order of the vertices does
	 *           not affect the result.
	 * @param v2 The coordinates of the second vertex.
	 * @param v3 The coordinates of the third vertex.
	 * @return The area of the triangle.
	 * @throws IllegalArgumentException if any argument is null or if all arguments
	 *                                  do not have the same number of components.
	 */
	public static double areaOfTriangle(final double[] v1, final double[] v2, final double[] v3) {
		if (v1 == null) {
			throw new IllegalArgumentException("First argument \"v1\" is null.");
		}
		if (v2 == null) {
			throw new IllegalArgumentException("Second argument \"v2\" is null.");
		}
		if (v3 == null) {
			throw new IllegalArgumentException("Third argument \"v3\" is null.");
		}
		final int n = v1.length;
		if (v2.length != n) {
			throw new IllegalArgumentException(String
					.format("First argument length is %,d but second argument length is %,d", v1.length, v2.length));
		}
		if (v3.length != n) {
			throw new IllegalArgumentException(String
					.format("First argument length is %,d but third argument length is %,d", v1.length, v3.length));
		}
		double a = 0;// length of side v1-v2;
		double b = 0;// length of side v2-v3;
		double c = 0; // length of side v3-v2;
		for (int i = 0; i < n; ++i) {
			final double d1 = v2[i] - v1[i];
			final double d2 = v3[i] - v2[i];
			final double d3 = v1[i] - v3[i];
			a += d1 * d1;
			b += d2 * d2;
			c += d3 * d3;
		}
		a = Math.sqrt(a);
		b = Math.sqrt(b);
		c = Math.sqrt(c);
		final double s = (a + b + c) / 2;
		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	public static double[] average(final double[][] points) {
		if (points == null) {
			return null;
		}
		if (points.length == 0) {
			return null;
		}
		final double[] result = new double[points[0].length];
		for (final double[] point : points) {
			for (int i = 0; i < result.length; ++i) {
				result[i] += point[i];
			}
		}
		for (int i = 0; i < result.length; ++i) {
			result[i] /= points.length;
		}
		return result;
	}

	/**
	 * @param text the text to be printed
	 * @return lines to be printed.
	 */
	public static String boxPrint(final List<String> msgs) {
		if (msgs == null) {
			return null;
		}
		final String leftOrRight = "│";
		final String topOrBottom = "─";
		final String upperLeft = "┌";
		final String upperRight = "┐";
		final String bottomLeft = "└";
		final String bottomRight = "┘";
		return StaticMethods.boxPrint(msgs, leftOrRight, topOrBottom, upperLeft, upperRight, bottomLeft, bottomRight);
	}

	public static String boxPrint(final List<String> msgs, final String leftOrRight, final String topOrBottom,
			final String upperLeft, final String upperRight, final String bottomLeft, final String bottomRight) {
		if (msgs == null) {
			return null;
		}

		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);

		final List<String> msgs2 = new ArrayList<>();

		for (final String s : msgs) {
			final String[] split = s.split("\\R");
			msgs2.addAll(Arrays.asList(split));
		}

		int maxL = 0;
		for (final String s : msgs2) {
			final int len = s.length();
			if (len > maxL) {
				maxL = len;
			}
		}
		out.print(upperLeft);
		for (int i = 0; i < maxL; ++i) {
			out.print(topOrBottom);
		}
		out.println(upperRight);

		for (final String text : msgs2) {
			out.print(leftOrRight + text);
			for (int i = text.length(); i < maxL; ++i) {
				out.print(" ");
			}
			out.println(leftOrRight);
		}
		out.print(bottomLeft);

		for (int i = 0; i < maxL; ++i) {
			out.print(topOrBottom);
		}
		out.println(bottomRight);
		out.close();
		return sw.toString();
	}

	/**
	 * @param text the text to be printed
	 * @return lines to be printed.
	 */
	public static String boxPrint(final String... text) {
		if (text == null) {
			return null;
		}
		final List<String> list = new ArrayList<>();
		for (final String s : text) {
			final String[] split = s.split("\\R");
			list.addAll(Arrays.asList(split));
		}
		return StaticMethods.boxPrint(list);
	}

	/**
	 * @param text the text to be printed
	 * @return lines to be printed.
	 */
	public static String boxPrintDouble(final List<String> msgs) {
		if (msgs == null) {
			return null;
		}
		final String leftOrRight = "║";
		final String topOrBottom = "═";
		final String upperLeft = "╔";
		final String upperRight = "╗";
		final String bottomLeft = "╚";
		final String bottomRight = "╝";
		return StaticMethods.boxPrint(msgs, leftOrRight, topOrBottom, upperLeft, upperRight, bottomLeft, bottomRight);
	}

	/**
	 * Given a String returned by {@link #boxPrint(List)},
	 * {@link #boxPrint(String...)},
	 * {@link #boxPrint(List, String, String, String, String, String, String)}, or
	 * {@link #boxPrintDouble(List)}, inserts the text of the title in top line of
	 * the box.
	 *
	 * @param title    The text to be inserted in the top border of the box.
	 * @param boxPrint The boxed text
	 * @param nl       the newline string
	 * @return the boxed text with the title inserted in the top border.
	 */
	public static String boxTitle(final String title, final String boxPrint, final String nl) {
		final int titleOffset = 2;
		final String[] split = boxPrint.split("\\R", titleOffset);

		boolean done = false;
		int index = 0;
		final String topLine = split[0];
		final StringBuilder sb = new StringBuilder();
		while (!done) {
			done = index >= topLine.length() && index >= title.length() + titleOffset;
			if (!done) {
				if (index < titleOffset || index >= title.length() + titleOffset) {
					sb.append(topLine.charAt(index));
				} else {
					sb.append(title.charAt(index - titleOffset));
				}
			}
			++index;
		}
		sb.append(nl);
		sb.append(split[1]);
		return sb.toString();
	}

	/**
	 * Leaves the vector unchanged if its L2 norm is less than or equal to
	 * <code>maxNorm</code>, otherwise scales it to make the norm equal to
	 * <code>maxNorm</code>. In either case, returns a newly allocated vector, not
	 * the original argument.
	 *
	 * @param v       The vector to be scaled,
	 * @param maxNorm The maximum L2 norm of the returned vector.
	 * @return a new vector parallel to the original, scaled if necessary to limit
	 *         its L2 norm to <code>maxNorm</code>.
	 */
	public static double[] capL2Norm(final double[] v, final double maxNorm) {
		final double l2Norm = StaticMethods.l2norm(v);
		if (l2Norm > maxNorm) {
			return StaticMethods.normalize(v, maxNorm);
		} else {
			return v.clone();
		}
	}

	public static String compactTimeStamp(final long timeMillis) {
		return String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", timeMillis);
	}

	/**
	 * Cosine of the angle between two vectors.
	 *
	 * @param u first vector.
	 * @param v second vector.
	 * @return cosine of the angle between the given vectors.
	 */
	public static double cosine(final double[] u, final double[] v) {
		if (u == null || v == null) {
			return Double.NaN;
		}
		double dotProduct = 0;
		double umag = 0;
		double vmag = 0;
		for (int i = 0; i < u.length; ++i) {
			final double uElem = u[i];
			final double vElem = v[i];

			dotProduct += uElem * vElem;
			umag += uElem * uElem;
			vmag += vElem * vElem;
		}

		if (umag <= 0) {
			throw new IllegalArgumentException("first argument \"u\" is a zero-length vector.");
		}
		if (vmag <= 0) {
			throw new IllegalArgumentException("second argument \"v\" is a zero-length vector.");
		}
		return dotProduct / Math.sqrt(umag * vmag);

	}

	public static String currentCompactTimeStamp() {
		return String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", System.currentTimeMillis());
	}

	public static double dotProduct(final double[] x1, final double[] x2) {
		if (x1 == null) {
			throw new IllegalArgumentException("First argument x1 is null.");
		}
		if (x2 == null) {
			throw new IllegalArgumentException("Second argument x2 is null.");
		}
		if (x1.length != x2.length) {
			throw new IllegalArgumentException(
					String.format("First vector has %,d elements, second has %,d", x1.length, x2.length));
		}
		double result = 0;
		for (int i = 0; i < x1.length; ++i) {
			result += x1[i] * x2[i];
		}
		return result;
	}

	/**
	 * @param mean
	 * @param covariance
	 * @return
	 * @throws MathArithmeticException
	 */
	public static Shape ellipseFromCovariance(final double[] mean, final RealMatrix covariance)
			throws MathArithmeticException {
		final EigenDecomposition eigenDecomposition = new EigenDecomposition(covariance);
		final double[] eigenvalues = eigenDecomposition.getRealEigenvalues();
		final double[] sigmas = new double[eigenvalues.length];

		for (int i = 0; i < sigmas.length; ++i) {
			sigmas[i] = Math.sqrt(2 * eigenvalues[i]);//
		}
		final double[] eigenvector = eigenDecomposition.getEigenvector(0).unitVector().toArray();

		/*
		 * Draw an ellipse through the given points by transforming a unit circle to the
		 * appropriate shape.
		 */
		final Ellipse2D.Double unitCircle = new Ellipse2D.Double(-1, -1, 2, 2);// Circle, to be transformed to ellipse.
		final AffineTransform ts = AffineTransform.getScaleInstance(sigmas[0], sigmas[1]);
		final AffineTransform tr = AffineTransform.getRotateInstance(eigenvector[0], eigenvector[1]);
		final AffineTransform tt = AffineTransform.getTranslateInstance(mean[0], mean[1]);
		final AffineTransform tc = new AffineTransform();// Composite transform will scale, rotate, and translate
		tc.preConcatenate(ts);// First, scale the major and minor axes of the ellipse,
		tc.preConcatenate(tr);// Then, rotate to align with the eigenvectors
		tc.preConcatenate(tt);// Finally, translate to the center-of-gravity of the given points.
		final Shape ellipse = tc.createTransformedShape(unitCircle);// Apply the transform to the unit circle.
		return ellipse;
	}

	/**
	 * @param t0 System time at the start of the execution unit being timed.
	 * @return A printable message in Unicode.
	 */
	public static String endMessage(final long t0) {
		final StackTraceElement[] stackTrace = new Throwable((String) null).getStackTrace();
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		final StackTraceElement element1 = stackTrace[1];

		final long t1 = System.currentTimeMillis();

		final String msg1 = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL  %2$s  %3$.3g sec elapsed.  \u2502";
		final String msg = String.format(msg1 + System.lineSeparator() + "\u2514", t1,
				element1.getClassName() + "." + element1.getMethodName() + " ENDING. ", (t1 - t0) * 1e-3);
		out.print("\u250c");
		for (int i = 0; i < msg.length() - 3; ++i) {
			out.print(i % 3 == 0 ? ' ' : '\u2500');
		}
		out.print("\u2510" + System.lineSeparator() + "\u2502 " + msg);
		for (int i = 0; i < msg.length() - 3; ++i) {
			out.print('\u2500');
		}
		out.print('\u2518');
		out.close();
		return sw.toString();
	}

	/**
	 * @param exception
	 * @return
	 */
	public static String exceptionReport(final Exception exception) {
		if (exception == null) {
			return "exception == null";
		}
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		out.println();
		out.println("\u250c EXCEPTION \u2500\u2500");
		out.format(String.format("\u2502 %s : %s\n",
				exception.getClass().getSimpleName().replaceFirst("Exception$", ""), exception.getMessage()));
		final StackTraceElement[] stackTrace = exception.getStackTrace();
		for (final StackTraceElement ste : stackTrace) {
			if (!ste.toString().startsWith("java")) {
				out.format("\u2502 at %s\n", ste);
			}
		}
		out.print("\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");

		out.flush();
		return sw.toString();
	}

	public static String fileDescription(final File f) {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		out.println(f.getAbsolutePath());
		out.println(String.format("%16s : %b", "file exists", f.exists()));
		out.println(String.format("%16s : %,d", "length", f.length()));
		out.println(String.format("%16s : %s", "last modified", new Date(f.lastModified()).toString()));
		out.println(String.format("%16s : %b", "is readable", f.canRead()));
		out.println(String.format("%16s : %b", "is writable", f.canWrite()));
		if (f.canRead()) {
			InputStream is = null;
			try {
				final FileInputStream fileInputStream = new FileInputStream(f);
				is = f.getName().endsWith(".gz") ? new GZIPInputStream(fileInputStream) : fileInputStream;
				final byte[] b = new byte[4];
				is.read(b);
				out.println(String.format("%16s : 0x%08x", "First 4 bytes", ByteBuffer.wrap(b).getInt()));
			} catch (final IOException e) {
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (final IOException e) {
					}
				}
			}
		}
		out.flush();
		return sw.toString();
	}

	/**
	 * Returns a GeneralPath object which can be plotted by means of
	 * utilities.PlotterMI to show the contour lines of an arbitrary function.
	 *
	 * @param window         to be plotted, in the argument space of the function
	 *                       being contoured.
	 * @param function       The function whose contours are to be plotted. Argument
	 *                       of the function must by of type <code>double[2]</code>.
	 * @param resolutionsXYZ The resolution in x, y and z directions. The latter
	 *                       specifies the number of contour lines to be plotted
	 *                       between the minimum and maximum values of the function
	 *                       within the window.
	 * @return A <code>Shape</code> representing the requested contour lines.
	 */
	public static Shape getContours(final Rectangle2D.Double window, final Function<double[], Double> function,
			final int[] resolutionsXYZ) {
		if (window == null) {
			throw new IllegalArgumentException("first argument \"window\" is null");
		}
		if (function == null) {
			throw new IllegalArgumentException("second argument \"function\" is null");
		}
		if (resolutionsXYZ == null) {
			throw new IllegalArgumentException("third argument \"resolutionsXYZ\" is null.");
		}

		final double xResolution = resolutionsXYZ[0];
		final double yResolution = resolutionsXYZ[1];
		final double zResolution = resolutionsXYZ[2];
		final double[][] functionValues = new double[(int) (xResolution + 1.5)][(int) (yResolution + 1.5)];
		final double xMin = window.getMinX();
		final double yMin = window.getMinY();
		final double xD = window.getWidth() / xResolution;
		final double yD = window.getHeight() / yResolution;
		double fMin = Double.POSITIVE_INFINITY;
		double fMax = Double.NEGATIVE_INFINITY;
		for (int ix = 0; ix <= xResolution; ++ix) {
			final double x = xMin + ix * xD;
			for (int iy = 0; iy <= yResolution; ++iy) {
				final double y = yMin + iy * yD;
				try {
					final Double f = function.apply(new double[] { x, y });
					functionValues[ix][iy] = f;
					if (f < fMin) {
						fMin = f;
					}
					if (f > fMax) {
						fMax = f;
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
		final GeneralPath plotPath = StaticMethods.getContoursInner1(xResolution, yResolution, zResolution,
				functionValues, xMin, yMin, xD, yD, fMin, fMax);
		return plotPath;

	}

	/**
	 * Generates contour lines for a tabulated function.
	 *
	 * @param xResolution    Count of x values at which contour computation is to be
	 *                       done minus one.
	 * @param yResolution    Count of y values at which contour computation is to be
	 *                       done minus one.
	 * @param zResolution    Count of contour lines to be plotted.
	 * @param functionValues dimension [xResolution+1][yResolution+1]
	 * @param xMin           the x value for the first element in "functionValues"
	 * @param yMin           the y value for the first element in "functionValues"
	 * @param xD             x increment
	 * @param yD             y increment
	 * @param fMin           the minimum function value to be considered
	 * @param fMax           the maximum function value to be considered.
	 * @return the contour lines.
	 */
	public static GeneralPath getContoursInner1(final double xResolution, final double yResolution,
			final double zResolution, final double[][] functionValues, final double xMin, final double yMin,
			final double xD, final double yD, final double fMin, final double fMax) {
		final double zD = (fMax - fMin) / zResolution;
		final GeneralPath plotPath = new GeneralPath();
		for (int ic = 0; ic < zResolution; ++ic) {
			final double c = fMin + (ic + 0.5) * zD;
			for (int ix = 0; ix < xResolution; ++ix) {
				for (int iy = 0; iy < yResolution; ++iy) {
					final double f00 = functionValues[ix][iy];// lower left
					final double f01 = functionValues[ix][iy + 1];// upper left
					final double f10 = functionValues[ix + 1][iy];// lower right
					final double f11 = functionValues[ix + 1][iy + 1];// upper right
					final int index = (f00 > c ? 1 : 0) + (f01 > c ? 2 : 0) + (f10 > c ? 4 : 0) + (f11 > c ? 8 : 0);
					double y0 = 0;
					double x0 = 0;
					double y1 = 0;
					double x1 = 0;

					final double xTop = xMin + xD * (ix + (c - f01) / (f11 - f01));
					final double yTop = yMin + yD * (iy + 1);
					final double xBottom = xMin + xD * (ix + (c - f00) / (f10 - f00));
					final double yBottom = yMin + yD * iy;
					final double xLeft = xMin + xD * ix;
					final double yLeft = yMin + yD * (iy + (c - f00) / (f01 - f00));
					final double xRight = xMin + xD * (ix + 1);
					final double yRight = yMin + yD * (iy + (c - f10) / (f11 - f10));

					switch (index) {
					case 0:
						/*
						 * index = 0000. No f values exceed the threshold. The contour line does not
						 * pass through this rectangle.
						 */
						break;
					case 1:
						/*
						 * index = 0001. Only f00, lower left, exceeds the threshold. Contour line from
						 * left to bottom.
						 */
						y0 = yLeft;
						x0 = xLeft;
						y1 = yBottom;
						x1 = xBottom;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 2:
						/*
						 * index = 0010. Only f01, upper left, exceeds the threshold. Contour line from
						 * top to left.
						 */
						y0 = yTop;
						x0 = xTop;
						y1 = yLeft;
						x1 = xLeft;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 3:
						/*
						 * index = 0011. Lower left and upper left exceed the threshold. Contour line
						 * from top to bottom.
						 */
						y0 = yTop;
						x0 = xTop;
						y1 = yBottom;
						x1 = xBottom;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 4:
						/*
						 * index = 0100. Only f10, lower right, exceeds the threshold. Contour line from
						 * right to bottom.
						 */
						y0 = yRight;
						x0 = xRight;
						y1 = yBottom;
						x1 = xBottom;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 5:
						/*
						 * index = 0101. Lower left and lower right exceed the threshold. Contour line
						 * from left to right.
						 */
						y0 = yLeft;
						x0 = xLeft;
						y1 = yRight;
						x1 = xRight;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 6:
						/*
						 * index = 0110. Lower left and upper left exceed the threshold. Contour lines
						 * from left to right and top to bottom.
						 */
						y0 = yLeft;
						x0 = xLeft;
						y1 = yRight;
						x1 = xRight;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);

						y0 = yTop;
						x0 = xTop;
						y1 = yBottom;
						x1 = xBottom;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);

						break;
					case 7:
						/*
						 * index = 0111. Only upper right does not exceed the threshold. Contour line
						 * from top to right.
						 */
						y0 = yTop;
						x0 = xTop;
						y1 = yRight;
						x1 = xRight;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 8:
						/*
						 * index = 1000. Only upper right exceeds the threshold. Contour line from top
						 * to right.
						 */
						y0 = yTop;
						x0 = xTop;
						y1 = yRight;
						x1 = xRight;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 9:
						/*
						 * index = 1001. Upper right and lower left exceed the threshold. Contour lines
						 * from left to right and top to bottom.
						 */
						y0 = yLeft;
						x0 = xLeft;
						y1 = yRight;
						x1 = xRight;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);

						y0 = yTop;
						x0 = xTop;
						y1 = yBottom;
						x1 = xBottom;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);

						break;
					case 10:
						/*
						 * index = 1010. Upper right and upper left exceed the threshold. Contour line
						 * from left to right.
						 */
						y0 = yLeft;
						x0 = xLeft;
						y1 = yRight;
						x1 = xRight;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 11:
						/*
						 * index = 1011. Only lower right does not exceed the threshold. Contour line
						 * from right to bottom.
						 */
						y0 = yRight;
						x0 = xRight;
						y1 = yBottom;
						x1 = xBottom;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 12:
						/*
						 * index = 1100. Upper right and lower right exceed the threshold. Contour line
						 * from top to bottom.
						 */
						y0 = yTop;
						x0 = xTop;
						y1 = yBottom;
						x1 = xBottom;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 13:
						/*
						 * index = 1101. Only upper left does not exceed the threshold. Contour line
						 * from top to left.
						 */
						y0 = yTop;
						x0 = xTop;
						y1 = yLeft;
						x1 = xLeft;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 14:
						/*
						 * index = 1110. Only lower left does not exceed the threshold. Contour line
						 * from left to bottom.
						 */
						y0 = yLeft;
						x0 = xLeft;
						y1 = yBottom;
						x1 = xBottom;
						plotPath.moveTo(x0, y0);
						plotPath.lineTo(x1, y1);
						break;
					case 15:
						/*
						 * index = 1111. All points are above the threshold. No contour line in this
						 * rectangle.
						 */
						break;
					default:
						final String msg = String.format("Unknown case %d", index);
						throw new RuntimeException(msg);
					}
				}
			}

		}
		return plotPath;
	}

	/**
	 * Given two points with their gradients, return a third point such that the
	 * three points form the vertices of an equilateral triangle. Of the two such
	 * choices for the third point, choose the one with the lower extrapolated
	 * function value.
	 *
	 * @param x1 The coordinates of the first point
	 * @param f1 The function value at the first point
	 * @param g1 The gradient at the first point.
	 * @param x2 The coordinates of the second point.
	 * @param f2 The function value at the second point.
	 * @param g2 The gradient at the second point.
	 * @return the point completing the equilateral triangle as described above.
	 */
	public static double[] getEquilateralPoint(final double[] x1, final double f1, final double[] g1, final double[] x2,
			final double f2, final double[] g2) {
		final double[] firstSide = StaticMethods.vectorDifference(x1, x2);
		final double[] secondSide = StaticMethods.rotateDegrees(firstSide, 60);
		final double[] thirdSide = StaticMethods.rotateDegrees(firstSide, -60);
		final double[] thirdPointA = StaticMethods.vectorSum(x2, secondSide);
		final double[] thirdPointB = StaticMethods.vectorSum(x2, thirdSide);
		final double fa = (f1 + StaticMethods.dotProduct(g1, firstSide) + f2 + StaticMethods.dotProduct(g2, secondSide))
				/ 2;
		final double fb = (f1 + StaticMethods.dotProduct(g1, secondSide) + f2 + StaticMethods.dotProduct(g2, firstSide))
				/ 2;
		return fa < fb ? thirdPointA : thirdPointB;
	}

	/**
	 * Generates an orthonormal basis for the given vectors.
	 *
	 * @param vectors The vectors for which the orthonormal basis is to be computed.
	 * @return A set of orthonormal vectors spanning the space of the argument
	 *         vectors.
	 */
	public static double[][] GramSchmidt(final double[][] vectors) {
		if (vectors == null) {
			throw new IllegalArgumentException("argument is null");
		}
		if (vectors.length == 0) {
			throw new IllegalArgumentException("argument is empty");
		}
		for (int i = 0; i < vectors.length; ++i) {
			if (vectors[i] == null) {
				throw new IllegalArgumentException(String.format("vectors[%d] is null", i));
			}
		}
		final int dim = vectors[0].length;
		if (dim < 1) {
			throw new IllegalArgumentException("vectors[0] is empty");
		}
		for (int i = 1; i < vectors.length; ++i) {
			if (vectors[i].length != dim) {
				throw new IllegalArgumentException(
						String.format("vectors[%d] length is %,d, should be %,d", i, vectors[i].length, dim));
			}
		}
		final double[][] result = new double[vectors.length][dim];

		System.arraycopy(StaticMethods.normalize(vectors[0]), 0, result[0], 0, dim);

		for (int i = 1; i < vectors.length; ++i) {
			final double[] residue = vectors[i].clone();
			for (int j = 0; j < i; ++j) {
				final double projection = StaticMethods.dotProduct(result[j], residue);
				for (int k = 0; k < dim; ++k) {
					residue[k] -= projection * result[j][k];
				}
			}
			System.arraycopy(StaticMethods.normalize(residue), 0, result[i], 0, dim);
		}

		return result;
	}

	/**
	 * Given a desired perceptual brightness for a gray tone, calculates suitable
	 * physical RGB values and combines them into a <code>Color</code> instance.
	 *
	 * @param grayValue The desired perceptual brightness, a numerical value in
	 *                  [0,1].
	 * @return a color suitable for visual representation of the given numerical
	 *         value.
	 */
	public static Color grayLevelToColor(final float grayValue) {
		final float grayValue1 = grayValue < 0 ? 0 : grayValue > 1 ? 1 : grayValue;// Restrict to unit interval.
		final float[] ciexyz = StaticMethods.csGray.toCIEXYZ(new float[] { grayValue1 });// Convert to CIE coordinates.
		final float[] toRGB = StaticMethods.csGray.toRGB(ciexyz);// Convert CIE to RGB values.
		return new Color(toRGB[0], toRGB[1], toRGB[2]);// Color from RGB values.
	}

	/**
	 * Calculates the area of a triangle, given the coordinates of the vertices.
	 *
	 * @param v1 The coordinates of the first vertex. The order of the vertices does
	 *           not affect the result.
	 * @param v2 The coordinates of the second vertex.
	 * @param v3 The coordinates of the third vertex.
	 * @return The area of the triangle.
	 * @throws IllegalArgumentException if any argument is null or if all arguments
	 *                                  do not have the same number of components.
	 */
	public static double heightToBaseRatioOfTriangle(final double[] v1, final double[] v2, final double[] v3) {
		if (v1 == null) {
			throw new IllegalArgumentException("First argument \"v1\" is null.");
		}
		if (v2 == null) {
			throw new IllegalArgumentException("Second argument \"v2\" is null.");
		}
		if (v3 == null) {
			throw new IllegalArgumentException("Third argument \"v3\" is null.");
		}
		final int n = v1.length;
		if (v2.length != n) {
			throw new IllegalArgumentException(String
					.format("First argument length is %,d but second argument length is %,d", v1.length, v2.length));
		}
		if (v3.length != n) {
			throw new IllegalArgumentException(String
					.format("First argument length is %,d but third argument length is %,d", v1.length, v3.length));
		}
		double a = 0;// length of side v1-v2;
		double b = 0;// length of side v2-v3;
		double c = 0; // length of side v3-v2;
		for (int i = 0; i < n; ++i) {
			final double d1 = v2[i] - v1[i];
			final double d2 = v3[i] - v2[i];
			final double d3 = v1[i] - v3[i];
			a += d1 * d1;
			b += d2 * d2;
			c += d3 * d3;
		}
		a = Math.sqrt(a);
		b = Math.sqrt(b);
		c = Math.sqrt(c);
		final double s = (a + b + c) / 2;
		final double area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
		final double base = a > b ? a > c ? a : c : b > c ? b : c;
		final double h = 2 * area / base;
		final double ratio = h / base;
		return ratio;
	}

	/**
	 * @param points    The coordinates of the points. Dimension must be exactly one
	 *                  less than the number of points.
	 * @param gradients The gradients of the function at the given coordinates.
	 * @return The Hessian matrix of a quadratic function which matches the given
	 *         gradients at the given points.
	 * @throws DimensionMismatchException
	 * @throws NoDataException
	 * @throws NullArgumentException
	 * @throws SingularMatrixException
	 * @throws NonSquareMatrixException
	 * @throws OutOfRangeException
	 */
	public static double[][] hessianFromGradients1(final double[][] points, final double[][] gradients)
			throws DimensionMismatchException, NoDataException, NullArgumentException, SingularMatrixException,
			NonSquareMatrixException, OutOfRangeException {

		if (points == null) {
			throw new RuntimeException("First argument \"points\" is null.");
		}
		final int n = points.length - 1;
		if (n < 0) {
			throw new RuntimeException("First argument \"points\" is empty.");
		}
		final int dim = points[0].length;
		if (dim != n) {
			throw new RuntimeException(
					String.format("dimension is %,d, should be exactly %,d for %,d points", dim, n, points.length));
		}

		final int rowCount = n * (n + 1) / 2;
		final double[][] as = new double[rowCount][rowCount];
		final double[] bs = new double[rowCount];

		/*
		 * For each pair of points
		 */
		int rowIndex = 0;
		for (int i = 0; i < n; ++i) {
			for (int j = i + 1; j <= n; ++j) {

				final double[] v = StaticMethods.vectorDifference(points[j], points[i]);// Vector from point "i" to
																						// point
				// "j".
				final double[] u = StaticMethods.normalize(v);// Unit vector from point "i" to point "j".
				final double gg0 = StaticMethods.dotProduct(gradients[i], u);// Directional derivative at first point.
				final double gg1 = StaticMethods.dotProduct(gradients[j], u);// Directional derivative at second point.
				final double len = StaticMethods.l2norm(v);// Length of line segment from first point to second.
				final double d2 = (gg1 - gg0) / len;// Directional 2nd derivative along "u".
				bs[rowIndex] = d2;

				int columnIndex = 0;
				for (int k1 = 1; k1 <= n; ++k1) {
					for (int m1 = 1; m1 <= k1; ++m1) {
						as[rowIndex][columnIndex] = u[k1 - 1] * u[m1 - 1];
						if (k1 != m1) {
							as[rowIndex][columnIndex] *= 2;
						}
						++columnIndex;
					}
				}

				++rowIndex;
			}
		}

		final RealMatrix a = new Array2DRowRealMatrix(as);
		final ArrayRealVector b = new ArrayRealVector(bs);
		try {
			final RealMatrix inverse = MatrixUtils.inverse(a);
			final RealVector solution = inverse.operate(b);

			int columnIndex = 0;
			final double[][] hessian = new double[n][n];
			for (int k1 = 1; k1 <= n; ++k1) {
				for (int m1 = 1; m1 <= k1; ++m1) {
					hessian[k1 - 1][m1 - 1] = hessian[m1 - 1][k1 - 1] = solution.getEntry(columnIndex);
					++columnIndex;
				}
			}
			return hessian;
		} catch (final Exception e) {
			return null;
		}
	}

	public static String horizontalLine(final int len) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; ++i) {
			sb.append('\u2500');
		}
		return sb.toString();
	}

	public static boolean identical(final double[] x, final double[] y) {
		for (int i = 0; i < x.length; ++i) {
			if (x[i] != y[i]) {
				return false;
			}
		}
		return true;
	}

	public static double l2norm(final double[] xs) {
		if (xs == null) {
			return Double.NaN;
		}
		double result = 0;
		for (final double d : xs) {
			if (Double.isFinite(d)) {
				result += d * d;
			} else {
				throw new IllegalArgumentException("argument contains a non-finite element");
			}
		}
		return Math.sqrt(result);
	}

	/**
	 * @param logPath
	 * @return
	 */
	public static String listGeomPath(final java.awt.geom.Path2D.Double logPath) {
		final PathIterator logPathIterator = logPath.getPathIterator(null);
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);

		while (!logPathIterator.isDone()) {
			final double[] coords = new double[2];
			final int currentSegment = logPathIterator.currentSegment(coords);
			String words = null;
			switch (currentSegment) {
			case java.awt.geom.PathIterator.SEG_CLOSE:
				words = "SEG_CLOSE";
				break;
			case java.awt.geom.PathIterator.SEG_CUBICTO:
				words = "SEG_CUBICTO";
				break;
			case java.awt.geom.PathIterator.SEG_LINETO:
				words = "SEG_LINETO";
				break;
			case java.awt.geom.PathIterator.SEG_MOVETO:
				words = "SEG_MOVETO";
				break;
			case java.awt.geom.PathIterator.SEG_QUADTO:
				words = "SEG_QUADTO";
				break;
			default:
				words = String.format("%d", currentSegment);
				break;
			}
			out.format("%s %s%n", words, StaticMethods.simpleVectorPrint(coords));
			logPathIterator.next();
		}
		out.close();
		return sw.toString();
	}

	/**
	 * Dynamically load and instantiate a class object given the binary name of the
	 * class as packageName.className. Class must have a public no-argument
	 * constructor.
	 *
	 * @param appName     The binary name of the class which is to be instantiated.
	 * @param classLoader A ClassLoader which has access to the requested class.
	 * @return A new instance of the requested class.
	 */
	public static Object loadApp(final String appName, final ClassLoader classLoader) {
		try {
			final Class<?> appClass = classLoader.loadClass(appName);
			return appClass.cast(appClass.getConstructor().newInstance());
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static double logistic(final double x) {
		return 1 / (1 + Math.exp(-x));
	}

	/**
	 * @param points Coordinates of the arguments. Dimension must be greater than or
	 *               equal to the number of points minus one.
	 * @param grads  Gradients at the given coordinates
	 * @return Combining weights for the coordinate vectors to get the location of
	 *         the minimum-magnitude gradient.
	 * @throws DimensionMismatchException
	 * @throws NoDataException
	 * @throws NullArgumentException
	 * @throws MathArithmeticException
	 * @throws SingularMatrixException
	 * @throws NonSquareMatrixException
	 * @throws OutOfRangeException
	 */
	public static double[] minGradWeights(final double[][] points, final double[][] grads)
			throws DimensionMismatchException, NoDataException, NullArgumentException, MathArithmeticException,
			SingularMatrixException, NonSquareMatrixException, OutOfRangeException {

		if (points == null) {
			throw new IllegalArgumentException("First argument \"points\" is null");
		}

		for (int i = 0; i < points.length; ++i) {
			if (points[i] == null) {
				throw new IllegalArgumentException(String.format("First argument element points[%,d] is null", i));
			}
		}

		if (grads == null) {
			throw new IllegalArgumentException("Second argument \"grads\" is null");
		}

		for (int i = 0; i < grads.length; ++i) {
			if (grads[i] == null) {
				throw new IllegalArgumentException(String.format("Second argument element grads[%,d] is null", i));
			}
		}

		final int pointCount = points.length;

		if (pointCount < 2) {
			return new double[] { 1 };
		}

		if (grads.length != pointCount) {
			throw new IllegalArgumentException(String.format("%,d points but %,d gradients", pointCount, grads.length));
		}

		final double[][] gammasOld = new double[pointCount][pointCount];
		for (int i = 0; i < pointCount; ++i) {
			for (int j = 0; j < pointCount; ++j) {
				gammasOld[i][j] = StaticMethods.dotProduct(grads[i], grads[j]);
			}
		}

		final double[][] gradsDiff = new double[pointCount - 1][pointCount - 1];
		for (int i = 0; i < pointCount - 1; ++i) {
			gradsDiff[i] = StaticMethods.vectorDifference(grads[0], grads[i + 1]);
		}

		final double[][] gammas = new double[pointCount - 1][pointCount - 1];
		for (int i = 1; i < pointCount; ++i) {
			for (int j = 1; j < pointCount; ++j) {
				gammas[i - 1][j - 1] = StaticMethods.dotProduct(gradsDiff[i - 1], gradsDiff[j - 1]);
			}
		}

		final double[] b = new double[pointCount - 1];
		final double[][] a = new double[pointCount - 1][pointCount - 1];
		for (int i = 1; i < pointCount; ++i) {
			b[i - 1] = StaticMethods.dotProduct(grads[0], gradsDiff[i - 1]);
			for (int j = 1; j < pointCount; ++j) {
				a[i - 1][j - 1] = gammas[i - 1][j - 1];
			}
		}

		final ArrayRealVector bVector = new ArrayRealVector(b);
		final RealMatrix aMatrix = new Array2DRowRealMatrix(a);

		try {
			final RealMatrix inverse = MatrixUtils.inverse(aMatrix);
			final RealVector solution = inverse.operate(bVector);
			if (!StaticMethods.allFinite(solution.toArray())) {
				System.err.println("points:");
				System.err.println(StaticMethods.simpleMatrixPrint(points));
				System.err.println("grads:");
				System.err.println(StaticMethods.simpleMatrixPrint(grads));
				System.err.println("aMatrix:");
				System.err.println(StaticMethods.simpleMatrixPrint(a));
				System.err.println("bVector:" + StaticMethods.simpleVectorPrint(b));
				System.err.println("inverse:");
				System.err.println(StaticMethods.simpleMatrixPrint(inverse.getData()));
				System.err.println("solution:" + StaticMethods.simpleVectorPrint(solution.toArray()));
				throw new RuntimeException("Failed to solve");
			}

			final double[] weights = new double[pointCount];
			weights[0] = 1;
			for (int i = 1; i < weights.length; ++i) {
				weights[i] = solution.getEntry(i - 1);
				weights[0] -= weights[i];
			}
			for (int i = 0; i < weights.length; ++i) {
				if (!Double.isFinite(weights[i])) {
					throw new RuntimeException(String.format("weights[%,d] == %.3g", i, weights[i]));
				}
			}
			return weights;
		} catch (final SingularMatrixException e) {
			e.printStackTrace();
			System.out.println("Singular matrix:");
			System.out.println(StaticMethods.simpleMatrixPrint(a));
			System.out.println("Returning null");
			return null;
		}
	}

	/**
	 * Assume that the gradient of a quadratic function at point
	 * <i>x</i><sub>1</sub> is <i>g</i><sub>1</sub>, and the gradient at
	 * <i>x</i><sub>2</sub> is <i>g</i><sub>2</sub>. Then draw a straight line
	 * through <i>x</i><sub>1</sub> and <i>x</i><sub>2</sub> and let <i>s</i> be a
	 * linear distance measure along that line such that
	 * <i>s</i>(<i>x</i><sub>1</sub>)=0 and <i>s</i>(<i>x</i><sub>2</sub>)=1. Then
	 * the gradient at any point along that line is a linear function of <i>s</i>.
	 * This method returns the value of <i>s</i> at which the magnitude of this
	 * gradient assumes its minimum value. Note that the value of <i>s</i> is not
	 * confined to the unit interval. The result is undefined if the gradients at
	 * the two points are identical.
	 *
	 * @param g1 Gradient at first point.
	 * @param g2 Gradient at second point.
	 * @return A value <code>s</code> such that the minimum gradient magnitude
	 *         occurs at <code>(1-s)*x1
	 *         + s*x2</code>. Returns <code>NaN</code> if <code>g1</code> and
	 *         <code>g2</code> are identical.
	 */
	public static double minimumGradientLocation(final double[] g1, final double[] g2) {
		final double[] g12 = StaticMethods.vectorDifference(g1, g2);
		final double dotProduct = StaticMethods.dotProduct(g12, g12);
		return dotProduct == 0 ? Double.NaN : StaticMethods.dotProduct(g12, g1) / dotProduct;
	}

	public static double NonQuadraticity(final double[] x1, final double f1, final double[] g1, final double[] x2,
			final double f2, final double[] g2) {
		double s = 0;
		for (int i = 0; i < x1.length; ++i) {
			s += (x2[i] - x1[i]) * (g1[i] + g2[i]);
		}
		final double df = f2 - f1;// Actual change in function value.
		final double dg = s / 2;// Function value change predicted from gradients.
		return (df - dg) / dg;
	}

	/**
	 * Returns a new vector of unit length, parallel to the argument vector. Does
	 * not modify the argument.
	 *
	 * @param xs The vector to be normalized.
	 * @return A new vector of unit length, in the same direction as the argument.
	 */
	public static double[] normalize(final double[] xs) {
		final double norm = StaticMethods.l2norm(xs);
		final double[] result = xs.clone();
		if (norm > 0) {
			for (int i = 0; i < result.length; ++i) {
				result[i] /= norm;
			}
		}
		return result;
	}

	public static double[] normalize(final double[] xs, final double c) {
		final double norm = c / StaticMethods.l2norm(xs);
		final double[] result = xs.clone();
		for (int i = 0; i < result.length; ++i) {
			result[i] *= norm;
		}
		return result;
	}

	/**
	 * Calculates a normalized measure of the deviation of a function from a pure
	 * quadratic, on the basis of function and gradient values at two points.
	 *
	 * @param x1 The coordinates of the first point.
	 * @param f1 The function value at the first point.
	 * @param g1 The gradient vector at the first point.
	 * @param x2 The coordinates of the second point.
	 * @param f2 The function value at the second point.
	 * @param g2 The gradient vector at the second point.
	 * @return a normalized difference between the actual function value change and
	 *         the change implied by the average gradient.
	 */
	public static double NormNonQuadraticity(final double[] x1, final double f1, final double[] g1, final double[] x2,
			final double f2, final double[] g2) {
		double s1 = 0;
		double s2 = 0;
		for (int i = 0; i < x1.length; ++i) {
			s1 += (x2[i] - x1[i]) * g1[i];
			s2 += (x2[i] - x1[i]) * g2[i];
		}
		final double df = f2 - f1;// Actual change in function value.
		final double dg = (s1 + s2) / 2;// Function value change predicted from average gradient.
		final double norm = Math.sqrt(s1 * s1 + s2 * s2 + df * df);
		return (df - dg) / norm;
	}

	/**
	 * @param g1 The gradient at the first point
	 * @param g2 The gradient at the second point
	 * @param x1 The coordinates of the first point
	 * @param x2 The coordinates of the second point
	 * @return A value <code>s</code> such that the orthogonal gradient occurs at
	 *         <code>(1-s)*x1
	 *         + s*x2</code>.
	 */
	public static double orthogonalGradientLocation(final double[] g1, final double[] g2, final double[] x1,
			final double[] x2) {
		final double[] step = StaticMethods.vectorDifference(x2, x1);
		final double gd1 = StaticMethods.projectAtoB(g1, step);
		final double gd2 = StaticMethods.projectAtoB(g2, step);
		final double s = gd1 / (gd1 - gd2);
		return s;
	}

	/**
	 * Returns the dot product of the vectors divided by the length of the second
	 * vector.
	 *
	 * @param a Vector to be projected
	 * @param b Vector onto which the first vector will be projected
	 * @return The length of the projection of the first vector to the normalized
	 *         second vector.
	 */
	public static double projectAtoB(final double[] a, final double[] b) {
		if (a == null) {
			throw new IllegalArgumentException("First argument \"a\" is null.");
		}
		if (b == null) {
			throw new IllegalArgumentException("Second argument \"b\" is null.");
		}
		if (a.length != b.length) {
			throw new IllegalArgumentException(String.format(
					"First argument length is %,d, second argument length is %,d.  " + "Lengths should be equal.",
					a.length, b.length));
		}
		final DoubleBuffer aBuf = DoubleBuffer.wrap(a);
		final DoubleBuffer bBuf = DoubleBuffer.wrap(b);
		double ab = 0;
		double bb = 0;
		while (aBuf.hasRemaining()) {
			final double aval = aBuf.get();
			final double bval = bBuf.get();
			ab += aval * bval;
			bb += bval * bval;
		}
		return ab / Math.sqrt(bb);
	}

	public static double quadraticDim(final int parameterCount) {
		return (Math.sqrt(8 * parameterCount + 1) - 3) / 2;
	}

	public static int quadraticParameterCount(final int dim) {
		return dim * (dim + 3) / 2 + 1;
	}

	/**
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws RuntimeException
	 */
	public static byte[] readFourBytesFromStream(final InputStream is) throws IOException, RuntimeException {
		final byte[] fourBytes = new byte[4];
		final int bytesRead = is.read(fourBytes);
		if (bytesRead != 4) {
			throw new RuntimeException(String.format("Expected 4 bytes but read only %d.", bytesRead));
		}
		return fourBytes;
	}

	public static XFG Rosenbrock2D(final double x, final double y, final double a, final double b) {
		final double f = (a - x) * (a - x) + b * (y - x * x) * (y - x * x);
		final double gx = 2 * x - 2 * a + 4 * b * x * x * x - 4 * b * x * y;
		final double gy = 2 * b * y - 2 * b * x * x;
		final XFG result = new XFG();
		result.f = f;
		result.g = new double[] { gx, gy };
		result.x = new double[] { x, y };
		return result;
	}

	/**
	 * @param vector  The vector to be rotated.
	 * @param degrees the angle of rotation in degrees.
	 * @return the rotated vector in the plane defined by the first two coordinates
	 *         of the argument vector.
	 */
	public static double[] rotateDegrees(final double[] vector, final double degrees) {
		final double radians = Math.toRadians(degrees);
		final double cos = Math.cos(radians);
		final double sin = Math.sin(radians);
		final double[] result = vector.clone();
		final double x = vector[0];
		final double y = vector[1];
		result[0] = cos * x - sin * y;
		result[1] = sin * x + cos * y;
		return result;
	}

	/**
	 * @param vector  The 2d vector to be rotated.
	 * @param degrees the angle of rotation in degrees.
	 * @return the rotated vector.
	 */
	public static Vector<Number> rotateDegrees(final Vector<Number> vector, final double degrees) {
		final double radians = Math.toRadians(degrees);
		final double cos = Math.cos(radians);
		final double sin = Math.sin(radians);
		final Vector<Number> result = new Vector<>();
		final double x = vector.get(0).doubleValue();
		final double y = vector.get(1).doubleValue();
		result.add(cos * x - sin * y);
		result.add(sin * x + cos * y);
		return result;
	}

	public static String simpleMatrixPrint(final double[][] data) {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		if (data == null) {
			out.format("(null)");
		} else {
			int rowIndex = 0;
			final int rowCount = data.length;
			for (final double[] row : data) {
				for (final double d : row) {
					out.format("%10.3g", d);
				}
				if (rowIndex < rowCount - 1) {
					out.println();
				}
			}
			++rowIndex;
		}
		out.flush();
		out.close();
		return sw.toString();
	}

	public static String simpleScalarPrint(final double d) {
		return String.format("%10.3g", d);
	}

	public static String simpleVectorPrint(final double[] data) {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		if (data == null) {
			out.format("(null)");
		} else {
			for (final double d : data) {
				out.format("%10.3g", d);
			}
		}
		out.flush();
		out.close();
		return sw.toString();
	}

	public static String simpleVectorPrint(final double[] data, final int i) {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		if (data == null) {
			out.format("(null)");
		} else {
			final String format = "%" + (7 + i) + "." + i + "g";
			for (final double d : data) {
				out.format(format, d);
			}
		}
		out.flush();
		out.close();
		return sw.toString();
	}

	public static String simpleVectorPrint(final float[] data) {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		if (data == null) {
			out.format("(null)");
		} else {
			for (final double d : data) {
				out.format("%10.3g", d);
			}
		}
		out.flush();
		out.close();
		return sw.toString();
	}

	/**
	 * @param n    max number of components to print
	 * @param data The array to print
	 * @return printable string
	 */
	public static String simpleVectorPrint(final int n, final double[] data) {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		if (data == null) {
			out.format("(null)");
		} else {
			int i = 0;
			for (final double d : data) {
				if (i < n) {
					out.format("%10.3g", d);
					++i;
				} else {
					if (data.length > n) {
						out.format(" ...");
					}
					break;
				}

			}
		}
		out.flush();
		out.close();
		return sw.toString();
	}

	public static double[] softmax(final double[] scores) {
		if (scores == null) {
			return null;
		}
		final double[] result = new double[scores.length];
		if (scores.length > 0) {
			if (scores.length == 1) {
				result[0] = 1;
			} else {
				double z = 0;
				for (int i = 0; i < scores.length; ++i) {
					final double q = Math.exp(scores[i]);
					z += q;
					result[i] = q;
				}
				for (int i = 0; i < scores.length; ++i) {
					result[i] /= z;
				}
			}
		}
		return result;
	}

	public static String stacktrace(final String msg) {
		final StackTraceElement[] stackTrace = new Throwable(msg).getStackTrace();
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		out.format("%s at %s", msg, stackTrace[1]);
		out.close();
		return sw.toString();
	}

	public static String stacktraceAll(final String msg) {
		final StackTraceElement[] stackTrace = new Throwable(msg).getStackTrace();
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		out.print(msg);
		for (int i = 1; i < stackTrace.length; ++i) {
			out.format("\nat %s", stackTrace[i]);
		}
		out.close();
		return sw.toString();
	}

	/**
	 * @param t0 System time at the start of the execution being timed.
	 * @return A message specifying the starting time and the method from which this
	 *         method was called.
	 */
	public static String startMessage(final long t0) {
		final StackTraceElement[] stackTrace = new Throwable((String) null).getStackTrace();
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		final StackTraceElement element1 = stackTrace[1];
		final String msg1 = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL  ", t0) + element1.getClassName()
				+ "." + element1.getMethodName() + " STARTING in " + Thread.currentThread() + " \u2502";
		final String msg = msg1 + System.lineSeparator() + "\u2514";
		out.print("\u250c");
		for (int i = 0; i < msg1.length(); ++i) {
			out.print('\u2500');
		}
		out.print("\u2510" + System.lineSeparator() + "\u2502 " + msg);
		for (int i = 0; i < msg1.length(); ++i) {
			out.print(i % 3 == 0 ? ' ' : '\u2500');
		}
		out.print('\u2518');
		out.close();
		return sw.toString();
	}

	/**
	 * @param text the text to be printed
	 * @return lines to be printed.
	 */
	public static String ulinePrint(final List<String> msgs) {
		if (msgs == null) {
			return null;
		}
		final String leftOrRight = " ";
		final String topOrBottom = "─";
		final String upperLeft = " ";
		final String upperRight = " ";
		final String bottomLeft = " ";
		final String bottomRight = " ";
		return StaticMethods.boxPrint(msgs, leftOrRight, topOrBottom, upperLeft, upperRight, bottomLeft, bottomRight);
	}

	public static double[] vectorDifference(final double[] minuend, final double[] subtrahend) {
		if (minuend == null || subtrahend == null) {
			return null;
		}
		final double[] result = new double[minuend.length];
		for (int i = 0; i < minuend.length; ++i) {
			result[i] = minuend[i] - subtrahend[i];
		}
		return result;
	}

	/**
	 * Subtracts the second vector, the subtrahend, from the first vector, the
	 * minuend.
	 *
	 * @param minuend
	 * @param subtrahend
	 * @return minuend-subtrahend as a <code>double[]</code> array.
	 */
	public static double[] vectorDifference(final Vector<Number> minuend, final double[] subtrahend) {
		final double[] result = new double[subtrahend.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = minuend.get(i).doubleValue() - subtrahend[i];
		}
		return result;
	}

	/**
	 * Subtracts the first vector from the second, returns the difference.
	 *
	 * @param x1 first vector, the subtrahend.
	 * @param x2 second vector, the minuend.
	 * @return the difference x2 - x1.
	 */
	public static Vector<Number> vectorDifference(final Vector<Number> x1, final Vector<Number> x2) {
		if (x1 == null || x2 == null) {
			return null;
		}
		final Vector<Number> result = new Vector<>();
		final Iterator<Number> x1Iterator = x1.iterator();
		final Iterator<Number> x2Iterator = x2.iterator();
		while (x1Iterator.hasNext() && x2Iterator.hasNext()) {
			result.add(x2Iterator.next().doubleValue() - x1Iterator.next().doubleValue());
		}
		return result;
	}

	public static double[] vectorLinearCombiner(final double[] weights, final double[][] vectors) {
		if (weights == null) {
			throw new IllegalArgumentException("First argument \"weights\" is null.");
		}
		final int nw = weights.length;
		if (nw < 1) {
			throw new IllegalArgumentException("First argument \"weights\" is empty.");
		}
		if (!StaticMethods.allFinite(weights)) {
			System.err.println("weights:" + StaticMethods.simpleVectorPrint(weights));
			throw new IllegalArgumentException("Not all weights are finite.");
		}
		if (vectors == null) {
			throw new IllegalArgumentException("Second argument \"vectors\" is null.");
		}
		final int nv = vectors.length;
		if (nv != nw) {
			throw new IllegalArgumentException(
					String.format("%,d weights but %,d vectors – counts should be equal.", nw, nv));
		}
		int n = 0;
		for (final double[] v : vectors) {
			n = Math.max(n, v.length);
		}
		final double[] results = new double[n];
		Arrays.fill(results, 0);
		int iw = 0;
		for (final double[] v : vectors) {
			for (int i = 0; i < v.length; ++i) {
				results[i] += v[i] * weights[iw];
			}
			++iw;
		}
		return results;
	}

	public static double[] vectorSum(final double[] x1, final double[] x2) {
		final double[] result = x1.clone();
		for (int i = 0; i < result.length; ++i) {
			result[i] += x2[i];
		}
		return result;
	}

	/**
	 * Returns the weights for the three points. The weighted average of the
	 * coordinates gives the coordinates of the minimum-gradient point. The weighted
	 * average of the gradients gives the gradient at that point.
	 *
	 * @param γ11 The dot product of the gradient at point 1 with itself.
	 * @param γ12 The dot product of the gradients at points 1 and 2.
	 * @param γ13 The dot product of the gradients at points 1 and 3.
	 * @param γ22 The dot product of the gradient at point 2 with itself.
	 * @param γ23 The dot product of the gradients at points 2 and 3.
	 * @param γ33 The dot product of the gradient at point 3 with itself.
	 * @return the weights.
	 */
	private static double[] w123(final double γ11, final double γ12, final double γ13, final double γ22,
			final double γ23, final double γ33) {
		final double d = γ12 * γ12 + γ13 * γ13 + γ23 * γ23 - γ11 * γ22 + 2 * γ11 * γ23 - 2 * γ12 * γ13 - γ11 * γ33
				- 2 * γ12 * γ23 + 2 * γ13 * γ22 + 2 * γ12 * γ33 - 2 * γ13 * γ23 - γ22 * γ33;
		final double w1 = (γ23 * γ23 - γ12 * γ23 + γ13 * γ22 + γ12 * γ33 - γ13 * γ23 - γ22 * γ33) / d;
		final double w2 = (γ13 * γ13 + γ11 * γ23 - γ13 * γ12 - γ11 * γ33 - γ13 * γ23 + γ12 * γ33) / d;
		return new double[] { w1, w2, 1 - w1 - w2 };
	}

	/**
	 * Accepts coordinates and gradients for exactly three points, returns the
	 * coordinates of the point where the predicted gradient has the smallest l2
	 * norm, as well as the predicted gradient at that point. Requires at least two
	 * dimensions.
	 *
	 * @param xs coordinates of three points &ndash; first index: point, second
	 *           index: dimension.
	 * @param gs gradients at the three points.
	 * @return first index: 0 for coordinates, 1 for gradients; second index:
	 *         dimension. first index 2 for weights.
	 */
	public static double[][] xgmin(final double[][] xs, final double[][] gs) {
		if (xs == null) {
			throw new IllegalArgumentException("First argument \"xs\" is null.");
		}
		final int n = xs.length;
		if (n != 3) {
			throw new IllegalArgumentException(
					String.format("First argument \"xs\" contains %,d points, but should contain 3.", n));
		}
		if (gs == null) {
			throw new IllegalArgumentException("Second argument \"gs\" is null.");
		}
		if (gs.length != n) {
			throw new IllegalArgumentException(
					String.format("Second argument \"gs\" contains %,d points, but should contain %,d.", gs.length, n));
		}
		for (int i = 0; i < n; ++i) {
			if (xs[i] == null) {
				throw new IllegalArgumentException(String.format("xs[%,d] is null", i));
			}
			if (gs[i] == null) {
				throw new IllegalArgumentException(String.format("gs[%,d] is null", i));
			}
		}
		final int dim = xs[0].length;
		if (dim < 2) {
			throw new IllegalArgumentException(String.format("dimension is %,d, but should be at least 2", dim));
		}
		for (int i = 0; i < n; ++i) {
			if (xs[i].length != dim) {
				throw new IllegalArgumentException(String.format("xs[%,d] length is %,d, but should be %,d", i, dim));
			}
			if (gs[i].length != dim) {
				throw new IllegalArgumentException(String.format("gs[%,d] length is %,d, but should be %,d", i, dim));
			}
			for (int j = 0; j < dim; ++j) {
				if (!Double.isFinite(xs[i][j])) {
					throw new IllegalArgumentException(String.format("xs[%,d][%,d] is %f", i, j, xs[i][j]));
				}
				if (!Double.isFinite(gs[i][j])) {
					throw new IllegalArgumentException(String.format("gs[%,d][%,d] is %f", i, j, gs[i][j]));
				}
			}
		}
		final double[] g1 = gs[0];
		final double[] g2 = gs[1];
		final double[] g3 = gs[2];

		final double g11 = StaticMethods.dotProduct(g1, g1);
		final double g12 = StaticMethods.dotProduct(g1, g2);
		final double g13 = StaticMethods.dotProduct(g1, g3);
		final double g22 = StaticMethods.dotProduct(g2, g2);
		final double g23 = StaticMethods.dotProduct(g2, g3);
		final double g33 = StaticMethods.dotProduct(g3, g3);

		final double[] weights = StaticMethods.w123(g11, g12, g13, g22, g23, g33);

		final double[] xStar = StaticMethods.vectorLinearCombiner(weights, xs);
		final double[] gStar = StaticMethods.vectorLinearCombiner(weights, gs);

		return new double[][] { xStar, gStar, weights };
	}

}
