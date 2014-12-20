package se.raketavdelningen.ci.jenkins.performance.group;

import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

public class LabelSampleGroupFunction extends SampleGroupFunction {

    @Override
    public String getSampleGroupKey(PerformanceSample sample) {
        return sample.getLabel();
    }
}
