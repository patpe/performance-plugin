package se.raketavdelningen.ci.jenkins.performance.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.raketavdelningen.ci.jenkins.performance.group.GroupFunction;
import se.raketavdelningen.ci.jenkins.performance.group.LabelSampleGroupFunction;
import se.raketavdelningen.ci.jenkins.performance.sample.Sample;

public class ReportParserTest extends ReportParser {

    private static final int NR_OF_SAMPLES = 100;
    
    private List<Sample> samples = new ArrayList<Sample>(NR_OF_SAMPLES);

    int index = 0;
    
    @Before
    public void setupSamples() {
        samples.clear();
        for (int i = 0; i < NR_OF_SAMPLES; i++) {
            if (i % 2 == 0) {
                samples.add(new Sample(1000 + i, 10000 + i, true, 100000 + i, "label1", "url1"));
            } else {
                samples.add(new Sample(1000 + i, 10000 + i, true, 100000 + i, "label2", "url2"));
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        // Do nothing
    }

    @Override
    public Sample getNextSample() {
        if (index == 100) {
            return null;
        }
        return samples.get(index++);
    }

    @Override
    public void resetStream() {
        index = 0;
    }
    
    @Test
    public void testFindMaxPerKey() {
        GroupFunction function = new LabelSampleGroupFunction();
        Map<String, Integer> maxValues = findMaxPerKey(function);
        assertEquals(2, maxValues.size());
        for (String label : maxValues.keySet()) {
            if ("label1".equals(label)) {
                assertEquals(new Integer(10000 + NR_OF_SAMPLES - 2), maxValues.get(label));
            } else {
                assertEquals(new Integer(10000 + NR_OF_SAMPLES - 1), maxValues.get(label));
            }
        }
    }
}
