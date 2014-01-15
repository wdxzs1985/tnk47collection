package tnk47collection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;

public class MergeCSV implements Runnable {

    public static void main(final String[] args) {
        new MergeCSV().run();
    }

    public static final String PRE_CSV = "pre.csv";
    public static final String DATA_CSV = "data/step3";
    public static final String DATA2_CSV = "data2/step3";
    public static final String DATA3_CSV = "data3/step2";
    public static final String OUTPUT = "card.csv";

    private static final Pattern ILL_PATTERN = Pattern.compile("ill_(\\d+)\\d_.*");

    @Override
    public void run() {
        final Map<Integer, String> mergeMap = new HashMap<Integer, String>();

        try {
            this.readPreDataIntoMap(mergeMap);
            this.readLastDataIntoMap(mergeMap);
            this.readDataIntoMap(mergeMap);
            this.readData3IntoMap(mergeMap);
            this.readData2IntoMap(mergeMap);

            final List<String> sortList = new ArrayList<String>();
            sortList.addAll(mergeMap.values());
            final Map<String, Integer> rarityMap = this.getRarityMap();
            final Comparator<String> c = new Comparator<String>() {

                @Override
                public int compare(final String o1, final String o2) {
                    return this.rank(o2) - this.rank(o1);
                }

                private int rank(String o) {
                    final String[] prop = StringUtils.splitPreserveAllTokens(o,
                                                                             ",");
                    int r = rarityMap.get(prop[3]);
                    int n = MergeCSV.this.getIllNo(prop[5]);
                    return r * 100000000 + n;
                }
            };
            Collections.sort(sortList, c);
            FileUtils.writeLines(new File(MergeCSV.OUTPUT), sortList);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    protected int getIllNo(String ill) {
        int illNo = 0;
        final Matcher m = MergeCSV.ILL_PATTERN.matcher(ill);
        if (m.find()) {
            illNo = Integer.valueOf(m.group(1));
        }
        return illNo;
    }

    private void readLastDataIntoMap(Map<Integer, String> mergeMap) throws IOException {
        final List<String> inputLines = FileUtils.readLines(new File(MergeCSV.OUTPUT));
        for (final String line : inputLines) {
            final String[] prop = StringUtils.splitPreserveAllTokens(line, ",");

            final String name = prop[0];
            final String region = prop[1];
            final String pref = prop[2];
            final String rarity = prop[3];
            final String type = prop[4];
            final String ill = prop[5];
            final String evo1 = prop[6];
            this.putToMergeMap(mergeMap,
                               name,
                               region,
                               pref,
                               rarity,
                               type,
                               ill,
                               evo1,
                               "1");

        }
    }

    private void readPreDataIntoMap(final Map<Integer, String> mergeMap) throws IOException {
        final List<String> inputLines = FileUtils.readLines(new File(MergeCSV.PRE_CSV));
        for (final String line : inputLines) {
            final String[] prop = StringUtils.splitPreserveAllTokens(line, ",");

            final String name = prop[0];
            final String region = prop[1];
            final String pref = prop[2];
            final String rarity = prop[3];
            final String type = prop[4];
            final String ill = prop[5];
            final String evo1 = prop[6];
            final String evo2 = prop[7];
            this.putToMergeMap(mergeMap,
                               name,
                               region,
                               pref,
                               rarity,
                               type,
                               ill,
                               evo1,
                               evo2);

        }
    }

    private void putToMergeMap(final Map<Integer, String> mergeMap, final String name, final String region, String pref, String rarilites, String type, String ill, String evo1, String evo2) {
        int illNo = this.getIllNo(ill);
        if (illNo > 0 && !mergeMap.containsKey(illNo)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(name).append(",");
            sb.append(region).append(",");
            sb.append(pref).append(",");
            sb.append(rarilites).append(",");
            sb.append(type).append(",");
            sb.append(ill).append(",");
            sb.append(evo1).append(",");
            sb.append(evo2);
            mergeMap.put(illNo, sb.toString());
        }
    }

    private void readDataIntoMap(final Map<Integer, String> mergeMap) throws IOException {
        final Collection<File> inputFiles = FileUtils.listFiles(new File(MergeCSV.DATA_CSV),
                                                                FileFileFilter.FILE,
                                                                null);
        for (final File input : inputFiles) {
            final List<String> inputLines = FileUtils.readLines(input);
            for (final String line : inputLines) {
                final String[] prop = StringUtils.splitPreserveAllTokens(line,
                                                                         ",");
                final String ill = prop[0];
                final String name = prop[1];
                final String region = prop[3];
                final String pref = prop[4];
                final String type = prop[5];
                String rarilites = prop[7];
                rarilites = StringUtils.replace(rarilites, "SSレア", "SSR");
                rarilites = StringUtils.replace(rarilites, "Sレア", "SR");
                rarilites = StringUtils.replace(rarilites, "ハイレア", "HR");
                rarilites = StringUtils.replace(rarilites, "レア", "R");
                rarilites = StringUtils.replace(rarilites, "ハイノーマル", "HN");
                rarilites = StringUtils.replace(rarilites, "ノーマル", "N");
                rarilites = StringUtils.replace(rarilites, "スペシャル", "SP");

                this.putToMergeMap(mergeMap,
                                   name,
                                   region,
                                   pref,
                                   rarilites,
                                   type,
                                   ill,
                                   "3",
                                   "1");
            }
        }
    }

    private void readData2IntoMap(final Map<Integer, String> mergeMap) throws IOException {
        final Collection<File> inputFiles = FileUtils.listFiles(new File(MergeCSV.DATA2_CSV),
                                                                FileFileFilter.FILE,
                                                                null);

        final Map<String, String> regionMap = this.getRegionMap();
        for (final File input : inputFiles) {
            final List<String> inputLines = FileUtils.readLines(input);
            for (final String line : inputLines) {
                final String[] prop = StringUtils.splitPreserveAllTokens(line,
                                                                         ",");
                final String ill = prop[0];
                final String pref = prop[1];
                final String region = regionMap.get(pref);
                String rarity = prop[2];
                rarity = StringUtils.replace(rarity, "ssrare", "SSR");
                rarity = StringUtils.replace(rarity, "srare", "SR");
                rarity = StringUtils.replace(rarity, "hrare", "HR");
                final String name = prop[3];
                final String type = "";

                this.putToMergeMap(mergeMap,
                                   name,
                                   region,
                                   pref,
                                   rarity,
                                   type,
                                   ill,
                                   "3",
                                   "1");
            }
        }
    }

    private void readData3IntoMap(final Map<Integer, String> mergeMap) throws IOException {
        final Collection<File> inputFiles = FileUtils.listFiles(new File(MergeCSV.DATA3_CSV),
                                                                FileFileFilter.FILE,
                                                                null);
        for (final File input : inputFiles) {
            final List<String> inputLines = FileUtils.readLines(input);
            for (final String line : inputLines) {
                final String[] prop = StringUtils.splitPreserveAllTokens(line,
                                                                         ",");

                final String name = prop[0];
                final String region = prop[1];
                final String pref = prop[2];
                final String rarity = prop[3];
                final String type = prop[4];
                final String ill = prop[5];

                this.putToMergeMap(mergeMap,
                                   name,
                                   region,
                                   pref,
                                   rarity,
                                   type,
                                   ill,
                                   "3",
                                   "1");
            }
        }
    }

    private Map<String, Integer> getRarityMap() {
        final Map<String, Integer> rarityMap = new HashMap<String, Integer>();
        rarityMap.put("SSR", 7);
        rarityMap.put("SR", 6);
        rarityMap.put("HR", 5);
        rarityMap.put("R", 4);
        rarityMap.put("HN", 3);
        rarityMap.put("N", 2);
        rarityMap.put("SP", 1);
        return rarityMap;
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
