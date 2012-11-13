# Performance Analysis Plugin for Jenkins

#What's this?#

----------
任意のMySQL Slow Query LogのSummaryをJenkinsのレポートとして表示できるプラグインです。



# How to install #

----------

公式プラグインではないので以下の手順でパッケージ化した後、Jenkinsの管理画面からアップロードしてください。

    mvn clean package
    
で.hpiファイルを作成。その後Jenkinsの管理画面＞プラグインの管理＞高度な設定からアップロード。


## Install Percona-Toolkit ##


MySQLのSlow query logのサマリーを取るのに[Perconal-Toolkit](http://www.percona.com/software/percona-toolkit)を使っています。
PerlベースのツールなのでPerlがインストールされていること、またPerl-DBD-MySQL等が必要になります。

    Redhat/CentOS
    rpm -ivh http://www.percona.com/redir/downloads/percona-toolkit/LATEST/percona-toolkit-2.1.5-1.noarch.rpm
    
    Debian
    dpkg -i http://www.percona.com/redir/downloads/percona-toolkit/LATEST/percona-toolkit_2.1.5_all.deb

# How to use #

----------

Percona-Toolkitのpt-query-digestコマンドの結果をファイルに保存してください。
コマンドオプションがいろいろありますが、--explainオプションのみを推奨します。その他のフォーマットの場合、ファイルのパースに失敗する可能性があります。

なおSlow query logを出力したくない場合、tcpdumpの結果を食べさせることもできるようです。

    pt-query-digest --explain /path/to/slow_query_log.log h=localhost,u=username,p=password > result.txt

上記で取得した結果をsftpなどの方法でJenkinsサーバ上の任意の場所へDLしてください。
パーミッションはjenkinsユーザが読み取り可能である必要があります。

Jenkins上で以下の操作を行ってください。

1. プロジェクトの設定画面を開きます。
2. 「ビルド後の処理」のプルダウンからPerformance Analysisを選択
3. pt-query-digestコマンド実行結果が保存されているディレクトリのパスを入力
4. 保存

ビルドを実行すると各回のビルド結果の左メニュー等にPerformance Analysisの解析結果へのリンクが出てきているはず！



# Development #

----------

    mvn hpi:run
でローカルのJettyサーバ上でJenkinsが動作します。http://localhost:8000にアクセスして下さい。

## License ##

----------

@TODO そのうち考える
