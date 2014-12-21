package se.raketavdelningen.ci.jenkins.performance.group;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;

/**
 * Test class for {@link LabelSampleGroupFunction}
 * 
 * @author patrikp
 *
 */
public class LabelSampleGroupFunctionTest {
	
	@Test
	public void testGetSampleGroupKey() {
		SampleGroupFunction group = new LabelSampleGroupFunction();
		
		PerformanceSample sample = new PerformanceSample(1, 1, true, 1, "label", "url");
		assertEquals("label", group.getSampleGroupKey(sample));
	}

	@Test
	public void testAddSampleToGroup() {
		SampleGroupFunction group = new LabelSampleGroupFunction();
		
		PerformanceSample sample = new PerformanceSample(1, 1, true, 1, "label", "url");
		group.addSampleToGroup(sample);
		
		List<PerformanceSample> samples = group.getSamples("label");
		assertEquals(1, samples.size());
	}
	
	@Test
	public void testAddSamplesToGroup() {
		SampleGroupFunction group = new LabelSampleGroupFunction();
		
		addSampleWithLabelToGroup("label1", group);
		addSampleWithLabelToGroup("label2", group);
		addSampleWithLabelToGroup("label3", group);
		
		List<PerformanceSample> samples = group.getSamples("label1");
		assertEquals(1, samples.size());
		assertEquals("label1", samples.get(0).getLabel());
		
		samples = group.getSamples("label2");
		assertEquals(1, samples.size());
		assertEquals("label2", samples.get(0).getLabel());
		
		samples = group.getSamples("label3");
		assertEquals(1, samples.size());
		assertEquals("label3", samples.get(0).getLabel());
	}

	@Test
	public void testAddMultipleSamplesToGroup() {
		SampleGroupFunction group = new LabelSampleGroupFunction();
		
		for (int i = 0; i < 100; i++) {
			addSampleWithLabelToGroup("label1", group);
			addSampleWithLabelToGroup("label2", group);
			addSampleWithLabelToGroup("label3", group);
		}
		
		List<PerformanceSample> samples = group.getSamples("label1");
		assertEquals(100, samples.size());
		for (PerformanceSample sample: samples) {
			assertEquals("label1", sample.getLabel());
		}
	}
	
	private void addSampleWithLabelToGroup(String label,
			SampleGroupFunction group) {
		PerformanceSample sample = new PerformanceSample(1, 1, true, 1, label, "url");
		group.addSampleToGroup(sample);
	}

	@Test
	public void testGetKeys() {
		SampleGroupFunction group = new LabelSampleGroupFunction();
		
		addSampleWithLabelToGroup("label1", group);
		addSampleWithLabelToGroup("label2", group);
		addSampleWithLabelToGroup("label3", group);
		
		Set<String> keys = group.getKeys();
		assertEquals(3, keys.size());
		assertTrue(keys.containsAll(Arrays.asList("label1", "label2", "label3")));
	}

	@Test
	public void testGetSamples() {
		SampleGroupFunction group = new LabelSampleGroupFunction();
		
		addSampleWithLabelToGroup("label1", group);
		addSampleWithLabelToGroup("label2", group);
		addSampleWithLabelToGroup("label3", group);		
		
		List<PerformanceSample> samples = group.getSamples("label1");
		assertEquals(1, samples.size());
		PerformanceSample sample = samples.get(0);
		assertEquals("label1", sample.getLabel());
		
		samples = group.getSamples("label2");
		assertEquals(1, samples.size());
		sample = samples.get(0);
		assertEquals("label2", sample.getLabel());
		
		samples = group.getSamples("label3");
		assertEquals(1, samples.size());
		sample = samples.get(0);
		assertEquals("label3", sample.getLabel());
	}
	
	@Test
	public void testGetMultipleSamples() {
		SampleGroupFunction group = new LabelSampleGroupFunction();
		
		for (int i = 0; i < 100; i++) {
			addSampleWithLabelToGroup("label1", group);
			addSampleWithLabelToGroup("label2", group);
		}
		addSampleWithLabelToGroup("label3", group);		
		
		List<PerformanceSample> samples = group.getSamples("label1");
		assertEquals(100, samples.size());
		for (PerformanceSample sample : samples) {
			assertEquals("label1", sample.getLabel());
		}
		
		samples = group.getSamples("label2");
		assertEquals(100, samples.size());
		for (PerformanceSample sample : samples) {
			assertEquals("label2", sample.getLabel());
		}
		
		samples = group.getSamples("label3");
		assertEquals(1, samples.size());
		PerformanceSample sample = samples.get(0);
		assertEquals("label3", sample.getLabel());
	}

	@Test
	public void testClearGroups() {
		SampleGroupFunction group = new LabelSampleGroupFunction();
		
		addSampleWithLabelToGroup("label1", group);
		addSampleWithLabelToGroup("label2", group);
		addSampleWithLabelToGroup("label3", group);		

		assertFalse(group.getSamples("label1").isEmpty());
		assertFalse(group.getSamples("label2").isEmpty());
		assertFalse(group.getSamples("label3").isEmpty());
		assertEquals(3, group.getKeys().size());
		group.clearGroups();		
		assertEquals(0, group.getKeys().size());
	}
}
