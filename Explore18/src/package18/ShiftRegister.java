package package18;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class ShiftRegister extends ArrayList<Number> {

	private static final long serialVersionUID = 1L;

	/**
	 * Count of elements in this array, current length of the array.
	 */
	private int count = 0;

	/**
	 * Maximum length of the array
	 */
	private final int limit;

	private double mean;

	/**
	 * The current count of non-NaN values in the array.
	 */
	private int n;

	/**
	 * Sum of all the current elements of this array.
	 */
	private double sum = 0;

	/**
	 * Constructor, given the limiting length of the new array.
	 *
	 * @param limit The maximum length of this array.
	 */
	public ShiftRegister(final int limit) {
		super(limit);
		this.limit = limit;
	}

	/**
	 * Get the total count of elements processed so far. This can be larger than the
	 * current length of the array, because as new elements are added to the array,
	 * old elements are shifted out to keep the length of the array no greater than
	 * {@link #limit}. Use {@link #size()} to get the current length of the array.
	 *
	 * @return the count
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @return the mean
	 */
	public double getMean() {
		return this.mean;
	}

	/**
	 * @return the n
	 */
	public int getN() {
		return this.n;
	}

	/**
	 * Get the sum of the current elements of this array.
	 *
	 * @return the sum
	 */
	public double getSum() {
		return this.sum;
	}

	/**
	 * Appends an element to the start of the array. If the resulting array would be
	 * longer than {@link #limit}, removes enough elements from the end of the array
	 * to reduce the length to <code>limit</code>.
	 *
	 * Recomputes the sum.
	 *
	 * @param x The element to be added.
	 */
	public void push(final Number x) {
		this.add(0, x);
		final double doubleValue = x.doubleValue();
		if (!Double.isNaN(doubleValue)) {
			++this.count;
		}

		while (this.size() > this.limit) {
			this.remove(this.size() - 1);
		}

		this.sum = 0;
		final Iterator<Number> iterator = this.iterator();
		while (iterator.hasNext()) {
			final double doubleValue2 = iterator.next().doubleValue();
			this.n = 0;
			if (!Double.isNaN(doubleValue2)) {
				this.sum += doubleValue2;
				++this.n;
			}
			this.mean = this.sum / this.n;
		}

	}

	@Override
	public String toString() {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		out.format("count = %,d%n", this.count);
		out.format("limit = %,d%n", this.limit);
		out.format(" size = %,d%n", this.size());
		out.format("  sum = %,d%n", this.sum);
		out.format("%s%n", super.toString());
		out.close();
		return sw.toString();
	}

}