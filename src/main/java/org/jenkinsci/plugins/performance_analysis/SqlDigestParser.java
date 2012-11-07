package org.jenkinsci.plugins.performance_analysis;

import hudson.FilePath;
import java.io.IOException;

/**
 * Percona-Toolkitのpt-query-digest --explainコマンドの実行結果ファイルをパースし、
 * 配列に落とす
 * @author Satoshi Akama
 */
public class SqlDigestParser {

    private static String dirPath;

    SqlDigestParser() {
    }

}
