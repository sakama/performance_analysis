package org.jenkinsci.plugins.performance_analysis;

import static org.junit.Assert.*;
import org.junit.Test;
import hudson.util.FormValidation;

import java.io.IOException;
import javax.servlet.ServletException;

public class AnalysisReportPublisherTest {

    @Test
    public void フォーム要素onblur時のバリデーションチェック() throws IOException, ServletException {
        FormValidation actual = AnalysisReportPublisher.DESCRIPTOR.doCheckPtpath("/var/");
        assertEquals(actual, FormValidation.ok());
        
        FormValidation actual2 = AnalysisReportPublisher.DESCRIPTOR.doCheckPtpath("");
        assertEquals(actual2.toString(), FormValidation.error("パスが入力されていません。").toString());
        
        FormValidation actual3 = AnalysisReportPublisher.DESCRIPTOR.doCheckPtpath("/var/notexist");
        assertEquals(actual3.toString(), FormValidation.error("ディレクトリが存在しません。").toString());
        
        FormValidation actual4 = AnalysisReportPublisher.DESCRIPTOR.doCheckPtpath("/etc/sudoers");
        assertEquals(actual4.toString(), FormValidation.error("ディレクトリに読み取り権限がありません。").toString());
    }
}
