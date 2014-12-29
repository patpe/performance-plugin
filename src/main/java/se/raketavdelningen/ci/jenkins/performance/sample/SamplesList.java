package se.raketavdelningen.ci.jenkins.performance.sample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SamplesList extends ArrayList<AggregatedSample> {

    /**
     * 
     */
    private static final long serialVersionUID = 6051136143483379711L;
    
    private static final DateFormat format = new SimpleDateFormat("HH:mm");
    
    public String getTimestampArray() {
        Date date = new Date();
        String[] result = new String[size()];
        Arrays.fill(result, "\"\"");
        date.setTime(get(0).getTimestamp());
        result[0] = createDateLabel(date);
        
        int endIndex = size() - 1;
        date.setTime(get(endIndex).getTimestamp());
        result[endIndex] = createDateLabel(date);

        int middleIndex = endIndex / 2;
        date.setTime(get(middleIndex).getTimestamp());
        result[middleIndex] = createDateLabel(date);
        return Arrays.toString(result);
    }

    private String createDateLabel(Date date) {
        return "\"" + format.format(date) + "\"";
    }
    
    public String getMinArray() {
        Long[] result = new Long[size()];
        int index = 0;
        for (AggregatedSample sample : this) { 
            result[index] = sample.getMin();
            index++;
        }
        return Arrays.toString(result);
    }
    
    public String getAverageArray() {
        Long[] result = new Long[size()];
        int index = 0;
        for (AggregatedSample sample : this) { 
            result[index] = sample.getAverage();
            index++;
        }
        return Arrays.toString(result);
    }
    
    public String getMaxArray() {
        Long[] result = new Long[size()];
        int index = 0;
        for (AggregatedSample sample : this) { 
            result[index] = sample.getMax();
            index++;
        }
        return Arrays.toString(result);
    }
}
