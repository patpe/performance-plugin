package se.raketavdelningen.ci.jenkins.performance.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.raketavdelningen.ci.jenkins.performance.sample.Sample;

public abstract class SampleGroupFunction {

    private Map<String, List<Sample>> samplesMap = new HashMap<>();
    
    public final void addSampleToGroup(Sample sample) {
        String groupKey = getSampleGroupKey(sample);
        List<Sample> samples = samplesMap.get(groupKey);
        if (samples == null) {
            samples = new ArrayList<>();
            samplesMap.put(groupKey, samples);
        }
        samples.add(sample);
    }
    
    public abstract String getSampleGroupKey(Sample sample);
    
    public final Set<String> getKeys() {
        return samplesMap.keySet();
    }
    
    public final List<Sample> getSamples(String key) {
        return samplesMap.get(key);
    }
    
    public final void clearGroups() {
        samplesMap.clear();
    }
}
