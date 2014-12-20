package se.raketavdelningen.ci.jenkins.performance.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

public abstract class SampleGroupFunction {

    private Map<String, List<PerformanceSample>> samplesMap = new HashMap<>();
    
    public final void addSampleToGroup(PerformanceSample sample) {
        String groupKey = getSampleGroupKey(sample);
        List<PerformanceSample> samples = samplesMap.get(groupKey);
        if (samples == null) {
            samples = new ArrayList<>();
            samplesMap.put(groupKey, samples);
        }
        samples.add(sample);
    }
    
    public abstract String getSampleGroupKey(PerformanceSample sample);
    
    public final Set<String> getKeys() {
        return samplesMap.keySet();
    }
    
    public final List<PerformanceSample> getSamples(String key) {
        return samplesMap.get(key);
    }
    
    public final void clearGroups() {
        samplesMap.clear();
    }
}
