package org.jenkinsci.plugins.performance_analysis;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Percona-Toolkitのpt-query-digest --explainコマンドの実行結果ファイルをパースし、 リストで返す
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
     * コマンド実行結果ファイルのパース結果をリストで返す
     * TODO 結果ファイルをコピーして過去のビルド履歴が見られるようにする
     */
    public List<SqlSummary> getResult() {
        String ptdump = this.readFile();
        return this.parseResult(ptdump);
    }

    /**
     * Percona-Toolkitの結果ファイルをパースする
     */
    private List<SqlSummary> parseResult(String dump) {
        List<SqlSummary> result = new ArrayList<SqlSummary>();
        // クエリ解析結果のブロックを抽出
        Matcher m = this.getMatches("(# Query [0-9]{1,3}.*?)(\n\n|\n$)", dump, true);
        
        while (m.find()) {
            // ID及びクエリ番号を取得
            SqlSummary summary = new SqlSummary();
            String queryBlock = m.group(1);
            summary.dump = queryBlock;
            Matcher m2 = this.getMatches("# Query ([0-9]{1,3}): (.+?) QPS.+?, ID(.+?) ", queryBlock, false);
            while (m2.find()) {
                summary.qindex = this.parseInt(m2.group(1));
                summary.qps = m2.group(2);
                summary.id = m2.group(3);
            }
            // Time rangeを取得
            Matcher m3 = this.getMatches("# Time range: (.*)", queryBlock, false);
            if (m3.find()) {
                summary.time_range = m3.group(1);
            }
            // 結果メトリクスを取得
            Matcher m4 = this.getMatches("=======\n(# Count.*?)# String:", queryBlock, true);
            if (m4.find()) {
                String metrics = this.makeMetrics(m4.group(1));
                summary.metrics = metrics;
            }
            // Query_timeを取得
            Matcher m5 = this.getMatches("# Query_time distribution\n(.*?)\n([^#]|# Tables)", dump, true);
            if (m5.find()) {
                summary.qtime = m5.group(1).replaceAll("\n", "<br />");
            }

            // SQL文の取得
            String[] rowArray = queryBlock.split("\n");
            summary.query = rowArray[rowArray.length-1];

            // Hostnameの取得
            Matcher m6 = this.getMatches("# Hostname: (.*)", dump, false);
            if (m6.find()) {
                summary.global_hostname = m6.group(1);
            }
            
            // 測定日時の取得
            Matcher m7 = this.getMatches("# Current date: (.*)", dump, false);
            if (m7.find()) {
                summary.global_current_date = m7.group(1);
            }
            
            // 概要の取得
            Matcher m8 = this.getMatches("# Overall: (.*) total, (.*) unique, (.*) QPS, (.*) concurrency.*", dump, false);
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

    private String makeMetrics(String metricsdump) {
        String metrics = "";
        String[] colArray = { "Count", "Exec time", "Lock time", "Rows sent", "Rows examine", "Query size" };
        Integer count = 0;
        Matcher m = this.getMatches("(# .*?)\n", metricsdump, true);
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
    private String readFile() {
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
    
    /**
     * 正規表現による項目抽出を行う
     */
    private Matcher getMatches(String regex, String target, Boolean isMultiline) {
        Pattern pattern;
        if(isMultiline==true) {
            pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        } else {
            pattern = Pattern.compile(regex);
        }
        Matcher match = pattern.matcher(target);
        return match;
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
