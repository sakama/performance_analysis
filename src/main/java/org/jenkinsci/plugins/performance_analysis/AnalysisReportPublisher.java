package org.jenkinsci.plugins.performance_analysis;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.Launcher;
import hudson.Util;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Publisher;
import hudson.tasks.BuildStepMonitor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * 管理画面「ビルド後の処理」のプルダウンに出てくる。
 * 実際の処理はActionクラスが担当する
 * @author Satoshi Akama
 */
public class AnalysisReportPublisher extends Publisher {
    
    private final String ptpath;
    
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(AnalysisReportPublisher.class.getName());

    @SuppressWarnings("deprecation")
    @DataBoundConstructor
    public AnalysisReportPublisher(String ptpath) {
        this.ptpath = ptpath;
    }

    /**
     * ビルド後に実施する処理を記述
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        AnalysisReportAction act = new AnalysisReportAction(build, this.getPtpath());

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
            super(AnalysisReportPublisher.class);
        }

        @Override
        public String getDisplayName() {
            return "Performance Analysis";
        }
        
        /**
         * Form Validation
         * 画面保存時に呼ばれる。フォーム要素のonblur時のバリデーションは別途必要
         */
        @Override
        public Publisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            String ptpath = Util.fixEmptyAndTrim(formData.getString("ptpath"));
            if (ptpath == null || ptpath.length() == 0) {
                throw new FormException("パスが入力されていません。", "ptpath");
            }
            File objFile = new File(ptpath);
            if(!objFile.exists()) {
                throw new FormException("ディレクトリが存在しません。", "ptpath");
            } else if(!objFile.canRead()) {
                throw new FormException("ディレクトリに読み取り権限がありません。", "ptpath");
            }
            
            return super.newInstance(req, formData);
        }
        
        /**
         * Form Validation
         * フォーム要素のonblur時に呼ばれる。画面保存時のバリデーションは別途必要
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
