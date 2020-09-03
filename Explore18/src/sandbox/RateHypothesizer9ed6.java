package sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bakis
 *
 */
public class RateHypothesizer9ed6 implements RateHypothesizer {

	/**
	 * Parameters. For example, learning rate.
	 */
	private final Map<String, Object> theta;

	/**
	 * Note that the values may be numeric, for example <code>Double</code>, but
	 * they may be functions and other objects.
	 *
	 * @param theta Parameters, for example LearningRate.
	 */
	public RateHypothesizer9ed6(final Map<String, Object> theta) {
		this.theta = new HashMap<>(theta);
	}

	/**
	 * @param observedCounts A sequence of observed integer-valued quantities.
	 */
	@Override
	public List<Double> apply(final List<Number> observedCounts) {
		final List<Double> hyp = new ArrayList<>();

		/*
		 * Initialize the hypothesis
		 */
		for (final Number i : observedCounts) {
			final Double d = i.doubleValue();
			hyp.add(d);
		}

		final List<Double> hyp_ = new ArrayList<>();
		final double f = this.calcFG(observedCounts, hyp, hyp_, this.theta);

		return null;
	}

	/**
	 * @param observedCounts INPUT: The observed sequence for which the underlying
	 *                       rate is to be estimated.
	 * @param hyp            INPUT: The hypothesized underlying rate.
	 * @param hyp_           OUTPUT: The derivative of the objective w.r.t. the
	 *                       hypothesized rate.
	 * @param theta2         INPUT: Parameters of the objective function.
	 * @return the objective function value.
	 */
	private double calcFG(final List<Number> observedCounts, final List<Double> hyp, final List<Double> hyp_,
			final Map<String, Object> theta2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
