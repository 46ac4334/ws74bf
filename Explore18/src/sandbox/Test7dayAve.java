/**
 *
 */
package sandbox;

import java.util.Arrays;
import java.util.List;

import package18.SevenDayAverage;

/**
 * @author bakis
 *
 */
public class Test7dayAve implements Runnable {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Thread(new Test7dayAve()).run();
	}

	/**
	 * constructor
	 */
	public Test7dayAve() {
	}

	@Override
	public void run() {
		final var inArray = new Integer[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1 };
		final SevenDayAverage sevenDayAverage = new SevenDayAverage();
		sevenDayAverage.setDebug(true);

		final List<Number> in = Arrays.asList(inArray);
		System.out.format(" Input array size: %,d%n", in.size());
		final var out = sevenDayAverage.apply(in);
		System.out.format("output array size: %,d%n", out.size());

		System.out.format("%7s %7s %7s%n", "i", "in[i]", "out[i]");

		for (var i = 0; i < in.size() && i < out.size(); ++i) {
			System.out.format("%,7d %,7d %7.3g%n", i, in.get(i), out.get(i));
		}

	}

}
