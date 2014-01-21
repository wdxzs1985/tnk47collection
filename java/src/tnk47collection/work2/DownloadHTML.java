package tnk47collection.work2;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import tnk47collection.HtmlWorker;

import common.SystemConstants;

public class DownloadHTML extends HtmlWorker {

    public static void main(final String[] args) {
        // 2013/12/18 last:8720
        final DownloadHTML worker = new DownloadHTML(10701, 10900);
        worker.run();
    }

    public DownloadHTML(int i, int j) {
        super(i, j);
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
        for (int i = this.start; i <= this.end; i += 1) {
            final String html = this.httpGet(String.format("/gacha/gacha-detail?gachaId=%d",
                                                           i));
            final String output = String.format("data2/step1/%d.html", i);
            if (StringUtils.contains(html, "ページが表示できませんでした。ごめんなさい。")) {
                this.log.warn("ページが表示できませんでした。");
            } else {
                try {
                    final File file = new File(output);
                    FileUtils.write(file, html, SystemConstants.ENCODING);
                } catch (final IOException e) {
                    this.log.error(e.getMessage(), e);
                }
            }
            try {
                final int sleepTime = 1000 + RandomUtils.nextInt(5000);
                Thread.sleep(sleepTime);
            } catch (final InterruptedException e) {
            }
        }
        this.httpClient.saveCookie(this.cookie);
    }

}
