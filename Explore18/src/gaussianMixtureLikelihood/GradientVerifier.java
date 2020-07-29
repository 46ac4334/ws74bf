/**
 *
 */
package gaussianMixtureLikelihood;

import java.util.Random;

import javax.swing.JOptionPane;

/**
 * Invokes {@link GaussianMixtureLogLikelihood} with random arguments to test
 * the accuracy of the derivative from that module.
 *
 * @author bakis
 *
 */
public class GradientVerifier implements Runnable {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final var gradientVerifier = new GradientVerifier();
		final var thread = new Thread(gradientVerifier);
		thread.start();

	}

	private final double errorLimit;
	private final Random random = new Random();

	/**
	 * Default constructor
	 */
	public GradientVerifier() {
		this.errorLimit = 1e-6;
	}

	@Override
	public void run() {
		final var delta = 1e-2;
		var componentCount = 1;
		final var mu = 0;
		final var sigma = 1D;
		final var weight = 1D;
		final var params = new double[componentCount][3];
		params[0][0] = mu;
		params[0][1] = sigma;
		params[0][2] = weight;
		final var likelihood = new GaussianMixtureLogLikelihood(params);

		System.out.format("%8s, %8s, %8s%n", "x", "y", "y'");
		var yOld = Double.NaN;
		for (var i = -20; i <= 20; ++i) {
			final var x = 0.1 * i;
			final var y = likelihood.apply(x);
			final var d = i == -20 ? Double.NaN : (y[0] - yOld) / 0.1;
			yOld = y[0];
			System.out.format("%8.3g, %8.3g, %8.3g, %8.3g, %n", x, y[0], y[1], d);
		}

		final var iterMax = 7;
		var maxErrMagn = Double.NEGATIVE_INFINITY;
		for (var iter = 0; iter < iterMax; ++iter) {
			final var maxCompCount = 8;
			componentCount = this.random.nextInt(maxCompCount) + 1;// Use 1 to 8 components.
			System.out.format("Using %,d mixture component(s).  ", componentCount);

			final var errorMagnitude = this.test1(componentCount, delta);
			maxErrMagn = Math.max(maxErrMagn, errorMagnitude);
		}
		final var fail = maxErrMagn > this.errorLimit;
		final var msg = fail ? "FAIL" : "PASS";
		System.out.format("%s:  Maximum error magnitude %.3g%n", msg, maxErrMagn);

		JOptionPane.showMessageDialog(null, msg + String.format(":  max err = %.3g", maxErrMagn), "Gradient Test Result",
				fail ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

	}

	private double test1(final int componentCount, final double delta) {
		final var params = new double[componentCount][3];
		for (var i = 0; i < componentCount; ++i) {
			final var mu = this.random.nextGaussian() * .1;
			final var r = this.random.nextGaussian();
			final var sigma = 1 + 1e-2 * (r * r);
			final var weight = Math.abs(this.random.nextGaussian());
			params[i][0] = mu;
			params[i][1] = sigma;
			params[i][2] = weight;
		}

		final var likelihood = new GaussianMixtureLogLikelihood(params);

		final var x0 = this.random.nextGaussian();
		final var x1 = x0 + delta;

		final var result0 = likelihood.apply(x0);
		final var result1 = likelihood.apply(x1);

		final var averagePredictedSlope = (result0[1] + result1[1]) / 2;
		System.out.format("averagePredictedSlope = %8.3g  ", averagePredictedSlope);
		final var averageActualSlope = (result1[0] - result0[0]) / delta;
		System.out.format("averageActualSlope = (%8.3g - %8.3g) / %8.3g = %8.3g  ", result1[0], result0[0], delta,
				averageActualSlope);
		final var ratio = averagePredictedSlope / averageActualSlope;
		final var d = ratio - 1;
		final var abs = Math.abs(d);
		System.out.println(String.format("ratio = 1 %s %8.3g", d < 0 ? "-" : "+", abs));
		return abs;
	}

}
