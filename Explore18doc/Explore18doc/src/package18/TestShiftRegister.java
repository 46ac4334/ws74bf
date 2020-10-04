/**
 *
 */
package package18;

/**
 * @author bakis
 *
 */
public class TestShiftRegister implements Runnable {

	/**
	 * @param args Command line arguments, not used in this application
	 */
	public static void main(final String[] args) {
		final TestShiftRegister testShiftRegister = new TestShiftRegister();
		final Thread thread = new Thread(testShiftRegister);
		thread.start();
	}

	/**
	 * default constructor
	 */
	public TestShiftRegister() {
	}

	@Override
	public void run() {
		final int limit = 7;
		System.out.format("%n%s%,d%n", "Instantiating with limit = ", limit);
		final ShiftRegister testee = new ShiftRegister(limit);
		System.out.format("%n%s%n", "Testing toString():");
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

		System.out.println("Testing push(1.0):");
		testee.push(1.0);
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

		System.out.println("Testing push(NaN):");
		testee.push(Double.NaN);
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

		System.out.println("Testing push(.7):");
		testee.push(.7);
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

		System.out.println("Testing push(5):");
		testee.push(5);
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

		System.out.println("Testing push(NaN):");
		testee.push(Double.NaN);
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

		System.out.println("Testing push(2.3):");
		testee.push(2.3);
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

		System.out.println("Testing push(.1):");
		testee.push(.1);
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

		System.out.println("Testing push(-.02):");
		testee.push(-.02);
		System.out.print(testee.toString());
		System.out.println(" *** End of toString() ***");
		System.out.println();

	}

}
