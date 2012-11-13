# Performance Analysis Plugin for Jenkins

#What's this?#

----------
任意のMySQL Slow Query LogのSummaryをJenkinsのレポートとして表示できるプラグインです。


# How to install #

----------

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



# Development #

----------

    mvn hpi:run
でローカルのJettyサーバ上でJenkinsが動作します。http://localhost:8000にアクセスして下さい。

## License ##

----------

@TODO そのうち考える
