package tnk47collection.work2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class MakeRawText implements Runnable {

    static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public static final Pattern TAG_IMG = Pattern.compile("<img src=\"http://stat100.ameba.jp/tnk47/ratio10/illustrations/card/(.*?)\">");
    public static final Pattern TAG_IMG2 = Pattern.compile("(ill_\\d+_[a-z_\\-]+\\d{2})");
    public static final Pattern TAG_REGION = Pattern.compile("<span class=\"regionBackgroundColor .*?\">(.*?)</span>");
    public static final Pattern TAG_REGION2 = Pattern.compile("<em class=\"regionBackgroundColor .*?\">(.*?)</em>");
    public static final Pattern TAG_TYPE = Pattern.compile("<em class=\"themeTxt\">(.*?)</em>");
    public static final Pattern TAG_NAME = Pattern.compile("<dd><span class=\"cardRarity (.*?)\"></span>(.*?)</dd>");

    private final int number;

    public static void main(final String[] args) {
        for (int i = 1; i <= 20614; i++) {
            MakeRawText.executor.execute(new MakeRawText(i));
        }
        MakeRawText.executor.shutdown();
    }

    public MakeRawText(final int number) {
        this.number = number;
    }

    @Override
    public void run() {
        System.out.printf("%s(%d) start\n",
                          this.getClass().getSimpleName(),
                          this.number);
        final String input = String.format("data2/step1/%d.html", this.number);
        final String output = String.format("data2/step2/%d.txt", this.number);

        try {
            final List<String> inputLines = FileUtils.readLines(new File(input));
            final List<String> outputLines = new ArrayList<String>();
            for (final String line : inputLines) {
                String found = line;
                Matcher matcher = null;
                if ((matcher = MakeRawText.TAG_IMG.matcher(found)).find()) {
                    found = matcher.group(1);
                    if ((matcher = MakeRawText.TAG_IMG2.matcher(found)).find()) {
                        outputLines.add(matcher.group(1));
                        System.out.println(matcher.group(1));
                    }
                } else if ((matcher = MakeRawText.TAG_REGION.matcher(found)).find()) {
                    outputLines.add(matcher.group(1));
                    System.out.println(matcher.group(1));
                } else if ((matcher = MakeRawText.TAG_REGION2.matcher(found)).find()) {
                    outputLines.add(matcher.group(1));
                    System.out.println(matcher.group(1));
                } else if ((matcher = MakeRawText.TAG_NAME.matcher(found)).find()) {
                    outputLines.add(matcher.group(1));
                    outputLines.add(matcher.group(2));
                    System.out.println(matcher.group(1));
                    System.out.println(matcher.group(2));
                }
            }
            FileUtils.writeLines(new File(output), outputLines);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        System.out.printf("%s(%d) end\n",
                          this.getClass().getSimpleName(),
                          this.number);
    }

}
