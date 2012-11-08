package org.jenkinsci.plugins.performance_analysis;

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalysisAction implements Action {

    private static final long serialVersionUID = 1L;

    // 画面閲覧権限
    private AbstractBuild<?, ?> owner;

    public AnalysisAction(AbstractBuild<?, ?> owner) {
        this.owner = owner;
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
    
    public String getPtdump() {
        String ptdump = "dumpdata";
        return ptdump;
    }
}