package tnk47collection.work3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;

import common.CommonHttpClient;

public class DownloadHTML implements Runnable {
    private final CommonHttpClient httpClient = new CommonHttpClient();
    private final int start;
    private final int end;
    private final Log log = LogFactory.getLog(DownloadHTML.class);
    private final File cookie = new File("cookie");

    private final Pattern cardPattern = Pattern.compile("<option value=\"(.*?)\"data-image-path=\"/illustrations/card/(ill_\\d+_.*?0\\d).jpg\\?[\\d]{8}-\\d\"data-rarity-code=\"([a-z]+)\"data-region-code=\"[a-z]+\"data-region-name=\"(.*?)\"data-pref-name=\"(.*?)\"data-max-attack=\"\\d+\"data-max-defence=\"\\d+\"data-max-cost=\"\\d+\"data-min-cost=\"\\d+\"data-card-type=\"(.*?)\"data-skill-name=\".*?\"data-skill-description=\".*?\"data-effect-class=\".*?\">.*?</option>");

    public static void main(final String[] args) {
        final DownloadHTML worker = new DownloadHTML(1, 811);
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
            final List<String> list = new ArrayList<String>();
            final String html = this.httpGet(String.format("/information?informationId=%d",
                                                           i));
            final Matcher matcher = this.cardPattern.matcher(html);
            while (matcher.find()) {
                final String name = matcher.group(1);
                final String ill = matcher.group(2);
                String rarityCode = matcher.group(3);
                final String region = matcher.group(4);
                final String pref = matcher.group(5);
                final String type = matcher.group(6);
                rarityCode = StringUtils.replace(rarityCode, "ssrare", "SSR");
                rarityCode = StringUtils.replace(rarityCode, "srare", "SR");
                rarityCode = StringUtils.replace(rarityCode, "hrare", "HR");

                final StringBuilder sb = new StringBuilder();
                sb.append(name).append(",");
                sb.append(region).append(",");
                sb.append(pref).append(",");
                sb.append(rarityCode).append(",");
                sb.append(type).append(",");
                sb.append(ill).append(",");
                sb.append(3);

                list.add(sb.toString());
            }

            if (CollectionUtils.isNotEmpty(list)) {
                final String output = String.format("data3/step2/%d.csv", i);
                try {
                    FileUtils.writeLines(new File(output), list);
                } catch (final IOException e) {
                    e.printStackTrace();
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
