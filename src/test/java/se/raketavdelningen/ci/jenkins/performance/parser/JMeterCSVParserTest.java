package se.raketavdelningen.ci.jenkins.performance.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import se.raketavdelningen.ci.jenkins.performance.sample.Sample;

public class JMeterCSVParserTest {

    @Test
    public void testGetNextSample() throws URISyntaxException, IOException {
        File file = initializeFileFromTestResource("jmeter_test_three_rows.csv");
        try (ReportParser parser = new JMeterCSVParser(new FilePath(file), true)) {
            Sample sample = parser.getNextSample();
            int nrOfRows = 0;
            while (sample != null) {
                nrOfRows++;
                sample = parser.getNextSample();

            }
            assertEquals(3, nrOfRows);
        } catch (Exception e) {
            throw e;
        }
    }
    
    @Test
    public void testReset() throws Exception {
        File file = initializeFileFromTestResource("jmeter_test_three_rows.csv");
        try (ReportParser parser = new JMeterCSVParser(new FilePath(file), true)) {
            Sample sample = parser.getNextSample();
            int nrOfRows = 0;
            while (sample != null) {
                nrOfRows++;
                sample = parser.getNextSample();

            }
            assertEquals(3, nrOfRows);
            
            parser.resetStream();
            sample = parser.getNextSample();
            nrOfRows = 0;
            while (sample != null) {
                nrOfRows++;
                sample = parser.getNextSample();

            }
            assertEquals(3, nrOfRows);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testGetNextSampleSpeed() throws URISyntaxException, IOException {
        File file = initializeFileFromTestResource("jmeter_test_many_rows.csv");
        try (ReportParser parser = new JMeterCSVParser(new FilePath(file), true)) {
            Sample sample = parser.getNextSample();
            int nrOfRows = 0;
            while (sample != null) {
                nrOfRows++;
                sample = parser.getNextSample();

            }
            assertEquals(10000, nrOfRows);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testJMeterCSVParser() throws IOException, URISyntaxException {
        File file = initializeFileFromTestResource("jmeter_test1.csv");
        try (ReportParser parser = new JMeterCSVParser(new FilePath(file), true)) {
            Sample sample = parser.getNextSample();
            assertNotNull(sample);
            assertEquals(1234567890, sample.getTimestamp());
            assertEquals("testLabel", sample.getLabel());
            assertEquals("testURL", sample.getUrl());
            assertEquals(123, sample.getElapsed());
            assertEquals(456, sample.getBytes());
            assertTrue(sample.isSuccess());
        } catch (Exception e) {
            throw e;
        }
    }

    private File initializeFileFromTestResource(String testResource) throws URISyntaxException {
        URL resource = getClass().getResource(testResource);
        File file = new File(new URI(resource.toString()));
        return file;
    }
}
