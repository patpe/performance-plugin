package se.raketavdelningen.ci.jenkins.performance.parser;

import se.raketavdelningen.ci.jenkins.performance.sample.Sample;


public abstract class ReportParser {

    public abstract Sample getNextSample();
}
