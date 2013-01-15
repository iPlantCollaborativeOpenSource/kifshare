# kifshare

A simple web page that allows users to download publicly available files without an iPlant account.

# Prerequisites
You'll need an iRODS install that's of version 3.1 or later. It doesn't need to live on the same box as kifshare, but kifshare will need to be able to connect to it.

You'll want Leiningen 2 installed as well. See http://leiningen.org for more details.

# Configuring kifshare

Create a .properties file using docs/sample.properties as a template.

Worth noting is that the "kifshare.app.external-url" option is used when doing redirects. If you have multiple instances of kifshare set up behind HAProxy, then set this value to the URL that HAProxy is listening on.

# Configuring mod_proxy in Apache for kifshare

All downloads flow through the /d/ endpoint in kifshare. So, to proxy download requests to another box, try the following in one of the httpd config files.

    ProxyPass /quickshare/d http://kifshare-downloads.example.org:31380/d retry=0
    ProxyPassReverse /quickshare/d http://kifshare-downloads.example.org:31380/d

Next, you need to set up ProxyPass entries for the UI:

    ProxyPass /quickshare http://kifshare.example.org:31380 retry=0
    ProxyPassReverse /quickshare http://kifshare.example.org:31380

The entries must be in that order. The lack of trailing slashes is important.

Another potential issue is the "DefaultType" setting in /etc/httpd/conf/httpd.conf. If it's set to text/plain, then kifshare's UI will show up as plain text. If it's set to None, then kifshare's UI works as expected. So, set the DefaultType to None in /etc/httpd/conf/httpd.conf, like in the following:

    DefaultType None

# Running kifshare

If you're working out of a git checkout, then you can run the following:

    lein run --config </path/to/config/file>

If you're running from a jar, then you should be able to run the following:

    java -jar <kifshare-jar> --config </path/to/config>

If you're running it from the RPM with Zookeeper, then run the following:

    sudo /sbin/service kifshare start

# Downloading through a browser

Hit kifshare with a browser, setting the path portion of the URL to the name of the publicly available iRODS ticket.

    http://<kifshare-host>:<kifshare-port>/<ticket-name>

Once the kifshare page comes up, click on the big "Download" button.

# Downloading with curl

You can download with curl in two different ways. Firstly, you can hit the same page that you hit with a browser. Make sure you include -L in the curl options, non-html accepting clients are redirected to the actual download location.

    curl -L http://<kifshare-host>:<kifshare-port>/<ticket-name>

You can also hit another URL, but this one requires you to know the name of the file referred to by the ticket.

    curl http://<kifshare-host>:<kifshare-port>/<ticket-name>/<filename>

If you want to download the download page and not the file, then you need to set the Accept header in the curl command to allow "text/html".

    curl -H "Accept:text/html" http://<kifshare-host>:<kifshare-port>/<ticket-name>





