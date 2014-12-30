package se.raketavdelningen.ci.jenkins.performance.parser;

import java.io.Closeable;

import se.raketavdelningen.ci.jenkins.performance.sample.Sample;


public abstract class ReportParser implements Closeable {

    public abstract Sample getNextSample();
}
