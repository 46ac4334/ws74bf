/**
 *
 */
package sandbox;

/**
 * Demonstrate formating of <code>NaN</code> and <code>null</code> values of
 * <code>Number</code> type.
 *
 * @author bakis
 *
 */
public class TestNaN implements Runnable {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Thread(new TestNaN()).start();
	}

	/**
	 *
	 */
	public TestNaN() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		final double d = Double.NaN;
		final Number n = d;
		final Number m = null;
		System.out.format("%n1) %30s: d = %.3g, n = %.3g%n", "NaN Number formated as %.3g", d, n);
		System.out.format("%n2) %30s: b = %d%n", "NaN Number.byteValue as %d", n.byteValue());
		System.out.format("%n3) %30s: f = %.3g%n", "NaN Number.floatValue as %.3g", n.floatValue());
		System.out.format("%n4) %30s: i = %d%n", "NaN Number.intValue as %d", n.intValue());
		System.out.format("%n5) %30s: n = %s%n", "NaN Number.toString as %s", n.toString());
		System.out.format("%n6) %30s: m = %.3g%n", "null Number as %.3g", m);

		try {
			System.out.format("%n7) %30s: m = ", "null Number toString()");
			System.out.format("%s%n", m.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.format("%n8) %30s: m = ", "null Number doubleValue()");
			System.out.format("%.3g%n", m.doubleValue());
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
