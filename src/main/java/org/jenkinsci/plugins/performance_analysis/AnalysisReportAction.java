package org.jenkinsci.plugins.performance_analysis;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Action;

import org.jenkinsci.plugins.performance_analysis.SqlDigestParser.SqlSummary;

/**
 * ビルド後のレポート作成処理を行う
 * 
 * @author Satoshi Akama
 */
public class AnalysisReportAction implements Action {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    // 画面閲覧権限
    private AbstractBuild<?, ?> owner;
    private String ptpath;
    private List<SqlSummary> ptDump;

    public AnalysisReportAction(AbstractBuild<?, ?> owner, String ptpath) {
        this.owner = owner;
        this.ptpath = ptpath;
        this.ptDump = this.getPtdump();
    }
    
    public void setPtpath(String ptpath) {
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
        if(this.ptDump!=null){
            return this.ptDump;
        }
        SqlDigestParser parser = new SqlDigestParser(this.ptpath);
        return parser.getResult();
    }
}