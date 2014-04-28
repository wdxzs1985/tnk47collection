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
        final DownloadHTML worker = new DownloadHTML(Work2Constants.LAST_ID,
                                                     Work2Constants.NEXT_ID);
        worker.run();
    }

    public DownloadHTML(final int i, final int j) {
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
        int i = this.start;
        while (i < this.end) {
            // for (int i = this.start; i <= this.end; i += 10) {
            final String html = this.httpGet(String.format("/gacha/gacha-detail?gachaId=%d",
                                                           i));
            final String output = String.format("data2/step1/%d.html", i);
            if (StringUtils.contains(html, "ページが表示できませんでした。ごめんなさい。")) {
                this.log.warn("ページが表示できませんでした。");
                i += 10;
            } else {
                i++;
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
