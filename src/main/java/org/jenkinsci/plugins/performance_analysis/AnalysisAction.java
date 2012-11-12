package org.jenkinsci.plugins.performance_analysis;

import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.performance_analysis.SqlDigestParser.SqlSummary;

public class AnalysisAction implements Action {

    @SuppressWarnings("unused") 
    private static final long serialVersionUID = 1L;
    
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(AnalysisAction.class.getName());

    // 画面閲覧権限
    private AbstractBuild<?, ?> owner;
    private String ptpath;

    public AnalysisAction(AbstractBuild<?, ?> owner, String ptpath) {
        this.owner = owner;
        this.ptpath = ptpath;
    }

    //左メニューの表示名
    public String getDisplayName() {
        return "Performance Analysis";
    }

    //左メニューアイコン
    public String getIconFileName() {
        return "/plugin/performance_analysis/images/icon.png";
    }

    //URLに使われる文字列
    public String getUrlName() {
        return "PerformanceAnalysis";
    }

    /**
     * 画面閲覧権限を取得する
     */
    public AbstractBuild<?, ?> getOwner() {
        return this.owner;
    }
    
    public List<SqlSummary> getPtdump() {
        SqlDigestParser parser = new SqlDigestParser(this.ptpath);
        return parser.getResult();
    }
}