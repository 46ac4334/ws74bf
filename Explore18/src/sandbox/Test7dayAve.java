/**
 *
 */
package sandbox;

import java.util.Arrays;
import java.util.List;

import package18.SevenDayAverage;
import package18.SevenDayAverage.EndPad;
import package18.SevenDayAverage.StartPad;

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

		for (final EndPad endPad : Arrays.asList(EndPad.CYCLE, EndPad.LAST_AVERAGE, EndPad.LAST_VALUE, EndPad.NAN,
				EndPad.SYMM, EndPad.ZERO)) {
			this.runTest(inArray, new SevenDayAverage(StartPad.ZERO, endPad));
			System.out.println();
		}

		for (final StartPad startPad : Arrays.asList(StartPad.CYCLE, StartPad.FIRST_AVERAGE, StartPad.FIRST_VALUE,
				StartPad.NAN, StartPad.ZERO)) {
			this.runTest(inArray, new SevenDayAverage(startPad, EndPad.ZERO));
			System.out.println();
		}
	}

	/**
	 * @param inArray
	 * @param sevenDayAverage
	 */
	private void runTest(final Integer[] inArray, final SevenDayAverage sevenDayAverage) {
		sevenDayAverage.setDebug(false);

		System.out.format("Start %s%n", sevenDayAverage.getStartPad());
		System.out.format("  End %s%n", sevenDayAverage.getEndPad());
		final List<Number> in = Arrays.asList(inArray);
		System.out.format(" Input array size: %,d%n", in.size());
		final var out = sevenDayAverage.apply(in);
		System.out.format("output array size: %,d%n", out.size());

		System.out.format("%7s %7s %7s%n", "i", "in[i]", "out[i]");

		for (var i = 0; i < in.size() && i < out.size(); ++i) {
			System.out.format("%,7d %,7d %7.3g%n", i, in.get(i), out.get(i));
		}
		System.out.format("sevenDayAverage.debug = %b", sevenDayAverage.isDebug());
		System.out.println();
	}

}
