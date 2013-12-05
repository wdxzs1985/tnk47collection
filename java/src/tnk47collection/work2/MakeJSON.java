package tnk47collection.work2;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;

public class MakeJSON implements Runnable {

    public static final String INPUT_DIR = "data2/step3";
    public static final String OUTPUT = "data2/step4/card.json";

    public static void main(final String[] args) {
        new MakeJSON().run();
    }

    @Override
    public void run() {
        final Map<String, String> regionMap = this.getRegionMap();

        System.out.printf("%s start\n", this.getClass().getSimpleName());

        final Set<String> mergeLines = new HashSet<String>();

        try {
            final Collection<File> inputFiles = FileUtils.listFiles(new File(MakeJSON.INPUT_DIR),
                                                                    FileFileFilter.FILE,
                                                                    null);
            for (final File input : inputFiles) {
                final List<String> inputLines = FileUtils.readLines(input);
                mergeLines.addAll(inputLines);
            }

            final Map<String, JSONObject> mapping = new HashMap<String, JSONObject>();
            for (final String line : mergeLines) {
                final String[] prop = StringUtils.splitPreserveAllTokens(line,
                                                                         ",");
                final String ill = prop[0];
                final String pref = prop[1];
                final String region = regionMap.get(prop[1]);
                String rarilites = prop[2];
                final String name = prop[3];
                rarilites = StringUtils.replace(rarilites, "ssrare", "SSR");
                rarilites = StringUtils.replace(rarilites, "srare", "SR");
                rarilites = StringUtils.replace(rarilites, "hrare", "HR");

                JSONObject card = null;
                if (mapping.containsKey(name)) {
                    card = mapping.get(name);
                    final JSONArray illList = card.getJSONArray("ill");
                    illList.add(ill);
                } else {
                    card = new JSONObject();
                    card.put("name", name);
                    card.put("rarilites", rarilites);
                    card.put("region", StringUtils.defaultIfBlank(region, "不明"));
                    card.put("pref", StringUtils.defaultIfBlank(pref, "不明"));
                    card.put("type", "不明");

                    final JSONArray illList = new JSONArray();
                    illList.add(ill);
                    card.put("ill", illList);

                    mapping.put(name, card);
                }
            }

            final JSONArray outputList = new JSONArray();
            outputList.addAll(mapping.values());

            FileUtils.write(new File(MakeJSON.OUTPUT),
                            "var cards = " + outputList.toString());
        } catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.printf("%s end\n", this.getClass().getSimpleName());
    }

    private Map<String, String> getRegionMap() {
        final Map<String, String> regionMap = new HashMap<String, String>();
        regionMap.put("無所属", "無所属");
        //
        regionMap.put("北海道・東北", "北海道・東北");
        regionMap.put("青森", "北海道・東北");
        regionMap.put("岩手", "北海道・東北");
        regionMap.put("北海道", "北海道・東北");
        regionMap.put("秋田", "北海道・東北");
        regionMap.put("山形", "北海道・東北");
        regionMap.put("宮城", "北海道・東北");
        regionMap.put("福島", "北海道・東北");
        // 中部
        regionMap.put("中部", "中部");
        regionMap.put("愛知", "中部");
        regionMap.put("中部", "中部");
        regionMap.put("新潟", "中部");
        regionMap.put("山梨", "中部");
        regionMap.put("静岡", "中部");
        regionMap.put("岐阜", "中部");
        regionMap.put("長野", "中部");
        regionMap.put("石川", "中部");
        regionMap.put("富山", "中部");
        // 中国・四国
        regionMap.put("中国・四国", "中国・四国");
        regionMap.put("山口", "中国・四国");
        regionMap.put("徳島", "中国・四国");
        regionMap.put("高知", "中国・四国");
        regionMap.put("愛媛", "中国・四国");
        regionMap.put("岡山", "中国・四国");
        regionMap.put("愛媛", "中国・四国");
        regionMap.put("鳥取", "中国・四国");
        regionMap.put("香川", "中国・四国");
        regionMap.put("広島", "中国・四国");
        regionMap.put("島根", "中国・四国");
        // 関東
        regionMap.put("関東", "関東");
        regionMap.put("栃木", "関東");
        regionMap.put("群馬", "関東");
        regionMap.put("茨城", "関東");
        regionMap.put("埼玉", "関東");
        regionMap.put("東京", "関東");
        regionMap.put("神奈川", "関東");
        // 近畿
        regionMap.put("近畿", "近畿");
        regionMap.put("奈良", "近畿");
        regionMap.put("大阪", "近畿");
        regionMap.put("京都", "近畿");
        regionMap.put("滋賀", "近畿");
        regionMap.put("福井", "近畿");
        regionMap.put("三重", "近畿");
        regionMap.put("和歌山", "近畿");
        regionMap.put("兵庫", "近畿");
        // 九州・沖縄
        regionMap.put("九州・沖縄", "九州・沖縄");
        regionMap.put("熊本", "九州・沖縄");
        regionMap.put("九州", "九州・沖縄");
        regionMap.put("佐賀", "九州・沖縄");
        regionMap.put("長崎", "九州・沖縄");
        regionMap.put("鹿児島", "九州・沖縄");
        regionMap.put("宮崎", "九州・沖縄");
        regionMap.put("大分", "九州・沖縄");
        //

        return regionMap;
    }
}
