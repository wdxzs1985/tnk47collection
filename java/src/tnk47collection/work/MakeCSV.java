package tnk47collection.work;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class MakeCSV implements Runnable {

    public static final Pattern PATTERN_IMG = Pattern.compile("(ill_\\d+_[a-z_\\-]+\\d{2})");

    private int number;

    public MakeCSV(final int number) {
        this.number = number;
    }

    @Override
    public void run() {
        System.out.printf("%s(%d) start\n",
                          this.getClass().getSimpleName(),
                          this.number);

        final String input = String.format("data/step2/%d.txt", this.number);
        final String output = String.format("data/step3/%d.csv", this.number);

        try {
            final List<String> inputLines = FileUtils.readLines(new File(input));
            final List<String> outputLines = new ArrayList<String>();
            StringWriter stringWriter = null;
            for (int i = 0; i < inputLines.size(); i++) {
                final String line = inputLines.get(i);
                switch (i % 8) {
                case 0:
                    if (stringWriter != null) {
                        outputLines.add(stringWriter.toString());
                    }
                    stringWriter = new StringWriter();
                    stringWriter.append(line);
                    break;
                default:
                    stringWriter.append("," + line);
                    break;
                }
            }

            FileUtils.writeLines(new File(output), outputLines);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.printf("%s(%d) end\n",
                          this.getClass().getSimpleName(),
                          this.number);
    }

}
