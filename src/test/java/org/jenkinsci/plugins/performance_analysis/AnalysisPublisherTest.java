package org.jenkinsci.plugins.performance_analysis;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import hudson.util.FormValidation;

import java.io.IOException;
import javax.servlet.ServletException;

public class AnalysisPublisherTest {

    @Test
    public void doCheckPtpathTest() throws IOException, ServletException {
        //FormValidation actual = AnalysisPublisher.DESCRIPTOR.doCheckPtpath("");
        //assertEquals(actual, FormValidation.error("パスが入力されていません。"));
    }
}
