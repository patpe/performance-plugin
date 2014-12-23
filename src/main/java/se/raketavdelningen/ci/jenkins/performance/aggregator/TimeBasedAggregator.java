package se.raketavdelningen.ci.jenkins.performance.aggregator;

import java.util.List;

import se.raketavdelningen.ci.jenkins.performance.exception.PerformanceReportException;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedPerformanceSample;
import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;
import hudson.model.Descriptor;

/**
 * Aggregator that aggregates samples using time as the unit.
 *
 */
public class TimeBasedAggregator extends Aggregator {

	private int minutesPerAggregation = 1;

	private long nextTimestamp = -1;

	@Override
	public boolean isSampleInCurrentAggregation(PerformanceSample sample) {
		return sample.getTimestamp() <= nextTimestamp;
	}

	@Override
	public AggregatedPerformanceSample aggregatePerformanceSamples(
			List<PerformanceSample> samples, String key) {
		long aggregatedTimestamp = calculateAggregatedTimestamp(samples);
		long samplesCounter = 0;
		long sumOfElapsed = 0;
		boolean success = true;

		long max = Long.MIN_VALUE;
		long min = Long.MAX_VALUE;
		for (PerformanceSample sample : samples) {
			long elapsed = sample.getElapsed();
			if (max < elapsed) {
				max = elapsed;
			}
			if (elapsed < min) {
				min = elapsed;
			}

			sumOfElapsed+=sample.getElapsed();
			success = (success && sample.isSuccess());
			samplesCounter++;
		}

		long averageElapsed = sumOfElapsed / samplesCounter;

		return new AggregatedPerformanceSample(aggregatedTimestamp, max, min, averageElapsed, 
				success, samplesCounter, key);
	}

	private long calculateAggregatedTimestamp(List<PerformanceSample> samples) {
		if (samples.size() > 1) {
			long firstTimestamp = samples.get(0).getTimestamp();
			long lastTimestamp = samples.get(samples.size() - 1).getTimestamp();
			return (lastTimestamp + firstTimestamp) / 2;
		} else if (samples.size() == 1) {
			return samples.get(0).getTimestamp();
		} else {
			throw new PerformanceReportException("No samples given to analyze");
		}
	}

	@Override
	public void startNewAggregationPeriod() {
		this.nextTimestamp = calculateNextTimestamp(nextTimestamp);
	}

	@Override
	public void initializeAggregatorFromFirstSample(PerformanceSample firstSample) {
		this.nextTimestamp = calculateNextTimestamp(firstSample.getTimestamp());
	}

	@Override
	public Descriptor<Aggregator> getDescriptor() {
		return new Descriptor<Aggregator>() {    

			@Override
			public String getDisplayName() {
				return "Time based aggregator (1 min interval)";
			}
		};
	}

	private long calculateNextTimestamp(long currentNextTimestamp) {
		return currentNextTimestamp + (minutesPerAggregation * 60 * 1000);
	}
}
