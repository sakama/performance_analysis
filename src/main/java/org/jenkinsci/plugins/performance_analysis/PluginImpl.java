package org.jenkinsci.plugins.performance_analysis;

import hudson.Plugin;
import hudson.tasks.BuildStep;

public class PluginImpl extends Plugin {
    @SuppressWarnings("deprecation")
    public void start() throws Exception {
        BuildStep.PUBLISHERS.add(AnalysisReportPublisher.DESCRIPTOR);
    }
}