package se.raketavdelningen.ci.jenkins.performance.parser;

import se.raketavdelningen.ci.jenkins.performance.sample.PerformanceSample;


public abstract class PerformanceReportParser {

    public abstract PerformanceSample getNextSample();
    
}
