package tnk47collection.work3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import tnk47collection.HtmlWorker;

public class DownloadHTML extends HtmlWorker {

    private final Pattern cardPattern = Pattern.compile("<option value=\"(.*?)\"data-image-path=\"/illustrations/card/(ill_\\d+_.*?0\\d).jpg\\?[\\d]{8}-.*?\"data-rarity-code=\"([a-z]+)\"data-region-code=\"[a-z]+\"data-region-name=\"(.*?)\"data-pref-name=\"(.*?)\"data-max-attack=\"\\d+\"data-max-defence=\"\\d+\"data-max-cost=\"\\d+\"data-min-cost=\"\\d+\"data-card-type=\"(.*?)\"data-skill-name=\".*?\"data-skill-description=\".*?\"data-effect-class=\".*?\">.*?</option>");

    public static void main(final String[] args) {
        final DownloadHTML worker = new DownloadHTML(820, 950);
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

        for (int i = this.start; i <= this.end; i++) {
            final List<String> list = new ArrayList<String>();
            String url = String.format("/information?informationId=%d", i);
            this.log.debug(url);
            final String html = this.httpGet(url);
            if (StringUtils.contains(html, "ページが表示できませんでした。ごめんなさい。")) {
                this.log.warn("ページが表示できませんでした。");
            } else {

                final Matcher matcher = this.cardPattern.matcher(html);
                while (matcher.find()) {
                    final String name = matcher.group(1);
                    final String ill = matcher.group(2);
                    String rarityCode = matcher.group(3);
                    final String region = matcher.group(4);
                    final String pref = matcher.group(5);
                    final String type = matcher.group(6);
                    rarityCode = StringUtils.replace(rarityCode,
                                                     "ssrare",
                                                     "SSR");
                    rarityCode = StringUtils.replace(rarityCode, "srare", "SR");
                    rarityCode = StringUtils.replace(rarityCode, "hrare", "HR");
                    rarityCode = StringUtils.replace(rarityCode, "rare", "R");
                    rarityCode = StringUtils.replace(rarityCode,
                                                     "special",
                                                     "SP");

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
