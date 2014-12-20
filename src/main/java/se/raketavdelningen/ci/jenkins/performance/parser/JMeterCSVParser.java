package se.raketavdelningen.ci.jenkins.performance.parser;

import hudson.FilePath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import se.raketavdelningen.ci.jenkins.performance.exception.PerformanceReportException;
import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;


public class JMeterCSVParser extends PerformanceReportParser {
    
    private BufferedReader reader;
       
    private int timestampIndex = 0;
    
    private int elapsedIndex = 1;
    
    private int labelIndex = 2;
    
    private int successIndex = 3;
    
    private int bytesIndex = 4;
    
    private int urlIndex = 5;
    
    public JMeterCSVParser(FilePath file, boolean containsHeader) throws IOException {
        InputStream ins = file.read();
        reader = new BufferedReader(new InputStreamReader(ins));
        if (containsHeader) {
            setFieldIndexFromHeaderLine();
        }
    }

    private void setFieldIndexFromHeaderLine() throws IOException {
        String headerLine = reader.readLine();
        String[] headers = headerLine.split(",");
        String header;
        for (int headerCounter = 0; headerCounter < headers.length; headerCounter++) {
            header = headers[headerCounter];
            if (JMeterFieldNames.TIMESTAMP.equals(header)) {
                this.timestampIndex = headerCounter;
            } else if (JMeterFieldNames.ELAPSED.equals(header)) {
                this.elapsedIndex = headerCounter;
            } else if (JMeterFieldNames.LABEL.equals(header)) {
                this.labelIndex = headerCounter;
            } else if (JMeterFieldNames.BYTES.equals(header)) {
                this.bytesIndex = headerCounter;
            } else if (JMeterFieldNames.SUCCESS.equals(header)) {
                this.successIndex = 0;
            } else if (JMeterFieldNames.URL.equals(header)) {
                this.urlIndex = headerCounter;
            }
        }
    }

    @Override
    public PerformanceSample getNextSample() throws PerformanceReportException {
        try {
            String sampleLine = reader.readLine();
            if (sampleLine == null) {
                reader.close();
                return null;
            }
            
            String[] sampleValues = sampleLine.split(",");
            return new PerformanceSample(
                    Long.valueOf(sampleValues[timestampIndex]),
                    Long.valueOf(sampleValues[elapsedIndex]),
                    Boolean.valueOf(sampleValues[successIndex]),
                    Long.valueOf(sampleValues[bytesIndex]),
                    sampleValues[labelIndex],
                    sampleValues[urlIndex]);
        } catch (IOException e) {
            throw new PerformanceReportException(e);
        }
    }
}
