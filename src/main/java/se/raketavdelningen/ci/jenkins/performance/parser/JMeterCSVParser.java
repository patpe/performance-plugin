package se.raketavdelningen.ci.jenkins.performance.parser;

import hudson.FilePath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import se.raketavdelningen.ci.jenkins.performance.exception.ReportException;
import se.raketavdelningen.ci.jenkins.performance.sample.Sample;


public class JMeterCSVParser extends ReportParser {
    
    private BufferedReader reader;
       
    private int timestampIndex = 0;
    
    private int elapsedIndex = 1;
    
    private int labelIndex = 2;
    
    private int successIndex = 3;
    
    private int bytesIndex = 4;
    
    private int urlIndex = 5;
    
    public JMeterCSVParser(FilePath file, boolean containsHeader) {
        try {
            InputStream ins = file.read();
            reader = new BufferedReader(new InputStreamReader(ins));
            if (containsHeader) {
                setFieldIndexFromHeaderLine();
            }
        } catch (Exception e) {
            throw new ReportException(e);
        }
    }

    private void setFieldIndexFromHeaderLine() throws IOException {
        String headerLine = reader.readLine();
        String[] headers = headerLine.split(JMeterFieldNames.CSV_FILE_DELIMITER);
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
                this.successIndex = headerCounter;
            } else if (JMeterFieldNames.URL.equals(header)) {
                this.urlIndex = headerCounter;
            }
        }
    }

    @Override
    public Sample getNextSample() throws ReportException {
        try {
            String sampleLine = reader.readLine();
            if (sampleLine == null) {
                reader.close();
                return null;
            }
            
            String[] sampleValues = sampleLine.split(JMeterFieldNames.CSV_FILE_DELIMITER);
            return new Sample(
                    Long.valueOf(sampleValues[timestampIndex]),
                    Long.valueOf(sampleValues[elapsedIndex]),
                    Boolean.valueOf(sampleValues[successIndex]),
                    Long.valueOf(sampleValues[bytesIndex]),
                    sampleValues[labelIndex],
                    sampleValues[urlIndex]);
        } catch (IOException e) {
            throw new ReportException(e);
        }
    }
}
