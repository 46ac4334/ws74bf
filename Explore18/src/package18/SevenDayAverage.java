/**
 *
 */
package package18;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import sandbox.RateHypothesizer;

/**
 * @author bakis
 *
 */
public class SevenDayAverage implements RateHypothesizer {

	public enum EndPad {
		/**
		 * Repeat part of the last seven-day period
		 */
		CYCLE,

		/**
		 * Pad at the end with the average of the last seven (or all, if series is
		 * shorter than 7) values.
		 */
		LAST_AVERAGE,

		/**
		 * Pad at the end with the last observed value
		 */
		LAST_VALUE,

		/**
		 * Pad at the end with <code>NaN</code>s
		 */
		NAN,

		/**
		 * Symmetric around current point
		 */
		SYMM,

		/**
		 * Pad at the end with zeros
		 */
		ZERO
	}

	public enum StartPad {

		/**
		 * Repeat part of the first seven-day period
		 */
		CYCLE,

		/**
		 * Pad at the start with the average of the first seven (or all, if series is
		 * shorter than 7) values.
		 */
		FIRST_AVERAGE,

		/**
		 * Pad at the start with the first observed value
		 */
		FIRST_VALUE,

		/**
		 * Pad at the start with <code>NaN</code>s.
		 */
		NAN,

		/**
		 * Pad at the start with zeros.
		 */
		ZERO;
	}

	private boolean debug = false;

	/**
	 * Type of padding to use at the start of the series.
	 */
	private final EndPad endPad;

	/**
	 * Type of padding to use at the end of the series.
	 */
	private final StartPad startPad;

	/**
	 * Constructor with default padding: zero at start, average of last 7 days at
	 * end.
	 */
	public SevenDayAverage() {
		this(StartPad.ZERO, EndPad.LAST_AVERAGE);
	}

	/**
	 * Constructor with specified padding at start and end.
	 *
	 * @param startPad Type of padding at start.
	 * @param endPad   Type of padding at end.
	 */
	public SevenDayAverage(final StartPad startPad, final EndPad endPad) {
		this.startPad = startPad;
		this.endPad = endPad;
	}

	/**
	 * @param source the list of values which are to be averaged
	 * @return A list of values of the same length as the input list, but each value
	 *         representing the average of seven input values: three before the
	 *         current one, the current input value, and three succeeding values.
	 *         The three values each before the start and after the end of the input
	 *         list are computed according to the given <code>StartPad</code> and
	 *         <code>EndPad</code> specifications.
	 */
	@Override
	public List<Double> apply(final List<Number> source) {

		if (source == null) {
			return null;
		}

		final List<Double> result = new ArrayList<>();

		final double[] startPadValues = new double[3];
		Arrays.fill(startPadValues, Double.NaN);// Default to NaN if subsequent code fails to specify anything else.
		double[] endPadValues = new double[3];
		Arrays.fill(endPadValues, Double.NaN);// Default to NaN if subsequent code fails to specify anything else.
		if (this.debug) {
			System.out.format("debugging%n");
			System.out.println("source =" + source.toString());
			System.out.println(StaticMethods.stacktrace(String.format("startPad = %s", this.startPad.toString())));
		}

		Double startPadValue = null;
		switch (this.startPad) {// Get the padding value for the three days preceding the given series:
		case CYCLE:
			final Iterator<Number> startPadIterator = source.iterator();
			int i = -3;
			while (i < 4) {
				final double d = startPadIterator.hasNext() ? startPadIterator.next().doubleValue() : Double.NaN;
				if (i > 0 && i <= startPadValues.length) {
					startPadValues[i - 1] = d;
				}
				++i;
			}
			System.out.println("** " + StaticMethods.simpleVectorPrint(startPadValues));
			startPadValue = null;
			break;
		case FIRST_AVERAGE:
			startPadValue = this.getFirstAverage(source, 7);
			break;
		case FIRST_VALUE:
			startPadValue = this.getFirstAverage(source, 1);
			break;
		case NAN:
			break;
		case ZERO:
			startPadValue = 0.;
			break;
		default:
			startPadValue = Double.NaN;
			break;
		}
		if (this.debug) {
			System.out.println(StaticMethods.stacktrace(String.format("startPadValue = %.3g", startPadValue)));
		}

		Double endPadValue = Double.NaN;
		if (this.debug) {
			System.out.println(StaticMethods.stacktrace(String.format("endPad = %s", this.endPad.toString())));
		}
		switch (this.endPad) {// Get the padding value for the last three days:
		case CYCLE:
			endPadValue = null;
			final ListIterator<Number> sourceListIterator = source.listIterator(source.size());
			for (int i = 0; i < 4; ++i) {// Skip the last 4 elements of source
				if (sourceListIterator.hasPrevious()) {
					sourceListIterator.previous();
				}
			}
			for (int i = 3; i > 0; --i) {// Get the three source values from the start of the last 7.
				endPadValues[i - 1] = sourceListIterator.hasPrevious() ? sourceListIterator.previous().doubleValue()
						: Double.NaN;
			}
			break;
		case LAST_AVERAGE:
			endPadValue = this.getLastAverage(source, 7);
			break;
		case LAST_VALUE:
			endPadValue = this.getLastAverage(source, 1);
			break;
		case NAN:
			break;
		case SYMM:
			endPadValue = null;
			endPadValues = null;
			break;
		case ZERO:
			endPadValue = 0.;
			break;
		default:
			endPadValue = Double.NaN;
			break;
		}
		if (this.debug) {
			System.out.println(StaticMethods.stacktrace(String.format("endPadValue = %.3g", endPadValue)));
		}

		final var iterator = source.iterator();
		final ShiftRegister shiftReg = new ShiftRegister(7);

		if (this.debug) {
			System.out.format("shiftReg: %s", shiftReg.toString());
		}

		/*
		 * prime the shift register with startPadValue
		 */
		for (int i = 0; i < 3; ++i) {
			shiftReg.push(startPadValue == null ? startPadValues[i] : startPadValue);
		}
		int j = -3;
		while (iterator.hasNext()) {
			if (this.debug) {
				System.out.format("j=%d%n", j);
			}
			final double doubleValue = iterator.next().doubleValue();
			shiftReg.push(doubleValue);
			if (this.debug) {
				System.out.format("push %.3g, j=%,d, sum=%.3g %s%n", doubleValue, j, shiftReg.getSum(),
						((List<Number>) shiftReg).toString());
			}
			if (this.debug) {
				System.out.format("shiftReg:%n%s%n", shiftReg.toString());
			}
			if (j >= 0) {
				result.add(shiftReg.getMean());
			}
			if (this.debug) {
				System.out.format("result: %s%n", result);
			}
			++j;
		}

		int i = 0;
		while (result.size() < source.size()) {
			if (endPadValue == null) {
				if (endPadValues == null) {
					for (int i1 = 0; i1 < 2; ++i1) {
						shiftReg.push(Double.NaN);
					}
				} else {
					shiftReg.push(endPadValues[i]);
				}
				result.add(shiftReg.getMean());
			} else {
				shiftReg.push(endPadValue);
				result.add(shiftReg.getMean());
			}
			++i;
		}

		return result;
	}

	/**
	 * @return the endPad
	 */
	public EndPad getEndPad() {
		return this.endPad;
	}

	/**
	 * @param source
	 * @param n
	 */
	private double getFirstAverage(final List<Number> source, final int n) {
		double startPadValue;
		if (source != null) {
			final Iterator<Number> iterator2 = source.iterator();
			int count = 0;
			double sum1 = 0.;
			while (iterator2.hasNext() && count < n) {
				sum1 += iterator2.next().doubleValue();
				++count;
			}
			startPadValue = count > 0 ? sum1 / count : 0;
		} else {
			startPadValue = Double.NaN;
		}
		return startPadValue;
	}

	/**
	 * @param source
	 * @param n
	 */
	private double getLastAverage(final List<Number> source, final int n) {
		double endPadValue;
		if (source != null) {
			final ListIterator<Number> iterator2 = source.listIterator(source.size());
			int count = 0;
			double sum1 = 0.;
			while (iterator2.hasPrevious() && count < n) {
				sum1 += iterator2.previous().doubleValue();
				++count;
			}
			endPadValue = count > 0 ? sum1 / count : 0;
		} else {
			endPadValue = Double.NaN;
		}
		return endPadValue;
	}

	/**
	 * @return the startPad
	 */
	public StartPad getStartPad() {
		return this.startPad;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(final boolean debug) {
		this.debug = debug;
	}

}
