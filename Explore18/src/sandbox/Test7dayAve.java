/**
 *
 */
package sandbox;

import java.util.Arrays;

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
		final var inArray = new Integer[] {  1,0,0,0,0,0,0,0,0,0,0,1 };
		final var sevenDayAverage = new SevenDayAverage();
		final var in = Arrays.asList(inArray);
		final var out = sevenDayAverage.apply(in);
		for (var i = 0; i < in.size() && i < out.size(); ++i) {
			System.out.format("%,7d %7.3g%n", in.get(i), out.get(i));
		}

	}

}
