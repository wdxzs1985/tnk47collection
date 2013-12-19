package tnk47collection.work3;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;

import common.CommonHttpClient;
import common.SystemConstants;

public class DownloadHTML implements Runnable {
    private final CommonHttpClient httpClient = new CommonHttpClient();
    private final int start;
    private final int end;
    private final Log log = LogFactory.getLog(DownloadHTML.class);
    private final File cookie = new File("cookie");

    public static void main(final String[] args) {
        final DownloadHTML worker = new DownloadHTML(80, 800);
        worker.run();
    }

    public DownloadHTML(final int start, final int end) {
        this.httpClient.loadCookie(this.cookie);
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        if (this.needLogin()) {
            if (this.login()) {
                this.log.info("login ok");
            } else {
                return;
            }
        }
        this.httpGet("/mypage");
        for (int i = this.start; i <= this.end; i++) {
            final String html = this.httpGet(String.format("/information?informationId=%d",
                                                           i));
            final String output = String.format("data3/step1/%d.html", i);
            if (!StringUtils.contains(html, "ページが表示できませんでした。ごめんなさい。")) {
                try {
                    final File file = new File(output);
                    FileUtils.write(file, html, SystemConstants.ENCODING);
                } catch (final IOException e) {
                    this.log.error(e.getMessage(), e);
                }
            }
            try {
                final int sleepTime = 1000 + RandomUtils.nextInt(1000);
                Thread.sleep(sleepTime);
            } catch (final InterruptedException e) {
            }
        }
        this.httpClient.saveCookie(this.cookie);
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
        final String username = "bushing2";
        final String password = "wdxzs1985";

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

    protected String httpPost(final String path,
                              final List<BasicNameValuePair> nvps) {
        final String url = this.buildPath(path);
        final String html = this.httpClient.post(url, nvps);
        this.httpClient.setReferer(url);
        return html;
    }

    private String buildPath(final String path) {
        return "http://tnk47.ameba.jp" + path;
    }

}
