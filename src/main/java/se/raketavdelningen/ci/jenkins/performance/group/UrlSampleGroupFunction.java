package se.raketavdelningen.ci.jenkins.performance.group;

import se.raketavdelningen.ci.jenkins.performance.sample.Sample;

public class UrlSampleGroupFunction extends SampleGroupFunction {

    @Override
    public String getSampleGroupKey(Sample sample) {
        return sample.getUrl();
    }
}
