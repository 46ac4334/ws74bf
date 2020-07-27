package package18;


	import javax.swing.JOptionPane;

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
		 * 
		 */
		public Explore18() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			JOptionPane.showMessageDialog(null, "Hello, World!");
		}

	}