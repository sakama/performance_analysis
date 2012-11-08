# Jenkins-Performance-Test Plugin

#これは何?#

----------
JenkinsとJMeterを使って負荷テストを行うためのプラグインです。
ジョブが実行されるとJMeterから負荷テスト対象サーバにリクエストを送信し、その結果をレポートにまとめて画面に表示します。


# インストール方法 #

----------
## Percona-Toolkitのインストール ##


MySQLのSlow query logのサマリーを取るのに[Perconal-Toolkit](http://www.percona.com/software/percona-toolkit)を使っています。

>**Redhat/CentOS**

>rpm -ivh http://www.percona.com/redir/downloads/percona-toolkit/LATEST/percona-toolkit-2.1.5-1.noarch.rpm

>依存関係のエラーが出た場合、yumで以下をインストール

>yum install PerlDBI-MySQL


>**Debian**

>dpkg -i http://www.percona.com/redir/downloads/percona-toolkit/LATEST/percona-toolkit_2.1.5_all.deb

## 使い方 ##

----------
