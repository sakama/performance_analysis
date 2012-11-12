package org.jenkinsci.plugins.performance_analysis;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Percona-Toolkitのpt-query-digest --explainコマンドの実行結果ファイルをパースし、 配列に落とす
 * 
 * @author Satoshi Akama
 */
public class SqlDigestParser {

    private String ptpath;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SqlDigestParser.class.getName());

    SqlDigestParser(String ptpath) {
        this.ptpath = ptpath;
    }

    /**
     * Percona-Toolkitの結果ファイルをパースする
     * 
     * @TODO 結果ファイルをコピーして過去のビルド履歴が見られるようにする
     */
    public List<SqlSummary> getResult() {
        String ptdump = this.readFile();
        return this.parseResult(ptdump);
    }

    public List<SqlSummary> parseResult(String dump) {
        List<SqlSummary> result = new ArrayList<SqlSummary>();
        // クエリ解析結果のブロックを抽出
        String regex = "(# Query [0-9]{1,3}.*?)(\n\n|\n$)";
        Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(dump);
        
        while (m.find()) {
            // ID及びクエリ番号を取得
            SqlSummary summary = new SqlSummary();
            String queryBlock = m.group(1);
            summary.dump = queryBlock;
            String regexLine = "# Query ([0-9]{1,3}): (.+?) QPS.+?, ID(.+?) ";
            Pattern p2 = Pattern.compile(regexLine);
            Matcher m2 = p2.matcher(queryBlock);
            while (m2.find()) {
                summary.qindex = this.parseInt(m2.group(1));
                summary.qps = m2.group(2);
                summary.id = m2.group(3);
            }
            // Time rangeを取得
            String regexLine3 = "# Time range: (.*)";
            Pattern p3 = Pattern.compile(regexLine3);
            Matcher m3 = p3.matcher(queryBlock);
            if (m3.find()) {
                summary.time_range = m3.group(1);
            }
            // 結果メトリクスを取得
            String regexLine4 = "=======\n(# Count.*?)# String:";
            Pattern p4 = Pattern.compile(regexLine4, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m4 = p4.matcher(queryBlock);
            if (m4.find()) {
                String metrics = this.makeMetrics(m4.group(1));
                summary.metrics = metrics;
            }
            // Query_timeを取得
            String regexLine5 = "# Query_time distribution\n(.*?)\n([^#]|# Tables)";
            Pattern p5 = Pattern.compile(regexLine5, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m5 = p5.matcher(queryBlock);
            if (m5.find()) {
                summary.qtime = m5.group(1).replaceAll("\n", "<br />");
            }

            // SQL文の取得
            String[] rowArray = queryBlock.split("\n");
            summary.query = rowArray[rowArray.length-1];
            
            // Hostnameの取得
            String regexLine6 = "# Hostname: (.*)";
            Pattern p6 = Pattern.compile(regexLine6);
            Matcher m6 = p6.matcher(dump);
            if (m6.find()) {
                summary.global_hostname = m6.group(1);
            }
            
            // 測定日時の取得
            String regexLine7 = "# Current date: (.*)";
            Pattern p7 = Pattern.compile(regexLine7);
            Matcher m7 = p7.matcher(dump);
            if (m7.find()) {
                summary.global_current_date = m7.group(1);
            }
            
            // 概要の取得
            String regexLine8 = "# Overall: (.*) total, (.*) unique, (.*) QPS, (.*) concurrency.*";
            Pattern p8 = Pattern.compile(regexLine8);
            Matcher m8 = p8.matcher(dump);
            if (m8.find()) {
                summary.global_total = this.parseInt(m8.group(1));
                summary.global_unique = this.parseInt(m8.group(2));
                summary.global_qps = m8.group(3);
                summary.global_concurrency = m8.group(4);
            }

            result.add(summary);
        }
        return result;
    }

    public class SqlSummary {
        public Integer qindex;
        public String id;
        public String query;
        public String qps;
        public String time_range;
        public String metrics;
        public String qtime;
        public String dump;
        //global
        public String global_hostname;
        public String global_current_date;
        public Integer global_total;
        public Integer global_unique;
        public String global_qps;
        public String global_concurrency;
    }

    public String makeMetrics(String metricsdump) {
        String metrics = "";

        String regex = "(# .*?)\n";
        Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(metricsdump);
        Integer count = 0;
        String[] colArray = { "Count", "Exec time", "Lock time", "Rows sent", "Rows examine", "Query size" };
        while (m.find()) {
            String row = m.group(1).replaceAll("(# .*?)\\s{1,12}", "");
            if (count == 0) {
                row = " " + row;
            }
            String[] rowArray = row.split("\\s{1,10}");
            for (int i = 0; i < 9; i++) {
                if (i == 0) {
                    metrics += "<tr><td>" + colArray[count];
                } else {
                    if (i >= rowArray.length) {
                        metrics += "<td>&nbsp;";
                    } else {
                        metrics += "<td>" + rowArray[i];
                    }
                }
                if(i==1) {
                    metrics += "%";
                }
                metrics += "</td>";
                if (i == 9) {
                    metrics += "</tr>";
                }
            }
            count++;
        }
        return metrics;
    }

    /**
     * Percona-Toolkitの結果ファイルを読み込む
     * 
     * @return
     */
    public String readFile() {
        String ptdump = "";
        try {
            File file = new File(this.ptpath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            while ((str = br.readLine()) != null) {
                ptdump += str;
                ptdump += "\n";
            }
            br.close();
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File is not found", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File can not readable", e);
        }
        return ptdump;
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
}
