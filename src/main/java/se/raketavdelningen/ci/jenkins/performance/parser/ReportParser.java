package se.raketavdelningen.ci.jenkins.performance.parser;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

import se.raketavdelningen.ci.jenkins.performance.group.GroupFunction;
import se.raketavdelningen.ci.jenkins.performance.sample.Sample;


public abstract class ReportParser implements Closeable {

    /**
     * Returns the next sample in the underlying stream
     * 
     * @return Sample, null if no more samples exist in the file
     */
    public abstract Sample getNextSample();
    
    /**
     * Resets the underlying stream so that it will read the first sample in the file upon the next call to {@link #getNextSample()}.
     */
    public abstract void resetStream();
    
    /**
     * Finds the max values for each key in the report file. This method will read through the entire file and then call {@link #resetStream()}.
     * 
     * @param groupFunction Function used to group samples
     * 
     * @return Map with max value per key
     */
    public final Map<String, Integer> findMaxPerKey(GroupFunction groupFunction) {
        Map<String, Integer> result = new HashMap<>();
        Sample sample = getNextSample();
        String key;
        while (sample != null) {
            key = groupFunction.getSampleGroupKey(sample);
            replaceMaxValueIfSampleIsGreater(key, sample, result);
            sample = getNextSample();
        }
        resetStream();
        return result;
    }

    private void replaceMaxValueIfSampleIsGreater(String key, Sample sample,
            Map<String, Integer> result) {
        Integer currentMax = result.get(key);
        if (currentMax != null) {
            if (currentMax < sample.getElapsed()) {
                currentMax = sample.getElapsed();
                result.put(key, sample.getElapsed());
            }
        } else {
            result.put(key, sample.getElapsed());
        }
    }
}
