package tnk47collection;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;

public class MergeCSV implements Runnable {

	public static void main(String[] args) {
		new MergeCSV().run();
	}

	public static final String DATA_CSV = "data/step3";
	public static final String DATA2_CSV = "data2/step3";
	public static final String OUTPUT = "card.csv";

	@Override
	public void run() {
		final Map<String, String> mergeMap = new HashMap<String, String>();

		try {
			this.readDataIntoMap(mergeMap);
			this.readData2IntoMap(mergeMap);
			FileUtils.writeLines(new File(OUTPUT), mergeMap.values());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readDataIntoMap(Map<String, String> mergeMap)
			throws IOException {
		final Collection<File> inputFiles = FileUtils.listFiles(new File(
				DATA_CSV), FileFileFilter.FILE, null);
		for (final File input : inputFiles) {
			final List<String> inputLines = FileUtils.readLines(input);
			for (String line : inputLines) {
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

				StringBuilder sb = new StringBuilder();
				sb.append(name).append(",");
				sb.append(region).append(",");
				sb.append(pref).append(",");
				sb.append(rarilites).append(",");
				sb.append(type).append(",");
				sb.append(ill).append(",");
				sb.append(3);

				mergeMap.put(name, sb.toString());
			}
		}
	}

	private void readData2IntoMap(Map<String, String> mergeMap)
			throws IOException {
		final Collection<File> inputFiles = FileUtils.listFiles(new File(
				DATA2_CSV), FileFileFilter.FILE, null);

		Map<String, String> regionMap = this.getRegionMap();
		for (final File input : inputFiles) {
			final List<String> inputLines = FileUtils.readLines(input);
			for (String line : inputLines) {
				String[] prop = StringUtils.splitPreserveAllTokens(line, ",");
				String name = prop[3];
				String pref = prop[1];
				String region = regionMap.get(prop[1]);
				String rarilites = prop[2];
				rarilites = StringUtils.replace(rarilites, "ssrare", "SSR");
				rarilites = StringUtils.replace(rarilites, "srare", "SR");
				rarilites = StringUtils.replace(rarilites, "hrare", "HR");
				final String type = "";
				String ill = prop[0];

				StringBuilder sb = new StringBuilder();
				sb.append(name).append(",");
				sb.append(region).append(",");
				sb.append(pref).append(",");
				sb.append(rarilites).append(",");
				sb.append(type).append(",");
				sb.append(ill).append(",");
				sb.append(3);

				mergeMap.put(name, sb.toString());
			}
		}
	}

	private Map<String, String> getRegionMap() {
		Map<String, String> regionMap = new HashMap<String, String>();
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
