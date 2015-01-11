package se.raketavdelningen.ci.jenkins.performance.sample;

import java.util.Arrays;


/**
 * A sample distribution is a record with information about the elapsed time for the call
 * and the number of calls that matched this value
 *
 */
public class SamplesDistribution {

    private static final String EMPTY_FOR_DATAPOINT = "\"\"";
    
    private static final String EMPTY_FOR_LABEL = "0";

    private long nrBelow10Percentile;

    private int percentile10;

    private long nrBetween10And30Percentile;

    private int percentile30;

    private long nrBetween30And70Percentile;

    private int percentile70;

    private long nrBetween70And90Percentile;

    private int percentile90;

    private long nrAbove90Percentile;

    private int max;

    public SamplesDistribution() {
        super();
    }

    public SamplesDistribution(int maxValue) {
        super();
        this.percentile10 = (int) (maxValue * 0.1);
        this.percentile30 = (int) (maxValue * 0.3);
        this.percentile70 = (int) (maxValue * 0.7);
        this.percentile90 = (int) (maxValue * 0.9);
        this.max = maxValue;
    }

    public int getPercentile10() {
        return percentile10;
    }

    public void setPercentile10(int percentile10) {
        this.percentile10 = percentile10;
    }

    public int getPercentile30() {
        return percentile30;
    }

    public void setPercentile30(int percentile30) {
        this.percentile30 = percentile30;
    }

    public int getPercentile70() {
        return percentile70;
    }

    public void setPercentile70(int percentile70) {
        this.percentile70 = percentile70;
    }

    public int getPercentile90() {
        return percentile90;
    }

    public void setPercentile90(int percentile90) {
        this.percentile90 = percentile90;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void addSampleToDistribution(Sample sample) {
        int elapsed = sample.getElapsed();
        if (elapsed < percentile10) {
            nrBelow10Percentile++;
        } else if (elapsed < percentile30) {
            nrBetween10And30Percentile++;
        } else if (elapsed < percentile70) {
            nrBetween30And70Percentile++;
        } else if (elapsed < percentile90) {
            nrBetween70And90Percentile++;
        } else {
            nrAbove90Percentile++;
        }
    }

    public long getNrBelow10Percentile() {
        return nrBelow10Percentile;
    }

    public void setNrBelow10Percentile(long nrBelow10Percentile) {
        this.nrBelow10Percentile = nrBelow10Percentile;
    }

    public long getNrBetween10And30Percentile() {
        return nrBetween10And30Percentile;
    }

    public void setNrBetween10And30Percentile(long nrBetween10And30Percentile) {
        this.nrBetween10And30Percentile = nrBetween10And30Percentile;
    }

    public long getNrBetween30And70Percentile() {
        return nrBetween30And70Percentile;
    }

    public void setNrBetween30And70Percentile(long nrBetween30And70Percentile) {
        this.nrBetween30And70Percentile = nrBetween30And70Percentile;
    }

    public long getNrBetween70And90Percentile() {
        return nrBetween70And90Percentile;
    }

    public void setNrBetween70And90Percentile(long nrBetween70And90Percentile) {
        this.nrBetween70And90Percentile = nrBetween70And90Percentile;
    }

    public long getNrAbove90Percentile() {
        return nrAbove90Percentile;
    }

    public void setNrAbove90Percentile(long nrAbove90Percentile) {
        this.nrAbove90Percentile = nrAbove90Percentile;
    }
    
    public String getDistributionLabelsArray() {
        String[] labels = new String[9];
        labels[0] = EMPTY_FOR_DATAPOINT;
        labels[1] = String.valueOf(percentile10);
        labels[2] = EMPTY_FOR_DATAPOINT;
        labels[3] = String.valueOf(percentile30);
        labels[4] = EMPTY_FOR_DATAPOINT;
        labels[5] = String.valueOf(percentile70);
        labels[6] = EMPTY_FOR_DATAPOINT;
        labels[7] = String.valueOf(percentile90);
        labels[8] = EMPTY_FOR_DATAPOINT;
        return Arrays.toString(labels);
    }
    
    public String getDistributionsSamplesArray() {
        String[] samples = new String[9];
        samples[0] = String.valueOf(nrBelow10Percentile);
        samples[1] = EMPTY_FOR_LABEL;
        samples[2] = String.valueOf(nrBetween10And30Percentile);
        samples[3] = EMPTY_FOR_LABEL;
        samples[4] = String.valueOf(nrBetween30And70Percentile);
        samples[5] = EMPTY_FOR_LABEL;
        samples[6] = String.valueOf(nrBetween70And90Percentile);
        samples[7] = EMPTY_FOR_LABEL;
        samples[8] = String.valueOf(nrAbove90Percentile);
        return Arrays.toString(samples);
    }
}
