package package18;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A shift register of a pre-defined length. Supports missing data.
 *
 * @author bakis
 *
 */
public class ShiftRegister extends ArrayList<Number> {

	private static final long serialVersionUID = 1L;

	private boolean debug = false;

	/**
	 * The total number of elements pushed to this array, including <code>NaN</code>
	 * values.
	 *
	 * @see #processedCount
	 */
	private int grandCount = 0;

	private ArrayList<Number> history;

	/**
	 * Maximum length of the array. If more than <code>limit</code> values are
	 * pushed into the array, the oldest values are automatically discarded.
	 */
	private final int limit;

	/**
	 * The average of the non-<code>NaN</code> values in the array, or 0 if there
	 * are no such values.
	 */
	private double mean;

	/**
	 * The current count of non-<code>NaN</code> values in the array.
	 */
	private int n;

	/**
	 * Total count of non-<code>NaN</code> elements processed so far. This can be
	 * larger than the current length of the array, because as new elements are
	 * added to the array, old elements are shifted out to keep the length of the
	 * array no greater than {@link #limit}. Use {@link #size()} to get the current
	 * length of the array.
	 *
	 * @see #grandCount
	 *
	 */
	private int processedCount = 0;

	/**
	 * Sum of the current elements present in this array. Elements with value
	 * <code>NaN</code> are considered missing and are ignored in the computation of
	 * this sum.
	 */
	private double sum = 0;

	/**
	 * Constructor, given the limiting length of the new array.
	 *
	 * @param limit The maximum length of this array.
	 */
	public ShiftRegister(final int limit) {
		super(limit + 1);
		this.limit = limit;
	}

	/**
	 * Constructor, given the limiting length of the new array.
	 *
	 * @param limit The maximum length of this array.
	 */
	public ShiftRegister(final int limit, final boolean debug) {
		super(limit + 1);
		this.debug = debug;
		this.limit = limit;
		if (debug) {
			this.history = new ArrayList<>();
		}
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
		return this.processedCount;
	}

	/**
	 * Get he total count of elements pushed to this array, including
	 * <code>NaN</code> values, and including values that have already been shifted
	 * out.
	 *
	 *
	 * @return the grandCount
	 */
	public int getGrandCount() {
		return this.grandCount;
	}

	/**
	 * @return unmodifyable copy of the history
	 */
	public List<Number> getHistory() {
		return this.history == null ? null : (List<Number>) List.copyOf(this.history);
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
	 * @return the debug
	 */
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * <p>
	 * Appends an element to the start of the array. If the resulting array would be
	 * longer than {@link #limit}, removes enough elements from the end of the array
	 * to reduce the length to <code>limit</code>.
	 * </p>
	 * <p>
	 * If the value of the new element is <code>NaN</code>, it is ignored in the
	 * calculations of the sum and the mean of the elements, but is counted when
	 * determining the current length of the array.
	 * </p>
	 * <p>
	 * Recomputes the sum.
	 * </p>
	 *
	 * @param x The element to be added.
	 */
	public void push(final Number x) {
		if (this.debug) {
			if (this.history == null) {
				this.history = new ArrayList<>();
			}
			this.history.add(x);
		}
		this.add(0, x);
		final double doubleValue = x.doubleValue();
		++this.grandCount;
		if (!Double.isNaN(doubleValue)) {
			++this.processedCount;
		}

		while (this.size() > this.limit) {
			this.remove(this.size() - 1);
		}

		this.sum = 0;
		this.n = 0;
		final Iterator<Number> iterator = this.iterator();
		while (iterator.hasNext()) {
			final double doubleValue2 = iterator.next().doubleValue();
			if (!Double.isNaN(doubleValue2)) {
				this.sum += doubleValue2;
				++this.n;
			}
		}

		this.mean = this.n == 0 ? 0 : this.sum / this.n;

	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(final boolean debug) {
		this.debug = debug;
	}

	@Override
	public String toString() {
		final StringWriter sw = new StringWriter();
		final PrintWriter out = new PrintWriter(sw);
		out.format("%15s = %,d%n", "processedCount", this.processedCount);
		out.format("%15s = %,d%n", "grandCount", this.grandCount);
		out.format("%15s = %,d%n", "limit", this.limit);
		out.format("%15s = %.3g%n", "mean", this.mean);
		out.format("%15s = %,d%n", "n", this.n);
		out.format("%15s = %,d%n", "size", this.size());
		out.format("%15s = %.3g%n", "sum", this.sum);
		out.format("%s%n", super.toString());
		out.close();
		return sw.toString();
	}

}