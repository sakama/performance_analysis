package org.jenkinsci.plugins.performance_analysis;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.Publisher;
import hudson.tasks.BuildStepMonitor;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;

/**
 * 
 * @author Satoshi Akama
 */
public class AnalysisPublisher extends Publisher {

    AnalysisPublisher() {
    }

    /**
     * ビルド後に実施する処理を記述
     */
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        AnalysisAction act = new AnalysisAction(build);

        // ビルド結果にAnalysisActionインスタンスを追加
        // build.xmlにシリアライズされて保存される
        build.addAction(act);

        return true;
    }

    public Descriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<Publisher> {
        DescriptorImpl() {
            super(AnalysisPublisher.class);
        }

        @Override
        public String getDisplayName() {
            return "Performance Analysis";
        }

        @Override
        public AnalysisPublisher newInstance(StaplerRequest req) throws FormException {
            return new AnalysisPublisher();
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
}
