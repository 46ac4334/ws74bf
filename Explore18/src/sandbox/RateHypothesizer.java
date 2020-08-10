/**
 *
 */
package sandbox;

import java.util.List;
import java.util.function.Function;

/**
 * Given a list of observed counts, hypothesize underlying expected rates. The
 * counts might, for example, be observed counts of a Poisson process, and the
 * hypothesized rate might be the intensity of that process, sometimes denoted
 * by λ.
 *
 * @author bakis
 *
 */
public interface RateHypothesizer extends Function<List<Integer>, List<Double>> {

}
