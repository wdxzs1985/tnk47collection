package tnk47collection;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;

import tnk47collection.work2.DownloadHTML;

import common.CommonHttpClient;

public abstract class HtmlWorker implements Runnable {

    protected final CommonHttpClient httpClient = new CommonHttpClient();
    protected final Log log = LogFactory.getLog(DownloadHTML.class);
    protected final File cookie = new File("cookie");

    protected final int start;
    protected final int end;

    public HtmlWorker(final int start, final int end) {
        this.httpClient.loadCookie(this.cookie);
        this.start = start;
        this.end = end;
    }

    public boolean needLogin() {
        final String html = this.httpGet("/");
        if (StringUtils.contains(html, "<title>Ameba</title>")) {
            return true;
        }
        return false;
    }

    public boolean login() {
        final String url = "https://login.user.ameba.jp/web/login";
        final String username = "amebaxh02";
        final String password = "123456";

        final List<BasicNameValuePair> nvps = new LinkedList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("username", username));
        nvps.add(new BasicNameValuePair("password", password));
        final String html = this.httpClient.post(url, nvps);

        if (StringUtils.isNotBlank(html)) {
            return false;
        }
        return true;
    }

    protected String httpGet(final String path) {
        final String url = this.buildPath(path);
        final String html = this.httpClient.get(url);
        this.httpClient.setReferer(url);
        return html;
    }

    protected String httpPost(final String path, final List<BasicNameValuePair> nvps) {
        final String url = this.buildPath(path);
        final String html = this.httpClient.post(url, nvps);
        this.httpClient.setReferer(url);
        return html;
    }

    private String buildPath(final String path) {
        return "http://tnk47.ameba.jp" + path;
    }
}
