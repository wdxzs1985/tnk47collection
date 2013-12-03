package tnk47collection.work;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class MakeRawText implements Runnable {

    public static final Pattern TAG_TD = Pattern.compile("<td>(.*)</td>");
    public static final Pattern TAG_IMG = Pattern.compile("<img .* src=\"(.*)\" .* />");
    public static final Pattern TAG_IMG2 = Pattern.compile("(ill_\\d+_[a-z_\\-]+\\d{2})");
    public static final Pattern TAG_A = Pattern.compile("<a .*>(.*)</a>");

    private int number;

    public MakeRawText(final int number) {
        this.number = number;
    }

    @Override
    public void run() {
        System.out.printf("%s(%d) start\n",
                          this.getClass().getSimpleName(),
                          this.number);

        final String input = String.format("data/step1/%d.html", this.number);
        final String output = String.format("data/step2/%d.txt", this.number);

        try {
            final List<String> inputLines = FileUtils.readLines(new File(input));
            final List<String> outputLines = new ArrayList<String>();
            for (final String line : inputLines) {
                final Matcher matcher = MakeRawText.TAG_TD.matcher(line);
                if (matcher.find()) {
                    String found = matcher.group(1);
                    final Matcher matcher2 = MakeRawText.TAG_IMG.matcher(found);
                    if (matcher2.find()) {
                        found = matcher2.group(1);
                        final Matcher matcher3 = MakeRawText.TAG_IMG2.matcher(found);
                        if (matcher3.find()) {
                            found = matcher3.group(1);
                        }
                    } else {
                        final Matcher matcher3 = MakeRawText.TAG_A.matcher(found);
                        if (matcher3.find()) {
                            found = matcher3.group(1);
                        }
                    }
                    outputLines.add(found);
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
