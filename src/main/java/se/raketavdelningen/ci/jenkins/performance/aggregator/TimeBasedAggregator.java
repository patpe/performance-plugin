package se.raketavdelningen.ci.jenkins.performance.aggregator;

import hudson.model.Descriptor;

import java.util.List;

import se.raketavdelningen.ci.jenkins.performance.exception.ReportException;
import se.raketavdelningen.ci.jenkins.performance.sample.AggregatedSample;
import se.raketavdelningen.ci.jenkins.performance.sample.Sample;

/**
 * Aggregator that aggregates samples using time as the unit.
 *
 */
public class TimeBasedAggregator extends Aggregator {

	private int minutesPerAggregation = 1;

	private long nextTimestamp = -1;

	@Override
	public boolean isSampleInCurrentAggregation(Sample sample) {
		return sample.getTimestamp() <= nextTimestamp;
	}

	@Override
	public AggregatedSample aggregatePerformanceSamples(
			List<Sample> samples, String key) {
		long aggregatedTimestamp = calculateAggregatedTimestamp(samples);
		long samplesCounter = 0;
		long samplesErrorCounter = 0;
		long sumOfElapsed = 0;
		boolean success = true;

		long max = Long.MIN_VALUE;
		long min = Long.MAX_VALUE;
		for (Sample sample : samples) {
			long elapsed = sample.getElapsed();
			if (max < elapsed) {
				max = elapsed;
			}
			if (elapsed < min) {
				min = elapsed;
			}

			sumOfElapsed+=sample.getElapsed();
			success = (success && sample.isSuccess());
			samplesErrorCounter = (sample.isSuccess() ? samplesErrorCounter : samplesErrorCounter + 1);
			samplesCounter++;
		}

		long averageElapsed = sumOfElapsed / samplesCounter;

		return new AggregatedSample(aggregatedTimestamp, max, min, averageElapsed, 
				success, samplesCounter, samplesErrorCounter, key);
	}

	private long calculateAggregatedTimestamp(List<Sample> samples) {
		if (samples.size() > 1) {
			long firstTimestamp = samples.get(0).getTimestamp();
			long lastTimestamp = samples.get(samples.size() - 1).getTimestamp();
			return (lastTimestamp + firstTimestamp) / 2;
		} else if (samples.size() == 1) {
			return samples.get(0).getTimestamp();
		} else {
			throw new ReportException("No samples given to analyze");
		}
	}

	@Override
	public void startNewAggregationPeriod() {
		this.nextTimestamp = calculateNextTimestamp(nextTimestamp);
	}

	@Override
	public void initializeAggregatorFromFirstSample(Sample firstSample) {
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
