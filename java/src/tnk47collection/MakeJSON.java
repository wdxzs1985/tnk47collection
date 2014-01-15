package tnk47collection;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class MakeJSON implements Runnable {

    public static final Pattern ILL_PATTERN = Pattern.compile("ill_(\\d+)\\d_(.*?)\\d{2}");
    public static final String INPUT = "card.csv";
    public static final String OUTPUT = "card.json";

    public static void main(String[] args) {
        new MakeJSON().run();
    }

    @Override
    public void run() {
        System.out.printf("%s start\n", this.getClass().getSimpleName());

        try {
            final List<String> mergeLines = FileUtils.readLines(new File(INPUT));

            final JSONArray outputList = new JSONArray();
            for (final String line : mergeLines) {
                final String[] prop = StringUtils.splitPreserveAllTokens(line,
                                                                         ",");
                final String name = prop[0];
                final String region = prop[1];
                final String pref = prop[2];
                final String rarilites = prop[3];
                final String type = prop[4];
                final String ill = prop[5];
                final int evo1 = Integer.valueOf(prop[6]);
                final int evo2 = Integer.valueOf(prop[7]);

                JSONObject card = new JSONObject();
                card.put("name", name);
                card.put("region", StringUtils.defaultIfBlank(region, "不明"));
                card.put("pref", StringUtils.defaultIfBlank(pref, "不明"));
                card.put("rarilites", rarilites);
                card.put("type", StringUtils.defaultIfBlank(type, "不明"));

                if (StringUtils.isNotBlank(ill)) {
                    JSONArray illList = new JSONArray();
                    Matcher illMatcher = ILL_PATTERN.matcher(ill);
                    if (illMatcher.find()) {
                        String number = illMatcher.group(1);
                        String roma = illMatcher.group(2);
                        for (int i = 1; i <= evo1; i++) {
                            illList.add(String.format("ill_%s%d_%s0%d",
                                                      number,
                                                      i,
                                                      roma,
                                                      evo2 + i - 1));
                        }
                        card.put("ill", illList);
                    }

                }
                outputList.add(card);
            }

            FileUtils.write(new File(OUTPUT),
                            "var cards = " + outputList.toString());
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.printf("%s end\n", this.getClass().getSimpleName());
    }
}
