package se.raketavdelningen.ci.jenkins.performance.group;

import se.raketavdelningen.ci.jenkins.performance.sample.Sample;

public class LabelSampleGroupFunction extends GroupFunction {

    @Override
    public String getSampleGroupKey(Sample sample) {
        return sample.getLabel();
    }
}
