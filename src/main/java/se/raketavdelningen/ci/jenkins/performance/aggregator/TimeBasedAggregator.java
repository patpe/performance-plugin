package se.raketavdelningen.ci.jenkins.performance.aggregator;

import hudson.model.Descriptor;

import java.util.Arrays;
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
		int samplesCounter = 0;
		int samplesErrorCounter = 0;
		long sumOfElapsed = 0;
		boolean success = true;

		long max = Long.MIN_VALUE;
		long min = Long.MAX_VALUE;
		long[] elapsedValues = new long[samples.size()];
		for (Sample sample : samples) {
			long elapsed = sample.getElapsed();
			if (max < elapsed) {
				max = elapsed;
			}
			if (elapsed < min) {
				min = elapsed;
			}

			elapsedValues[samplesCounter] = sample.getElapsed();
			
			sumOfElapsed+=sample.getElapsed();
			success = (success && sample.isSuccess());
			samplesErrorCounter = (sample.isSuccess() ? samplesErrorCounter : samplesErrorCounter + 1);
			samplesCounter++;
		}

		long averageElapsed = sumOfElapsed / samplesCounter;

		return new AggregatedSample(aggregatedTimestamp, max, min, averageElapsed, findPercentile95(elapsedValues),
				success, (long)samplesCounter, (long)samplesErrorCounter, key);
	}

	private long findPercentile95(long[] elapsedValues) {
	    if (elapsedValues.length > 1) {
	        // Sort array
	        Arrays.sort(elapsedValues);
	        // Find and return index that represents 95th index in the array
	        int percentile95Index = (int) Math.floor((elapsedValues.length -1) * 0.95);
	        return elapsedValues[percentile95Index];
	    } else if (elapsedValues.length == 1) {
	        return elapsedValues[0];
	    } else {
	        throw new ReportException("No samples given to analyze");
	    }
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
