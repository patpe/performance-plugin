package se.raketavdelningen.ci.jenkins.performance.group;

import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

public class UrlSampleGroupFunction extends SampleGroupFunction {

    @Override
    public String getSampleGroupKey(PerformanceSample sample) {
        return sample.getUrl();
    }
}
