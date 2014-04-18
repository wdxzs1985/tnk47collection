package tnk47collection.work2;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class MakeCSV implements Runnable {

    static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    public static final Pattern PATTERN_IMG = Pattern.compile("(ill_\\d+_[a-z_\\-]+\\d{2})");

    private final int number;

    public static void main(final String[] args) {
        for (int i = 1; i <= 20614; i++) {
            MakeCSV.executor.execute(new MakeCSV(i));
        }
        MakeCSV.executor.shutdown();
    }

    public MakeCSV(final int number) {
        this.number = number;
    }

    @Override
    public void run() {
        System.out.printf("%s(%d) start\n",
                          this.getClass().getSimpleName(),
                          this.number);
        final String input = String.format("data2/step2/%d.txt", this.number);
        final String output = String.format("data2/step3/%d.csv", this.number);

        try {
            final List<String> inputLines = FileUtils.readLines(new File(input));
            final List<String> outputLines = new ArrayList<String>();
            StringWriter stringWriter = null;
            for (int i = 0; i < inputLines.size(); i++) {
                final String line = inputLines.get(i);
                final Matcher imgMatcher = MakeCSV.PATTERN_IMG.matcher(line);
                if (imgMatcher.find()) {
                    if (stringWriter != null) {
                        outputLines.add(stringWriter.toString());
                    }
                    stringWriter = new StringWriter();
                    stringWriter.append(line);
                    stringWriter.append(",").append(inputLines.get(i + 1));
                    stringWriter.append(",").append(inputLines.get(i + 2));
                    stringWriter.append(",").append(inputLines.get(i + 3));
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
