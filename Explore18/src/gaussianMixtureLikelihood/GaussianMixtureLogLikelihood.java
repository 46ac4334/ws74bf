/**
 *
 */
package gaussianMixtureLikelihood;

import java.util.function.Function;

/**
 * <p>
 * Calculates log likelihood and derivative of a mixture of Gaussians in one
 * variable.
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 *
 * <math xmlns="http://www.w3.org/1998/Math/MathML"> <semantics> <mrow>
 * <mi>f</mi> <mrow> <mrow> <mo stretchy="false">(</mo> <mrow> <mi>x</mi>
 * </mrow> <mo stretchy="false">)</mo> </mrow> <mo stretchy="false">=</mo>
 * <mi>ln</mi> </mrow> <mfenced open="(" close=")"> <mrow> <mfrac> <mrow>
 * <mn>1</mn> </mrow> <mrow> <msqrt> <mrow> <mn>2</mn>
 * <mo stretchy="false">π</mo> </mrow> </msqrt> </mrow> </mfrac> <mrow>
 * <munderover> <mo stretchy="false">∑</mo> <mrow> <mrow> <mi>i</mi>
 * <mo stretchy="false">=</mo> <mn>0</mn> </mrow> </mrow> <mrow> <mrow>
 * <mi>n</mi> <mo stretchy="false">−</mo> <mn>1</mn> </mrow> </mrow>
 * </munderover> <mrow> <mfrac> <mrow> <msub> <mi>w</mi> <mi>i</mi> </msub>
 * </mrow> <mrow> <msub> <mo stretchy="false">σ</mo> <mi>i</mi> </msub> </mrow>
 * </mfrac> <msup> <mi>e</mi> <mrow> <mo stretchy="false">−</mo> <mrow> <mfrac>
 * <mrow> <mn>1</mn> </mrow> <mrow> <mn>2</mn> </mrow> </mfrac> <msup>
 * <mfenced open="(" close=")"> <mrow> <mfrac> <mrow> <mrow> <mi>x</mi>
 * <mo stretchy="false">−</mo> <msub> <mo stretchy="false">μ</mo> <mi>i</mi>
 * </msub> </mrow> </mrow> <mrow> <msub> <mo stretchy="false">σ</mo> <mi>i</mi>
 * </msub> </mrow> </mfrac> </mrow> </mfenced> <mn>2</mn> </msup> </mrow>
 * </mrow> </msup> </mrow> </mrow> </mrow> </mfenced> </mrow>
 * <annotation encoding="StarMath 5.0">f(x) = ln left({1}over{sqrt{2%pi}} SUM
 * from{i=0}to{n-1}{{w_i}over{%sigma_i}e^-{{1}over{2} left(
 * {x-%mu_i}over{%sigma_i} right) ^2} } right)</annotation> </semantics> </math>
 * </p>
 * <p>
 * This function returns its result in a <code>double[2]</code> instance, where
 * the first element is the logarithm of the probability density, and the second
 * is the derivative of the function value w.r.t. the argument.
 * </p>
 *
 * @author bakis
 *
 */
public class GaussianMixtureLogLikelihood implements Function<Double, double[]> {

	/**
	 * Number of components
	 */
	private final int componentCount;

	private final double[] means;

	private final double[] sigmas;

	private final double[] weights;

	/**
	 * Constructor.
	 *
	 * @param params element <code>[i][0</code>] must contain the mean,
	 *               <code>[i][1]</code> the sigma, and <code>[i][2]</code> the
	 *               weight for the <code>i</code>-th mixture component. Does not
	 *               force the sum of weights to be unity, but all weights must be
	 *               non-negative.
	 */
	public GaussianMixtureLogLikelihood(final double[][] params) {
		if (params == null) {
			throw new IllegalArgumentException("argument is null");
		}
		if (params.length < 1) {
			throw new IllegalArgumentException("argument array is empty");
		}

		this.componentCount = params.length;

		this.means = new double[this.componentCount];
		this.sigmas = new double[this.componentCount];
		this.weights = new double[this.componentCount];

		for (var i = 0; i < this.componentCount; ++i) {
			if (params[i] == null) {
				throw new IllegalArgumentException(String.format("row %,d of argument is null", i));
			}
			if (params[i].length == 3) {
				this.means[i] = params[i][0];
				this.sigmas[i] = params[i][1];
				if (params[i][2] < 0) {
					throw new IllegalArgumentException(
							String.format("params[%,d][2] = %.3g, should be non-negative", i, params[i][2]));
				}
				this.weights[i] = params[i][2];
			} else {
				throw new IllegalArgumentException(
						String.format("row %,d of argument has length %,d, should be 3", params[i].length));
			}

		}

	}

	/**
	 * @param x The argument for the Gaussian mixture.
	 * @return array of two elements: the logarithm of the Gassian-mixture density
	 *         function, and the derivative of that function w.r.t. the argument x.
	 */
	@Override
	public double[] apply(final Double x) {
		if (!Double.isFinite(x)) {
			throw new IllegalArgumentException(String.format("argument is %.3g", x));
		}
		var sum = 0D;
		for (var i = 0; i < this.componentCount; ++i) { // . . . . . . . Step 1 forward
			final var t = (x - this.means[i]) / this.sigmas[i]; // . . . Step 2 forward
			final var exponent = -t * t / 2; // . . . . . . . . . . . . .Step 3 forward
			final var exp = Math.exp(exponent); // . . . . . . . . . . . Step 4 forward
			sum += this.weights[i] * exp / this.sigmas[i]; // . . . . . .Step 5 forward
		} // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . Step 6 forward
		final var f = sum / Math.sqrt(2. * Math.PI); // . . . . . . . . .Step 7 forward
		final var y = Math.log(f); // . . . . . . . . . . . . . . . . . .Step 8 forward

		final var y_ = 1.; // . . . . . . . . . . . . . . . . . . . . . .Step 9 reverse
		// In a network, this might be the derivative of the overall objective w.r.t.
		// the output of this node. Here it is set to unity.

		final var a_ = y_ / f; // . . . . . . . . . . . . . . . . . . . .Step 8 reverse
		final var sum_ = a_ / Math.sqrt(2. * Math.PI); // . . . . . . . .Step 7 reverse
		var x_ = 0D;
		for (var i = this.componentCount - 1; i >= 0; --i) { // . . . . .Step 6 reverse

			/*
			 * There is a trade-off between compute cycles and storage: The following three
			 * statements, which re-compute the values of "t", "exponent", and "exp", become
			 * unnecessary if a these values are stored during the forward pass for every
			 * needed value of "i".
			 */
			final var t = (x - this.means[i]) / this.sigmas[i]; // . . . Step 2 dup
			final var exponent = -t * t / 2; // . . . . . . . . . . . . .Step 3 dup
			final var exp = Math.exp(exponent); // . . . . . . . . . . . Step 4 dup

			final var exp_ = sum_ * this.weights[i] / this.sigmas[i]; // Step 5 reverse
			final var exponent_ = exp_ * exp; // . . . . . . . . . . . . Step 4 reverse
			final var t_ = -exponent_ * t; // . . . . . . . . . . . . . .Step 3 reverse
			x_ += t_ / this.sigmas[i]; // . . . . . . . . . . . . . . . .Step 2 reverse
		} // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . Step 1 reverse

		return new double[] { y, x_ };
	}

}
