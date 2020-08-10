/**
 *
 */
package sandbox;

import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.util.Random;

import package18.LinearToSemilogPathConverter;
import package18.StaticMethods;

/**
 * @author bakis
 *
 */
public class TestSemiLogPathConversion implements Runnable {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final TestSemiLogPathConversion tester = new TestSemiLogPathConversion();
		final Thread thread = new Thread(tester);
		thread.start();
	}

	@Override
	public void run() {

		System.out.println("Demo the sandbox.LinearToSemilogPathConverter");
		/*
		 * Construct a small hypothetical linear xy path. For this demo, use only four
		 * points, and let y = x, in other words, a 45° straight line. Note that the
		 * input path does not have to be a straight line, but this makes a convenient
		 * demo.
		 */
		final Double linearPath = new Path2D.Double();

		linearPath.moveTo(.1, .1);
		linearPath.lineTo(1, 1);
		linearPath.lineTo(2, 2);
		linearPath.lineTo(3, 3);
		/*
		 * Print the input path on the standard output stream:
		 */
		final String listGeomPath = StaticMethods.listGeomPath(linearPath);// create a printable listing of the path
		System.out.format("input:%n%s%n", listGeomPath);// print the listing

		/*
		 * Convert the path to semi-log scale
		 */
		final LinearToSemilogPathConverter converter = new LinearToSemilogPathConverter(.01);// Instantiate the
																								// converter

		final Double logPath = converter.apply(linearPath);// Apply the converter to the linear path

		/*
		 * Print the results
		 */
		final String listLogPath = StaticMethods.listGeomPath(logPath);// create a printable listing of the path
		System.out.format("expected result: x values are unchanaged, y values are%nlogarithms of original values.%n%s",
				listLogPath);// print the listing

		System.out.println();
		System.out.println(StaticMethods.horizontalLine(32));
		System.out.println();

		/*
		 * Now test how the converter handles values it cannot convert -- negative y
		 * values: It should not crash but should produce a NaN output value when the
		 * input y is non-positive.
		 */
		final Double linearPath1 = new Path2D.Double();

		linearPath1.moveTo(.1, .1);
		linearPath1.lineTo(1, -1);
		linearPath1.lineTo(2, 2);
		linearPath1.lineTo(3, -3);
		/*
		 * Print the input path on the standard output stream:
		 */
		final String listGeomPath1 = StaticMethods.listGeomPath(linearPath1);// create a printable listing of the path
		System.out.format("input:%n%s%n", listGeomPath1);// print the listing

		final Double logPath1 = converter.apply(linearPath1);// Apply the converter to the linear path

		/*
		 * Print the results
		 */
		final String listLogPath1 = StaticMethods.listGeomPath(logPath1);// create a printable listing of the path
		System.out
				.format("expected result: x values are unchanaged, y values are either%nlogarithms of original values,"
						+ " or NaN if the original y<=0.%n%s%n", listLogPath1);// print the listing

		System.out.println();
		System.out.println(StaticMethods.horizontalLine(32));
		System.out.println();

		/*
		 * Now demonstrate on a random path:
		 */
		final Random random = new Random();

		final Double linearPath2 = new Path2D.Double();

		linearPath2.moveTo(random.nextGaussian(), random.nextGaussian());
		linearPath2.lineTo(random.nextGaussian(), random.nextGaussian());
		linearPath2.lineTo(random.nextGaussian(), random.nextGaussian());
		linearPath2.lineTo(random.nextGaussian(), random.nextGaussian());
		/*
		 * Print the input path on the standard output stream:
		 */
		final String listGeomPath2 = StaticMethods.listGeomPath(linearPath2);// create a printable listing of the path
		System.out.format("random input:%n%s%n", listGeomPath2);// print the listing

		final Double logPath2 = converter.apply(linearPath2);// Apply the converter to the linear path

		/*
		 * Print the results
		 */
		final String listLogPath2 = StaticMethods.listGeomPath(logPath2);// create a printable listing of the path
		System.out
				.format("expected result: x values are unchanaged, y values are either%nlogarithms of original values,"
						+ " or NaN if the original y<=0.%n%s%n", listLogPath2);// print the listing

		System.out.println();
		System.out.println(StaticMethods.horizontalLine(32));
		System.out.println();

	}

}
