/**
 *
 */
package package18;

import java.io.PrintWriter;
import java.io.StringWriter;
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
		 * Pad at the end with the average of the last seven (or all) values.
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
		 * Pad at the end with zeros
		 */
		ZERO
	}

	public class ShiftRegister extends ArrayList<Number> {
		private static final long serialVersionUID = 1L;
		private int count = 0;
		private final int limit;
		private double sum = 0;

		public ShiftRegister(final int limit) {
			super(limit);
			this.limit = limit;
			if (SevenDayAverage.this.debug) {
				System.out.println(StaticMethods.stacktrace(
						String.format("%s instantiated, with limit = %,d.%n", this.getClass().getSimpleName(), limit)));
			}
		}

		/**
		 * @return the count
		 */
		public int getCount() {
			return this.count;
		}

		/**
		 * @return the sum
		 */
		public double getSum() {
			return this.sum;
		}

		public void push(final Number x) {
			this.add(0, x);
			final double doubleValue = x.doubleValue();
			if (Double.isFinite(doubleValue)) {
				++this.count;
				this.sum += doubleValue;
			}

			while (this.size() > this.limit) {
				final double doubleValue2 = this.remove(this.size() - 1).doubleValue();
				if (Double.isFinite(doubleValue2)) {
					this.sum -= doubleValue2;
				}
			}

		}

		/**
		 * @param sum the sum to set
		 */
		public void setSum(final double sum) {
			this.sum = sum;
		}

		@Override
		public String toString() {
			final StringWriter sw = new StringWriter();
			final PrintWriter out = new PrintWriter(sw);
			out.format("count = %,d%n", this.count);
			out.format("limit = %,d%n", this.limit);
			out.format(" size = %,d%n", this.size());
			out.format("%s%n", super.toString());
			out.close();
			return sw.toString();
		}

	}

	public enum StartPad {

		/**
		 * Repeat part of the first seven-day period
		 */
		CYCLE,

		/**
		 * Pad at the start with the average of the first seven (or all) values.
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

	@Override
	public List<Double> apply(final List<Number> source) {

		final List<Double> result = source == null ? null : new ArrayList<>();

		double startPadValue = Double.NaN;
		final double[] startPadValues = new double[3];
		Arrays.fill(startPadValues, Double.NaN);
		if (this.debug) {
			System.out.format("debugging%n");
			System.out.println("source =" + source.toString());
			System.out.println(StaticMethods.stacktrace(String.format("startPad = %s", this.startPad.toString())));
		}

		switch (this.startPad) {// Get the padding value for the first three days:
		case CYCLE:
			final Iterator<Number> startPadIterator = source.iterator();
			int i = -3;
			while (startPadIterator.hasNext() && i < 7) {
				if (i >= 0 && i < 3 && i < startPadValues.length) {
					startPadValues[i] = startPadIterator.next().doubleValue();
				}
				++i;
			}
			break;
		case FIRST_AVERAGE:
			startPadValue = this.getFirstAverage(source, 3);
			break;
		case FIRST_VALUE:
			startPadValue = this.getFirstAverage(source, 1);
			break;
		case ZERO:
			startPadValue = 0;
			break;
		default:
			startPadValue = Double.NaN;
			break;
		}
		if (this.debug) {
			System.out.println(StaticMethods.stacktrace(String.format("startPadValue = %.3g", startPadValue)));
		}

		double endPadValue = Double.NaN;
		if (this.debug) {
			System.out.println(StaticMethods.stacktrace(String.format("endPad = %s", this.endPad.toString())));
		}
		switch (this.endPad) {// Get the padding value for the last three days:
		case LAST_AVERAGE:
			endPadValue = this.getLastAverage(source, 3);
			break;
		case LAST_VALUE:
			endPadValue = this.getLastAverage(source, 1);
			break;
		case ZERO:
			endPadValue = 0;
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
			shiftReg.push(startPadValue);
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
			System.out.format("shiftReg:%n%s%n", shiftReg.toString());
			if (j >= 0) {
				result.add(shiftReg.getSum() / 7.);
			}
			if (this.debug) {
				System.out.format("result: %s%n", result);
			}
			++j;
		}

		while (result.size() < source.size()) {
			shiftReg.push(endPadValue);
			result.add(shiftReg.getSum() / 7.);
		}

//		for (var i = 0; i < 3; ++i) {
//
//			sum += next;
//			shiftReg.add(0, next);
//
//			while (shiftReg.size() > 7) {
//				sum -= shiftReg.remove(7);
//			}
//			result.add(sum / 7.);
//		}
//		if (result.size() > 0) {
//			result.remove(0);
//		}
//		if (result.size() > 0) {
//			result.remove(0);
//		}
//		if (result.size() > 0) {
//			result.remove(0);
//		}

		return result;
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
