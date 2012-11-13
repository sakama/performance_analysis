package org.jenkinsci.plugins.performance_analysis;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.Launcher;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Publisher;
import hudson.tasks.BuildStepMonitor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * 
 * @author Satoshi Akama
 */
public class AnalysisPublisher extends Publisher {
    
    private final String ptpath;
    
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(AnalysisPublisher.class.getName());

    @SuppressWarnings("deprecation")
    @DataBoundConstructor
    public AnalysisPublisher(String ptpath) {
        this.ptpath = ptpath;
    }

    /**
     * ビルド後に実施する処理を記述
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        AnalysisAction act = new AnalysisAction(build, this.getPtpath());

        // ビルド結果にAnalysisActionインスタンスを追加
        // build.xmlにシリアライズされて保存される
        build.addAction(act);
        LOGGER.log(Level.SEVERE, "Result was successfully saved.");

        return true;
    }
    
    public String getPtpath() {
        return this.ptpath;
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
        
        /**
         * Form Validation
         * @TODO バリデーションエラーの警告は出るが保存できてしまう
         */
        public FormValidation doCheckPtpath(@QueryParameter String ptpath) throws IOException, ServletException {
            if (ptpath.length() == 0) {
                return FormValidation.error("パスが入力されていません。");
            }
            
            File objFile = new File(ptpath);
            if(!objFile.exists()) {
                return FormValidation.error("ディレクトリが存在しません。");
            } else if(!objFile.canRead()) {
                return FormValidation.error("ディレクトリに読み取り権限がありません。");
            }
            
            return FormValidation.ok();
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
}
