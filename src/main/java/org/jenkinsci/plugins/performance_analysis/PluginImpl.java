package org.jenkinsci.plugins.performance_analysis;

import hudson.Plugin;
import hudson.tasks.BuildStep;

public class PluginImpl extends Plugin {
    public void start() throws Exception {
        BuildStep.PUBLISHERS.add(AnalysisPublisher.DESCRIPTOR);
    }
}