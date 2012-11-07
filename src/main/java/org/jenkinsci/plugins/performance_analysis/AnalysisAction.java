package org.jenkinsci.plugins.performance_analysis;

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public class AnalysisAction implements Action {

    private static final long serialVersionUID = 1L;

    private AbstractBuild<?, ?> owner;
    private String resultFileName;

    public AnalysisAction(AbstractBuild<?, ?> owner) {
        this.owner = owner;
    }

    public String getDisplayName() {
        return "Performance Analysis";
    }

    public String getIconFileName() {
        return "/plugin/performance_analysis/images/icon.png";
    }

    public String getUrlName() {
        return "Performance Analysis";
    }

    public AbstractBuild<?, ?> getOwner() {
        return this.owner;
    }

    public List<FunctionMetrics> getFunctionMetricsList() {
        return this.parseResultXml();
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    // Analysis_result.xml ファイルを解析して、
    // メソッドに関するメトリクス結果を List に格納
    private List<FunctionMetrics> parseResultXml() {
        List<FunctionMetrics> result = new ArrayList<FunctionMetrics>();

        if (this.resultFileName != null) {

            SAXReader saxReader = new SAXReader();

            try {
                Document doc = saxReader.read(new File(this.owner.getRootDir(),
                        this.resultFileName));
                XPath xpath = DocumentHelper.createXPath("//function");

                for (Element func : (List<Element>) xpath.selectNodes(doc)) {
                    FunctionMetrics fm = new FunctionMetrics();

                    fm.name = func.elementTextTrim("name");
                    fm.ccn = this.parseInt(func.elementTextTrim("ccn"));
                    fm.ncss = this.parseInt(func.elementTextTrim("ncss"));

                    result.add(fm);
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Integer parseInt(String number) {
        Integer result = null;

        if (number != null) {
            try {
                result = Integer.parseInt(number);
            } catch (NumberFormatException ex) {
            }
        }

        return result;
    }

    // メソッドのメトリクス結果を格納するためのクラス
    public class FunctionMetrics {
        public String name;
        public Integer ncss;
        public Integer ccn;
    }
}