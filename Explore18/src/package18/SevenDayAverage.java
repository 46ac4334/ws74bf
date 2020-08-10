/**
 *
 */
package package18;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sandbox.RateHypothesizer;

/**
 * @author bakis
 *
 */
public class SevenDayAverage implements RateHypothesizer {

	/**
	 * constructor
	 */
	public SevenDayAverage() {
	}

	@Override
	public List<Double> apply(final List<Integer> t) {
		final List<Double> result = new ArrayList<>();
		Double next = t.get(0).doubleValue();// First element in input list
		final List<Double> shiftReg = new ArrayList<>(Arrays.asList(next, next, next));
		var sum = 3 * next;
		final var iterator = t.iterator();

		while (iterator.hasNext()) {
			next = iterator.next().doubleValue();
			sum += next;
			shiftReg.add(0, next);

			while (shiftReg.size() > 7) {
				sum -= shiftReg.remove(7);
			}
			result.add(sum / 7.);
		}
		for (var i = 0; i < 3; ++i) {

			sum += next;
			shiftReg.add(0, next);

			while (shiftReg.size() > 7) {
				sum -= shiftReg.remove(7);
			}
			result.add(sum / 7.);
		}
		if (result.size() > 0) {
			result.remove(0);
		}
		if (result.size() > 0) {
			result.remove(0);
		}
		if (result.size() > 0) {
			result.remove(0);
		}

		return result;
	}

}
