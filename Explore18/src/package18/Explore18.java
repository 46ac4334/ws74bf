package package18;


	import javax.swing.JOptionPane;

import org.apache.commons.math3.linear.ArrayRealVector;

	/**
	 * @author bakis
	 *
	 */
	public class Explore18 implements Runnable {

		/**
		 * @param args
		 */
		public static void main(String[] args) {
			Explore18 app = new Explore18();
			new Thread(app).start();
		}

		/**
		 * constructor
		 */
		public Explore18() {
		}

		
		@Override
		public void run() {
			JOptionPane.showMessageDialog(null, "Hello, World!  Hello");
			ArrayRealVector arrayRealVector = new ArrayRealVector(7);
			System.out.println(arrayRealVector);
		}

	}


