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
 * Test class for {@link UrlSampleGroupFunction}
 * 
 * @author patrikp
 *
 */
public class UrlSampleGroupFunctionTest {
	
	@Test
	public void testGetSampleGroupKey() {
		SampleGroupFunction group = new UrlSampleGroupFunction();
		
		PerformanceSample sample = new PerformanceSample(1, 1, true, 1, "label", "url");
		assertEquals("url", group.getSampleGroupKey(sample));
	}

	@Test
	public void testAddSampleToGroup() {
		SampleGroupFunction group = new UrlSampleGroupFunction();
		
		PerformanceSample sample = new PerformanceSample(1, 1, true, 1, "label", "url");
		group.addSampleToGroup(sample);
		
		List<PerformanceSample> samples = group.getSamples("url");
		assertEquals(1, samples.size());
	}
	
	@Test
	public void testAddSamplesToGroup() {
		SampleGroupFunction group = new UrlSampleGroupFunction();
		
		addSampleWithUrlToGroup("url1", group);
		addSampleWithUrlToGroup("url2", group);
		addSampleWithUrlToGroup("url3", group);
		
		List<PerformanceSample> samples = group.getSamples("url1");
		assertEquals(1, samples.size());
		assertEquals("url1", samples.get(0).getUrl());
		
		samples = group.getSamples("url2");
		assertEquals(1, samples.size());
		assertEquals("url2", samples.get(0).getUrl());
		
		samples = group.getSamples("url3");
		assertEquals(1, samples.size());
		assertEquals("url3", samples.get(0).getUrl());
	}

	@Test
	public void testAddMultipleSamplesToGroup() {
		SampleGroupFunction group = new UrlSampleGroupFunction();
		
		for (int i = 0; i < 100; i++) {
			addSampleWithUrlToGroup("url1", group);
			addSampleWithUrlToGroup("url2", group);
			addSampleWithUrlToGroup("url3", group);
		}
		
		List<PerformanceSample> samples = group.getSamples("url1");
		assertEquals(100, samples.size());
		for (PerformanceSample sample: samples) {
			assertEquals("url1", sample.getUrl());
		}
	}
	
	private void addSampleWithUrlToGroup(String url,
			SampleGroupFunction group) {
		PerformanceSample sample = new PerformanceSample(1, 1, true, 1, "label", url);
		group.addSampleToGroup(sample);
	}

	@Test
	public void testGetKeys() {
		SampleGroupFunction group = new UrlSampleGroupFunction();
		
		addSampleWithUrlToGroup("url1", group);
		addSampleWithUrlToGroup("url2", group);
		addSampleWithUrlToGroup("url3", group);
		
		Set<String> keys = group.getKeys();
		assertEquals(3, keys.size());
		assertTrue(keys.containsAll(Arrays.asList("url1", "url2", "url3")));
	}

	@Test
	public void testGetSamples() {
		SampleGroupFunction group = new UrlSampleGroupFunction();
		
		addSampleWithUrlToGroup("url1", group);
		addSampleWithUrlToGroup("url2", group);
		addSampleWithUrlToGroup("url3", group);		
		
		List<PerformanceSample> samples = group.getSamples("url1");
		assertEquals(1, samples.size());
		PerformanceSample sample = samples.get(0);
		assertEquals("url1", sample.getUrl());
		
		samples = group.getSamples("url2");
		assertEquals(1, samples.size());
		sample = samples.get(0);
		assertEquals("url2", sample.getUrl());
		
		samples = group.getSamples("url3");
		assertEquals(1, samples.size());
		sample = samples.get(0);
		assertEquals("url3", sample.getUrl());
	}
	
	@Test
	public void testGetMultipleSamples() {
		SampleGroupFunction group = new UrlSampleGroupFunction();
		
		for (int i = 0; i < 100; i++) {
			addSampleWithUrlToGroup("url1", group);
			addSampleWithUrlToGroup("url2", group);
		}
		addSampleWithUrlToGroup("url3", group);		
		
		List<PerformanceSample> samples = group.getSamples("url1");
		assertEquals(100, samples.size());
		for (PerformanceSample sample : samples) {
			assertEquals("url1", sample.getUrl());
		}
		
		samples = group.getSamples("url2");
		assertEquals(100, samples.size());
		for (PerformanceSample sample : samples) {
			assertEquals("url2", sample.getUrl());
		}
		
		samples = group.getSamples("url3");
		assertEquals(1, samples.size());
		PerformanceSample sample = samples.get(0);
		assertEquals("url3", sample.getUrl());
	}

	@Test
	public void testClearGroups() {
		SampleGroupFunction group = new UrlSampleGroupFunction();
		
		addSampleWithUrlToGroup("url1", group);
		addSampleWithUrlToGroup("url2", group);
		addSampleWithUrlToGroup("url3", group);		

		assertFalse(group.getSamples("url1").isEmpty());
		assertFalse(group.getSamples("url2").isEmpty());
		assertFalse(group.getSamples("url3").isEmpty());
		assertEquals(3, group.getKeys().size());
		group.clearGroups();		
		assertEquals(0, group.getKeys().size());
	}
}
